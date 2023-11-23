
package dev.ftb.mods.ftbfiltersystem.filter.compound;

import dev.ftb.mods.ftbfiltersystem.api.filter.AbstractCompoundFilter;
import dev.ftb.mods.ftbfiltersystem.api.FTBFilterSystemAPI;
import dev.ftb.mods.ftbfiltersystem.api.filter.SmartFilter;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class NotFilter extends AbstractCompoundFilter {
    public static final ResourceLocation ID = FTBFilterSystemAPI.rl("not");

    public NotFilter(SmartFilter.Compound parent) {
        super(parent);
    }

    public NotFilter(SmartFilter.Compound parent, List<SmartFilter> children) {
        super(parent, children);
    }

    @Override
    public int maxChildren() {
        return 1;
    }

    @Override
    public boolean test(ItemStack stack) {
        return !getChildren().isEmpty() && !getChildren().get(0).test(stack);
    }

    public static NotFilter fromString(SmartFilter.Compound parent, String str) {
        return createCompoundFilter(NotFilter::new, parent, str);
    }

    @Override
    public ResourceLocation getId() {
        return ID;
    }
}
