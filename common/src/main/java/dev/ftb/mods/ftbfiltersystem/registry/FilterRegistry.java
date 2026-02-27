package dev.ftb.mods.ftbfiltersystem.registry;

import dev.architectury.platform.Platform;
import dev.ftb.mods.ftbfiltersystem.api.FTBFilterSystemRegistry;
import dev.ftb.mods.ftbfiltersystem.api.filter.SmartFilter;
import net.fabricmc.api.EnvType;
import net.minecraft.resources.Identifier;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class FilterRegistry implements FTBFilterSystemRegistry {
    private static final FilterRegistry SERVER_INSTANCE = new FilterRegistry();
    private static final FilterRegistry CLIENT_INSTANCE = new FilterRegistry();

    private final Map<Identifier, FilterDetails<?>> filterMap = new ConcurrentHashMap<>();
    private final Map<Identifier, SmartFilter> defaultInstances = new ConcurrentHashMap<>();
    private boolean frozen = false;

    public static FilterRegistry getInstance() {
        return Platform.getEnv() == EnvType.CLIENT ? CLIENT_INSTANCE : SERVER_INSTANCE;
    }

    @Override
    public <T extends SmartFilter> void register(Identifier id, SmartFilter.Factory<T> factory, SmartFilter.DefaultFactory<T> defaultFactory) {
        if (frozen) {
            throw new IllegalStateException("filter registry is now frozen!");
        }
        filterMap.put(id, new FilterDetails<>(id, factory, defaultFactory));
    }

    @Override
    public Collection<Identifier> allFilterKeys() {
        return Collections.unmodifiableCollection(filterMap.keySet());
    }

    @Override
    public Collection<SmartFilter> defaultFilterInstances() {
        return defaultInstances.values();
    }

    public Optional<FilterDetails<?>> getDetails(Identifier type) {
        return Optional.ofNullable(filterMap.get(type));
    }

    public void freeze() {
        frozen = true;
        filterMap.forEach((id, entry) -> defaultInstances.put(id, entry.defaultSupplier().create(null)));
    }

    public Optional<SmartFilter> createDefaultFilter(SmartFilter.Compound parent, Identifier filterId) {
        if (filterMap.containsKey(filterId)) {
            return Optional.of(filterMap.get(filterId).defaultSupplier().create(parent));
        }
        return Optional.empty();
    }

    public record FilterDetails<T extends SmartFilter> (
            Identifier id,
            SmartFilter.Factory<T> factory,
            SmartFilter.DefaultFactory<T> defaultSupplier
    ) { }
}
