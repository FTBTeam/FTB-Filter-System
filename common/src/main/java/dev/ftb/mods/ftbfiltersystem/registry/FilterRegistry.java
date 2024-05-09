package dev.ftb.mods.ftbfiltersystem.registry;

import dev.ftb.mods.ftbfiltersystem.api.FTBFilterSystemRegistry;
import dev.ftb.mods.ftbfiltersystem.api.filter.SmartFilter;
import net.minecraft.resources.ResourceLocation;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public enum FilterRegistry implements FTBFilterSystemRegistry {
    INSTANCE;

    private final Map<ResourceLocation, FilterDetails<?>> filterMap = new ConcurrentHashMap<>();
    private final Map<ResourceLocation, SmartFilter> defaultInstances = new ConcurrentHashMap<>();
    private boolean frozen = false;

    @Override
    public <T extends SmartFilter> void register(ResourceLocation id, SmartFilter.Factory<T> factory, SmartFilter.DefaultFactory<T> defaultFactory) {
        if (frozen) {
            throw new IllegalStateException("filter registry is now frozen!");
        }
        filterMap.put(id, new FilterDetails<>(id, factory, defaultFactory));
    }

    @Override
    public Collection<ResourceLocation> allFilterKeys() {
        return Collections.unmodifiableCollection(filterMap.keySet());
    }

    @Override
    public Collection<SmartFilter> defaultFilterInstances() {
        return defaultInstances.values();
    }

    public Optional<FilterDetails<?>> getDetails(ResourceLocation type) {
        return Optional.ofNullable(filterMap.get(type));
    }

    public void freeze() {
        frozen = true;
        filterMap.forEach((id, entry) -> defaultInstances.put(id, entry.defaultSupplier().create(null)));
    }

    public Optional<SmartFilter> createDefaultFilter(SmartFilter.Compound parent, ResourceLocation filterId) {
        if (filterMap.containsKey(filterId)) {
            return Optional.of(filterMap.get(filterId).defaultSupplier().create(parent));
        }
        return Optional.empty();
    }

    public record FilterDetails<T extends SmartFilter>(ResourceLocation id, SmartFilter.Factory<T> factory, SmartFilter.DefaultFactory<T> defaultSupplier) {
    }
}
