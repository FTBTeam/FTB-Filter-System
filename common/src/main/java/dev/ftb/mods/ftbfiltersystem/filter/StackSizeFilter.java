package dev.ftb.mods.ftbfiltersystem.filter;

import dev.ftb.mods.ftbfiltersystem.api.FTBFilterSystemAPI;
import dev.ftb.mods.ftbfiltersystem.api.NumericComparison;
import dev.ftb.mods.ftbfiltersystem.api.filter.AbstractComparisonFilter;
import dev.ftb.mods.ftbfiltersystem.api.filter.SmartFilter;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;

public class StackSizeFilter extends AbstractComparisonFilter {
    public static final Identifier ID = FTBFilterSystemAPI.rl("stack_size");

    public StackSizeFilter(SmartFilter.Compound parent) {
        this(parent, new NumericComparison(NumericComparison.ComparisonOp.GT, 1, false));
    }

    public StackSizeFilter(SmartFilter.Compound parent, NumericComparison comparison) {
        super(parent, comparison);
    }

    @Override
    public Identifier getId() {
        return ID;
    }

    @Override
    protected int getValueToCompare(ItemStack stack) {
        return stack.getCount();
    }

    public static StackSizeFilter fromString(SmartFilter.Compound parent, String str, HolderLookup.Provider ignored2) {
        return new StackSizeFilter(parent, NumericComparison.fromString(str, false));
    }
}
