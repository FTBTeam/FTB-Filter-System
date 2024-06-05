package dev.ftb.mods.ftbfiltersystem.filter.compound;

import dev.ftb.mods.ftbfiltersystem.api.FTBFilterSystemAPI;
import dev.ftb.mods.ftbfiltersystem.api.filter.AbstractCompoundFilter;
import dev.ftb.mods.ftbfiltersystem.api.filter.SmartFilter;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class AndFilter extends AbstractCompoundFilter {
    public static final ResourceLocation ID = FTBFilterSystemAPI.rl("and");

    public AndFilter(SmartFilter.Compound parent) {
        super(parent);
    }

    public AndFilter(SmartFilter.Compound parent, List<SmartFilter> children) {
        super(parent, children);
    }

    @Override
    public boolean test(ItemStack stack) {
        return getChildren().stream().allMatch(filter -> filter.test(stack));
    }

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    public static AndFilter fromString(SmartFilter.Compound parent, String str) {
        return createCompoundFilter(AndFilter::new, parent, str);
    }
}
