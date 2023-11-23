package dev.ftb.mods.ftbfiltersystem.api.filter;

import dev.ftb.mods.ftbfiltersystem.api.NumericComparison;
import net.minecraft.world.item.ItemStack;

public abstract class AbstractComparisonFilter extends AbstractSmartFilter {
    protected final NumericComparison comparison;

    protected AbstractComparisonFilter(SmartFilter.Compound parent, NumericComparison comparison) {
        super(parent);
        this.comparison = comparison;
    }

    public NumericComparison getComparison() {
        return comparison;
    }

    protected abstract int getValueToCompare(ItemStack stack);

    public boolean allowsPercentage() {
        return false;
    }

    @Override
    public boolean test(ItemStack stack) {
        return comparison.test(getValueToCompare(stack));
    }

    @Override
    public String getStringArg() {
        return comparison.toString();
    }
}
