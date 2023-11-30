package dev.ftb.mods.ftbfiltersystem.client.gui;

import dev.ftb.mods.ftbfiltersystem.api.FTBFilterSystemAPI;
import dev.ftb.mods.ftbfiltersystem.api.FilterException;
import dev.ftb.mods.ftbfiltersystem.api.client.gui.AbstractFilterScreen;
import dev.ftb.mods.ftbfiltersystem.client.FTBFilterSystemClient;
import dev.ftb.mods.ftbfiltersystem.client.gui.widget.CustomStringWidget;
import dev.ftb.mods.ftbfiltersystem.filter.ExpressionFilter;
import dev.ftb.mods.ftbfiltersystem.registry.item.SmartFilterItem;
import dev.ftb.mods.ftbfiltersystem.util.FilterParser;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.function.Predicate;

public class ExpressionConfigScreen extends AbstractItemEditorConfigScreen<ExpressionFilter> {
    private EditBox customEditBox;

    public ExpressionConfigScreen(ExpressionFilter filter, AbstractFilterScreen parentScreen) {
        super(filter, parentScreen, 320, 176);
    }

    @Override
    protected void init() {
        super.init();

        editBox.setValue(filter.getExpression());

        Component text = Component.translatable("ftbfiltersystem.gui.custom_name");
        addRenderableWidget(CustomStringWidget.withText(leftPos + 180, topPos + 110, text, font).setColor(0x404040));
        customEditBox = addRenderableWidget(new EditBox(font, leftPos + 180, topPos + 110 + font.lineHeight + 4, guiWidth - 190, font.lineHeight + 6, Component.empty()));
        customEditBox.setValue(filter.getCustomName());
    }

    @Override
    protected @Nullable ExpressionFilter makeNewFilter() {
        try {
            String str = customEditBox.getValue().isEmpty() ? editBox.getValue() : editBox.getValue() + "/" + customEditBox.getValue();
            return new ExpressionFilter(filter.getParent(), str);
        } catch (FilterException e) {
            return null;
        }
    }

    @Override
    protected void onItemWidgetClicked() {
        customEditBox.setValue(customHoverName == null ? "" : customHoverName.getString());
    }

    @Override
    protected Predicate<ItemStack> inventoryChecker() {
        return stack -> stack.hasTag() && FTBFilterSystemAPI.api().isFilterItem(stack)
                && !FTBFilterSystemClient.isPlayerHolding(stack);
    }

    @Override
    protected String serialize(ItemStack stack) {
        return SmartFilterItem.getFilterString(stack);
    }

    @Override
    protected void doScheduledUpdate() {
        if (editBox.getValue().isEmpty()) {
            setStatus(true, Component.empty(), null);
        } else {
            try {
                FilterParser.parse(editBox.getValue());
                setStatus(true, Component.translatable("ftbfiltersystem.gui.filter_ok"), null);
            } catch (FilterException e) {
                setStatus(false, Component.translatable("ftbfiltersystem.gui.filter_bad"), e.getMessage());
            }
        }
    }
}
