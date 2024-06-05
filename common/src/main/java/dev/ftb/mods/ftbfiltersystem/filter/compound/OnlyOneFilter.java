
package dev.ftb.mods.ftbfiltersystem.filter.compound;

import dev.ftb.mods.ftbfiltersystem.api.FTBFilterSystemAPI;
import dev.ftb.mods.ftbfiltersystem.api.filter.AbstractCompoundFilter;
import dev.ftb.mods.ftbfiltersystem.api.filter.SmartFilter;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.apache.commons.lang3.mutable.MutableInt;

import java.util.List;

public class OnlyOneFilter extends AbstractCompoundFilter {
    public static final ResourceLocation ID = FTBFilterSystemAPI.rl("only_one");

    public OnlyOneFilter(SmartFilter.Compound parent) {
        super(parent);
    }

    public OnlyOneFilter(SmartFilter.Compound parent, List<SmartFilter> children) {
        super(parent, children);
    }

    @Override
    public boolean test(ItemStack stack) {
        MutableInt matched = new MutableInt(0);
        return getChildren().stream().noneMatch(child -> child.test(stack) && matched.incrementAndGet() > 1) && matched.intValue() == 1;
    }

    public static OnlyOneFilter fromString(SmartFilter.Compound parent, String str) {
        return createCompoundFilter(OnlyOneFilter::new, parent, str);
    }

    @Override
    public ResourceLocation getId() {
        return ID;
    }
}
