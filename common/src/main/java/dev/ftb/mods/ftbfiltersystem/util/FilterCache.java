package dev.ftb.mods.ftbfiltersystem.util;

import dev.ftb.mods.ftbfiltersystem.api.FilterException;
import dev.ftb.mods.ftbfiltersystem.api.filter.SmartFilter;
import net.minecraft.core.HolderLookup;

/**
 * An LRU cache to store known filter strings and the SmartFilter objects created from them.
 */
public enum FilterCache {
    INSTANCE;

    private static final int MAX_SIZE = 1000;
    private final LRUCache<String,SmartFilter> cache = new LRUCache<>(MAX_SIZE);

    public SmartFilter getOrCreateFilter(String filterStr, HolderLookup.Provider registryAccess) throws FilterException {
        SmartFilter res = cache.get(filterStr);
        if (res == null) {
            try {
                res = FilterParser.parseRaw(filterStr, registryAccess);
            } catch (FilterException ignored) {
                // res stays null, which is fine
            }
            cache.put(filterStr, res);
        }
        return res;
    }

    public void clear() {
        cache.clear();
    }
}
