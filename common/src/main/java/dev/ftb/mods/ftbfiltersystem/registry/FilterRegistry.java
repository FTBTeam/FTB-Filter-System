package dev.ftb.mods.ftbfiltersystem.registry;

import com.google.common.collect.ImmutableMap;
import dev.ftb.mods.ftbfiltersystem.api.FTBFilterSystemRegistry;
import dev.ftb.mods.ftbfiltersystem.api.filter.SmartFilter;
import dev.ftb.mods.ftblibrary.util.Lazy;
import net.minecraft.resources.Identifier;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class FilterRegistry implements FTBFilterSystemRegistry {
    private static final FilterRegistry INSTANCE = new FilterRegistry();

    private final Map<Identifier, FilterDetails<?>> filterMap = new ConcurrentHashMap<>();
    private final Lazy<Map<Identifier, SmartFilter>> defaultInstances = Lazy.of(this::buildDefaultInstances);

    public static FilterRegistry getInstance() {
        return INSTANCE;
    }

    private Map<Identifier, SmartFilter> buildDefaultInstances() {
        ImmutableMap.Builder<Identifier, SmartFilter> res = ImmutableMap.builder();
        filterMap.forEach((id, entry) -> res.put(id, entry.defaultSupplier().create(null)));
        return res.build();
    }

    @Override
    public <T extends SmartFilter> void register(Identifier id, SmartFilter.Factory<T> factory, SmartFilter.DefaultFactory<T> defaultFactory) {
        filterMap.put(id, new FilterDetails<>(id, factory, defaultFactory));
    }

    @Override
    public Collection<Identifier> allFilterKeys() {
        return Collections.unmodifiableCollection(filterMap.keySet());
    }

    @Override
    public Collection<SmartFilter> defaultFilterInstances() {
        return defaultInstances.get().values();
    }

    public Optional<FilterDetails<?>> getDetails(Identifier type) {
        return Optional.ofNullable(filterMap.get(type));
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
