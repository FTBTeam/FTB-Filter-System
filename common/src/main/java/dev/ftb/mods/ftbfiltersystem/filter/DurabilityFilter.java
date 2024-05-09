package dev.ftb.mods.ftbfiltersystem.filter;

import dev.ftb.mods.ftbfiltersystem.api.FTBFilterSystemAPI;
import dev.ftb.mods.ftbfiltersystem.api.NumericComparison;
import dev.ftb.mods.ftbfiltersystem.api.filter.AbstractComparisonFilter;
import dev.ftb.mods.ftbfiltersystem.api.filter.SmartFilter;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public class DurabilityFilter extends AbstractComparisonFilter {
    public static final ResourceLocation ID = FTBFilterSystemAPI.rl("durability");

    public DurabilityFilter(SmartFilter.Compound parent) {
        this(parent, new NumericComparison(NumericComparison.ComparisonOp.GT, 0, true));
    }

    public DurabilityFilter(SmartFilter.Compound parent, NumericComparison comparison) {
        super(parent, comparison);
    }

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    @Override
    protected int getValueToCompare(ItemStack stack) {
        if (stack.getMaxDamage() != 0) {
            int durability = stack.getMaxDamage() - stack.getDamageValue();
            return comparison.percentage() ?
                    (int) (durability * 100L / stack.getMaxDamage()) :  // long arithmetic for int overflow avoidance
                    durability;
        } else {
            return 0;
        }
    }

    @Override
    public boolean allowsPercentage() {
        return true;
    }

    public static DurabilityFilter fromString(SmartFilter.Compound parent, String str) {
        return new DurabilityFilter(parent, NumericComparison.fromString(str, true));
    }
}
