package dev.ftb.mods.ftbfiltersystem.api.filter;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Predicate;

/**
 * Represents a filter object, which can be used to test item stacks for specific properties. Smart filters may be
 * compound (see {@link AbstractCompoundFilter}) and represent a hierarchy of filters.
 *
 * @apiNote Implementations should extend {@link AbstractSmartFilter} or {@link AbstractCompoundFilter} rather than
 * implementing this interface directly. Implementations are registered via the
 * {@link dev.ftb.mods.ftbfiltersystem.api.event.FilterRegistrationEvent} and
 * {@link dev.ftb.mods.ftbfiltersystem.api.event.client.ClientFilterRegistrationEvent} events.
 */
public interface SmartFilter extends Predicate<ItemStack> {
    /**
     * Get the unique ID for this filter type.
     * @return the filter type ID
     */
    ResourceLocation getId();

    /**
     * Get the parent for this filter. Only the top-level filter in the hierarchy has a null parent.
     *
     * @return the filter's parent filter
     */
    @Nullable
    SmartFilter.Compound getParent();

    /**
     * Get the display name for this filter.
     *
     * @return the display name
     */
    Component getDisplayName();

    /**
     * Return the filter's string argument data, which is the serialized form of the filter's properties.
     *
     * @return the filter's properties, serialized into a string
     */
    String getStringArg();

    default Component getDisplayArg() {
        return Component.literal(getStringArg());
    }

    /**
     * Is this filter configurable, i.e. does it have a setup GUI? Most filters do, but some (e.g. the "Is Block"
     * filter) don't have any configurable properties.
     * @return true if the filter is configurable, false otherwise
     */
    default boolean isConfigurable() {
        return true;
    }

    /**
     * Compound filters (those which have children) implement this interface.
     *
     * @apiNote If you want to add a custom compound filter, you should extend {@link AbstractCompoundFilter} rather than
     * implementing this interface yourself.
     */
    interface Compound extends SmartFilter {
        /**
         * Get the child filters which have been added to this compound filter.
         *
         * @return the children
         */
        List<SmartFilter> getChildren();

        /**
         * Get the maximum number of children which this compound filter can have.
         *
         * @return the maximum children
         */
        default int maxChildren() {
            return Integer.MAX_VALUE;
        }
    }

    /**
     * Factory to create a new smart filter from a serialized string. An implementation of this is registered
     * via {@link dev.ftb.mods.ftbfiltersystem.api.FTBFilterSystemRegistry#register(ResourceLocation, Factory, DefaultFactory)}.
     *
     * @param <T> the filter type
     */
    @FunctionalInterface
    interface Factory<T extends SmartFilter> {
        T create(SmartFilter.Compound parent, String arg);
    }

    /**
     * Factory to create a new smart filter with default properties. An implementation of this is registered
     * via {@link dev.ftb.mods.ftbfiltersystem.api.FTBFilterSystemRegistry#register(ResourceLocation, Factory, DefaultFactory)}.
     *
     * @param <T> the filter type
     */
    @FunctionalInterface
    interface DefaultFactory<T extends SmartFilter> {
        T create(SmartFilter.Compound parent);
    }
}
