package dev.ftb.mods.ftbfiltersystem.client.gui;

import dev.ftb.mods.ftbfiltersystem.api.FTBFilterSystemAPI;
import dev.ftb.mods.ftbfiltersystem.api.FilterException;
import dev.ftb.mods.ftbfiltersystem.api.client.gui.AbstractFilterScreen;
import dev.ftb.mods.ftbfiltersystem.filter.FilterStackFilter;
import dev.ftb.mods.ftbfiltersystem.registry.ModItems;
import dev.ftb.mods.ftbfiltersystem.registry.item.SmartFilterItem;
import dev.ftb.mods.ftbfiltersystem.util.FilterParser;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.function.Predicate;

public class FilterStackConfigScreen extends AbstractItemEditorConfigScreen<FilterStackFilter> {
    public FilterStackConfigScreen(FilterStackFilter filter, AbstractFilterScreen parentScreen) {
        super(filter, parentScreen, 320, 176);
    }

    @Override
    protected @Nullable FilterStackFilter makeNewFilter() {
        ItemStack stack = new ItemStack(ModItems.SMART_FILTER.get());
        SmartFilterItem.setFilter(stack, editBox.getValue());
        if (customHoverName != null) {
            stack.setHoverName(customHoverName);
        }
        return new FilterStackFilter(filter.getParent(), stack);
    }

    @Override
    protected Predicate<ItemStack> inventoryChecker() {
        return stack -> stack.hasTag() && FTBFilterSystemAPI.api().isFilterItem(stack);
    }

    @Override
    protected String serialize(ItemStack stack) {
        return SmartFilterItem.getFilterString(stack);
    }

    @Override
    protected void doScheduledUpdate() {
        if (editBox.getValue().isEmpty()) {
            statusMsg = Component.empty();
        } else {
            try {
                FilterParser.parse(editBox.getValue());
                statusMsg = Component.translatable("ftbfiltersystem.gui.filter_ok").withStyle(ChatFormatting.DARK_GREEN);
            } catch (FilterException e) {
                statusMsg = Component.translatable("ftbfiltersystem.gui.filter_bad").withStyle(ChatFormatting.DARK_RED);
            }
        }
    }

}
