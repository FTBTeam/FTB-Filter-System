package dev.ftb.mods.ftbfiltersystem.api;

import dev.ftb.mods.ftbfiltersystem.api.filter.SmartFilter;
import net.minecraft.resources.ResourceLocation;

import java.util.Collection;

/**
 * Holds data about all known registered filter types. Custom implementations should be registered using the
 * {@link dev.ftb.mods.ftbfiltersystem.api.event.FilterRegistrationEvent} event.
 */
public interface FTBFilterSystemRegistry {
    /**
     * Register a new filter type. This must be done from the {@code FilterRegistrationEvent} !
     * @param id the unique id for this filter type
     * @param factory a factory to produce an instance from a serialized string
     * @param defaultFactory a factory to produce an instance with default properties
     * @param <T> the implementation type
     */
    <T extends SmartFilter> void register(ResourceLocation id, SmartFilter.Factory<T> factory, SmartFilter.DefaultFactory<T> defaultFactory);

    /**
     * Return all the known registered filter type ID's.
     *
     * @return known type ID's
     */
    Collection<ResourceLocation> allFilterKeys();

    /**
     * Return a collection of default instances for every registered filter type.
     *
     * @return all the default instances
     */
    Collection<SmartFilter> defaultFilterInstances();
}
