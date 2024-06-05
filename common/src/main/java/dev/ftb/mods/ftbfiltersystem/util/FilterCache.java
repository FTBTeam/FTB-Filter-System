package dev.ftb.mods.ftbfiltersystem.util;

import dev.ftb.mods.ftbfiltersystem.api.FilterException;
import dev.ftb.mods.ftbfiltersystem.api.filter.SmartFilter;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;

/**
 * An LRU cache to store known filter strings and the SmartFilter objects created from them.
 */
public enum FilterCache {
    INSTANCE;

    private static final int MAX_SIZE = 1000;
    private final Object2ObjectLinkedOpenHashMap<String,SmartFilter> cache = new Object2ObjectLinkedOpenHashMap<>();

    public SmartFilter getOrCreateFilter(String filterStr) throws FilterException {
        if (cache.containsKey(filterStr)) {
            return cache.getAndMoveToFirst(filterStr);
        } else {
            SmartFilter filter;
            try {
                filter = FilterParser.parseRaw(filterStr);
            } catch (FilterException e) {
                filter = null;
            }
            if (cache.size() >= MAX_SIZE) {
                cache.removeLast();
            }
            cache.put(filterStr, filter);
            return filter;
        }
    }

    public void clear() {
        cache.clear();
    }
}
