package dev.ftb.mods.ftbfiltersystem.client.gui;

import dev.ftb.mods.ftbfiltersystem.api.FilterException;
import dev.ftb.mods.ftbfiltersystem.api.client.gui.AbstractFilterScreen;
import dev.ftb.mods.ftbfiltersystem.client.FTBFilterSystemClient;
import dev.ftb.mods.ftbfiltersystem.filter.ComponentFilter;
import dev.ftb.mods.ftbfiltersystem.util.PlatformUtil;
import net.minecraft.client.gui.components.Checkbox;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.function.Predicate;

public class ComponentConfigScreen extends AbstractItemEditorConfigScreen<ComponentFilter> implements GhostDropReceiver {
    private Checkbox fuzzyCB;

    public ComponentConfigScreen(ComponentFilter filter, AbstractFilterScreen parentScreen) {
        super(filter, parentScreen, 320, 176);
    }

    @Override
    protected void init() {
        super.init();

        Component str = Component.translatable("ftbfiltersystem.gui.fuzzy_match").withColor(0xFF404040).withoutShadow();
        fuzzyCB = addRenderableWidget(Checkbox.builder(str, font).pos(leftPos + 180, topPos + 110).maxWidth(font.width(str)).selected(filter.isFuzzyMatch()).build());
//        fuzzyCB = addRenderableWidget(new CustomCheckbox(leftPos + 180, topPos + 110, font.width(str), 20, str, filter.isFuzzyMatch()));

        try {
            editBox.setValue(filter.getStringArgWithoutPrefix(FTBFilterSystemClient.registryAccess()));
        } catch (IllegalStateException ignored) {
        }
    }

    @Override
    protected void doScheduledUpdate() {
        if (editBox.getValue().isEmpty()) {
            setStatus(true, Component.empty(), null);
        } else {
            try {
                ComponentFilter.fromString(filter.getParent(), editBox.getValue(), FTBFilterSystemClient.registryAccess());
                setStatus(true, Component.translatable("ftbfiltersystem.gui.nbt_ok"), null);
            } catch (FilterException e) {
                setStatus(false, Component.translatable("ftbfiltersystem.gui.nbt_bad"), e.getMessage());
            }
        }
    }

    @Override
    protected @Nullable ComponentFilter makeNewFilter() {
        try {
            String str = ComponentFilter.getPrefixStr(fuzzyCB.selected()) + editBox.getValue();
            return ComponentFilter.fromString(filter.getParent(), str, FTBFilterSystemClient.registryAccess());
        } catch (FilterException e) {
            return null;
        }
    }

    @Override
    protected Predicate<ItemStack> inventoryChecker() {
        return PlatformUtil::hasComponentPatch;
    }

    @Override
    protected String serialize(ItemStack stack) {
        Tag res = ItemStack.CODEC.encodeStart(FTBFilterSystemClient.registryAccess().createSerializationContext(NbtOps.INSTANCE), stack).getOrThrow();
        if (res instanceof CompoundTag c && c.contains("components")) {
            //noinspection DataFlowIssue
            return c.get("components").toString();
        }
        return "";
    }

    @Override
    public Rect2i getGhostDropRegion() {
        return new Rect2i(editBox.getX(), editBox.getY(), editBox.getWidth(), editBox.getHeight());
    }

    @Override
    public void receiveGhostDrop(ItemStack stack) {
        if (PlatformUtil.hasComponentPatch(stack)) {
            editBox.setValue(serialize(stack));
            customHoverName = stack.getOrDefault(DataComponents.CUSTOM_NAME, null);
            setFocused(editBox);
        }
    }
}
