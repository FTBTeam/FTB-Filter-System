package dev.ftb.mods.ftbfiltersystem.filter.compound;

import dev.ftb.mods.ftbfiltersystem.api.FTBFilterSystemAPI;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;

/**
 * Special root-level filter which isn't registered, so can't be created via parsing, but is implicitly created
 * as the top-level filter in any filter tree built by {@link dev.ftb.mods.ftbfiltersystem.util.FilterParser#parse(String)}
 * It acts otherwise just like an AND filter.
 */
public class RootFilter extends AndFilter {
    private static final ResourceLocation ID = FTBFilterSystemAPI.rl("root");

    public RootFilter() {
        super(null, new ArrayList<>());
    }

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    @Override
    public String toString() {
        return getStringArg();
    }
}
