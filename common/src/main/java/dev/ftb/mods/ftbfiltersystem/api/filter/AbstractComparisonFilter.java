package dev.ftb.mods.ftbfiltersystem.api.filter;

import dev.ftb.mods.ftbfiltersystem.api.NumericComparison;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.ItemStack;

/**
 * Base class for all simple comparison filters; those filters which compare a single integer property.
 */
public abstract class AbstractComparisonFilter extends AbstractSmartFilter {
    protected final NumericComparison comparison;

    protected AbstractComparisonFilter(SmartFilter.Compound parent, NumericComparison comparison) {
        super(parent);
        this.comparison = comparison;
    }

    public NumericComparison getComparison() {
        return comparison;
    }

    /**
     * Retrieve the actual value to be compared from the given itemstack
     * @param stack the stack to check
     * @return the value to be compared
     */
    protected abstract int getValueToCompare(ItemStack stack);

    /**
     * Does this filter allow the comparison value to alternately be specified as a percentage of the maximum amount?
     * @return true to allow percentage comparisons, false otherwise
     */
    public boolean allowsPercentage() {
        return false;
    }

    @Override
    public boolean test(ItemStack stack) {
        return comparison.test(getValueToCompare(stack));
    }

    @Override
    public String getStringArg(HolderLookup.Provider registryAccess) {
        return comparison.toString();
    }
}
