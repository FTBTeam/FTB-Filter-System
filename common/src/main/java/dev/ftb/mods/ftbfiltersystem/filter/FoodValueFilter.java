package dev.ftb.mods.ftbfiltersystem.filter;

import dev.ftb.mods.ftbfiltersystem.api.filter.AbstractComparisonFilter;
import dev.ftb.mods.ftbfiltersystem.api.NumericComparison;
import dev.ftb.mods.ftbfiltersystem.api.filter.AbstractCompoundFilter;
import dev.ftb.mods.ftbfiltersystem.api.FTBFilterSystemAPI;
import dev.ftb.mods.ftbfiltersystem.api.filter.SmartFilter;
import dev.ftb.mods.ftbfiltersystem.util.PlatformUtil;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public class FoodValueFilter extends AbstractComparisonFilter {
    public static final ResourceLocation ID = FTBFilterSystemAPI.rl("food_value");

    public FoodValueFilter(SmartFilter.Compound parent) {
        this(parent, new NumericComparison(NumericComparison.ComparisonOp.GT, 0, false));
    }

    public FoodValueFilter(SmartFilter.Compound parent, NumericComparison comparison) {
        super(parent, comparison);
    }

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    @Override
    protected int getValueToCompare(ItemStack stack) {
        return PlatformUtil.getFoodValue(stack);
    }

    public static FoodValueFilter fromString(SmartFilter.Compound parent, String str) {
        return new FoodValueFilter(parent, NumericComparison.fromString(str, false));
    }
}
