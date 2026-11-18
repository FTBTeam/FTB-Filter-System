package dev.ftb.mods.ftbfiltersystem.filter;

import dev.ftb.mods.ftbfiltersystem.api.FTBFilterSystemAPI;
import dev.ftb.mods.ftbfiltersystem.api.NumericComparison;
import dev.ftb.mods.ftbfiltersystem.api.filter.AbstractComparisonFilter;
import dev.ftb.mods.ftbfiltersystem.api.filter.SmartFilter;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public class MaxStackSizeFilter extends AbstractComparisonFilter {
    public static final ResourceLocation ID = FTBFilterSystemAPI.rl("max_stack_size");

    public MaxStackSizeFilter(SmartFilter.Compound parent) {
        this(parent, new NumericComparison(NumericComparison.ComparisonOp.GT, 1, false));
    }

    public MaxStackSizeFilter(SmartFilter.Compound parent, NumericComparison comparison) {
        super(parent, comparison);
    }

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    @Override
    protected int getValueToCompare(ItemStack stack) {
        return stack.getMaxStackSize();
    }

    public static MaxStackSizeFilter fromString(SmartFilter.Compound parent, String str, HolderLookup.Provider ignored2) {
        return new MaxStackSizeFilter(parent, NumericComparison.fromString(str, false));
    }
}
