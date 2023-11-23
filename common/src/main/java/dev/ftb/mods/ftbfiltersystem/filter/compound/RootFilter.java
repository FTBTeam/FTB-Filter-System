package dev.ftb.mods.ftbfiltersystem.filter.compound;

import dev.ftb.mods.ftbfiltersystem.api.filter.AbstractCompoundFilter;
import dev.ftb.mods.ftbfiltersystem.api.FTBFilterSystemAPI;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;

/**
 * Special root-level filter which isn't registered, so can't be created via parsing, but is implicitly created
 * as the top-level filter in any filter tree built by {@link dev.ftb.mods.ftbfiltersystem.util.FilterParser#parse(String)}
 */
public class RootFilter extends AbstractCompoundFilter {
    private static final ResourceLocation ID = FTBFilterSystemAPI.rl("root");

    public RootFilter() {
        super(null, new ArrayList<>());
    }

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    @Override
    public int maxChildren() {
        return 1;
    }

    @Override
    public String toString() {
        return getChildren().isEmpty() ? "" : getChildren().get(0).toString();
    }

    @Override
    public boolean test(ItemStack stack) {
        return !getChildren().isEmpty() && getChildren().get(0).test(stack);
    }
}
