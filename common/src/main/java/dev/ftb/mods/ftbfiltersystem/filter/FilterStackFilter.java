package dev.ftb.mods.ftbfiltersystem.filter;

import dev.ftb.mods.ftbfiltersystem.api.FTBFilterSystemAPI;
import dev.ftb.mods.ftbfiltersystem.api.FilterException;
import dev.ftb.mods.ftbfiltersystem.api.filter.AbstractCompoundFilter;
import dev.ftb.mods.ftbfiltersystem.api.filter.AbstractSmartFilter;
import dev.ftb.mods.ftbfiltersystem.api.filter.SmartFilter;
import dev.ftb.mods.ftbfiltersystem.registry.ModItems;
import dev.ftb.mods.ftbfiltersystem.registry.item.SmartFilterItem;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public class FilterStackFilter extends AbstractSmartFilter {
    public static final ResourceLocation ID = FTBFilterSystemAPI.rl("filter_stack");

    private final ItemStack filterStack;

    public FilterStackFilter(SmartFilter.Compound parent) {
        this(parent, new ItemStack(ModItems.SMART_FILTER.get()));
    }

    public FilterStackFilter(SmartFilter.Compound parent, ItemStack filterStack) {
        super(parent);
        if (!(filterStack.getItem() instanceof SmartFilterItem)) {
            throw new FilterException("item must be a smart filter!");
        }
        this.filterStack = filterStack;
    }

    public ItemStack getFilterStack() {
        return filterStack;
    }

    @Override
    public boolean test(ItemStack stack) {
        return FTBFilterSystemAPI.api().doesFilterMatch(filterStack, stack);
    }

    @Override
    public String getStringArg() {
        return SmartFilterItem.getFilterString(filterStack);
    }

    public static FilterStackFilter fromString(SmartFilter.Compound parent, String str) {
        ItemStack filterStack = new ItemStack(ModItems.SMART_FILTER.get());
        SmartFilterItem.setFilter(filterStack, str);
        return new FilterStackFilter(parent, filterStack);
    }

    @Override
    public ResourceLocation getId() {
        return ID;
    }
}
