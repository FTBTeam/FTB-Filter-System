
package dev.ftb.mods.ftbfiltersystem.filter.compound;

import dev.ftb.mods.ftbfiltersystem.api.FTBFilterSystemAPI;
import dev.ftb.mods.ftbfiltersystem.api.filter.AbstractCompoundFilter;
import dev.ftb.mods.ftbfiltersystem.api.filter.SmartFilter;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class OrFilter extends AbstractCompoundFilter {
    public static final Identifier ID = FTBFilterSystemAPI.rl("or");

    public OrFilter(SmartFilter.Compound parent) {
        super(parent);
    }

    public OrFilter(SmartFilter.Compound parent, List<SmartFilter> children) {
        super(parent, children);
    }

    @Override
    public boolean test(ItemStack stack) {
        return getChildren().stream().anyMatch(filter -> filter.test(stack));
    }

    public static OrFilter fromString(SmartFilter.Compound parent, String str, HolderLookup.Provider registryAccess) {
        return createCompoundFilter(OrFilter::new, parent, str, registryAccess);
    }

    @Override
    public Identifier getId() {
        return ID;
    }
}
