package dev.ftb.mods.ftbfiltersystem.filter.compound;

import dev.ftb.mods.ftbfiltersystem.api.FTBFilterSystemAPI;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.Identifier;

import java.util.ArrayList;

/**
 * Special root-level filter which isn't registered, so can't be created via parsing, but is implicitly created
 * as the top-level filter in any filter tree built by
 * {@link dev.ftb.mods.ftbfiltersystem.util.FilterParser#parse(String,HolderLookup.Provider)}
 * It acts otherwise just like an AND filter.
 */
public class RootFilter extends AndFilter {
    private static final Identifier ID = FTBFilterSystemAPI.rl("root");

    public RootFilter() {
        super(null, new ArrayList<>());
    }

    @Override
    public Identifier getId() {
        return ID;
    }

    @Override
    public String asString(HolderLookup.Provider registryAccess) {
        // root filter stringifies as just its arguments (it doesn't have a registered ID)
        return getStringArg(registryAccess);
    }
}
