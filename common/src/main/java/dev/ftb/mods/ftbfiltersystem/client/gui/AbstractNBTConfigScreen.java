package dev.ftb.mods.ftbfiltersystem.client.gui;

import dev.ftb.mods.ftbfiltersystem.api.FilterException;
import dev.ftb.mods.ftbfiltersystem.api.client.gui.AbstractFilterScreen;
import dev.ftb.mods.ftbfiltersystem.filter.NbtFilter;
import net.minecraft.ChatFormatting;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

import java.util.Objects;
import java.util.function.Predicate;

public abstract class AbstractNBTConfigScreen<T extends NbtFilter> extends AbstractItemEditorConfigScreen<T> implements GhostDropReceiver {
    public AbstractNBTConfigScreen(T filter, AbstractFilterScreen parentScreen) {
        super(filter, parentScreen, 320, 176);
    }

    @Override
    protected void doScheduledUpdate() {
        if (editBox.getValue().isEmpty()) {
            statusMsg = Component.empty();
        } else {
            try {
                NbtFilter.fromString(filter.getParent(), editBox.getValue());
                statusMsg = Component.translatable("ftbfiltersystem.gui.nbt_ok").withStyle(ChatFormatting.DARK_GREEN);
            } catch (FilterException e) {
                statusMsg = Component.translatable("ftbfiltersystem.gui.nbt_bad").withStyle(ChatFormatting.DARK_RED);
            }
        }
    }

    @Override
    protected Predicate<ItemStack> inventoryChecker() {
        return ItemStack::hasTag;
    }

    @Override
    protected String serialize(ItemStack stack) {
        return Objects.requireNonNull(stack.getTag()).toString();
    }

    @Override
    public Rect2i getGhostDropRegion() {
        return new Rect2i(editBox.getX(), editBox.getY(), editBox.getWidth(), editBox.getHeight());
    }

    @Override
    public void receiveGhostDrop(ItemStack stack) {
        if (stack.hasTag()) {
            editBox.setValue(serialize(stack));
            customHoverName = stack.hasCustomHoverName() ? stack.getHoverName() : null;
            setFocused(editBox);
        }
    }
}
