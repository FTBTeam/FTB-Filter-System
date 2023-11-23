package dev.ftb.mods.ftbfiltersystem.api.client;

import dev.ftb.mods.ftbfiltersystem.api.filter.SmartFilter;
import net.minecraft.resources.ResourceLocation;

public interface FTBFilterSystemClientAPI {
    /**
     * Register a GUI configuration screen for this filter type. Filters which don't have any configurable data
     * (e.g. the "Is Block" filter) don't need to have a screen factory registered.
     *
     * @param filterId the unique filter type ID
     * @param factory the factory to create an instance of the config screen
     * @param <T> the filter implementation type
     */
    <T extends SmartFilter> void registerFilterScreenFactory(ResourceLocation filterId, FilterScreenFactory<T> factory);
}
