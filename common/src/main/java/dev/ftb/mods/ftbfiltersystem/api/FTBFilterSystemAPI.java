package dev.ftb.mods.ftbfiltersystem.api;

import dev.ftb.mods.ftbfiltersystem.api.client.FTBFilterSystemClientAPI;
import dev.ftb.mods.ftbfiltersystem.api.filter.DumpedFilter;
import dev.ftb.mods.ftbfiltersystem.api.filter.SmartFilter;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class FTBFilterSystemAPI {
    public static final String MOD_ID = "ftbfiltersystem";

    private static API instance;
    private static FTBFilterSystemClientAPI clientInstance;

    /**
     * Retrieve the public API instance.
     *
     * @return the API handler
     * @throws NullPointerException if called before initialised
     */
    @NotNull
    public static FTBFilterSystemAPI.API api() {
        return Objects.requireNonNull(instance);
    }

    /**
     * Retrieve the public API instance.
     *
     * @return the API handler
     * @throws NullPointerException if called before initialised
     */
    @NotNull
    public static FTBFilterSystemClientAPI clientApi() {
        return Objects.requireNonNull(clientInstance);
    }

    /**
     * Convenience method to get a resource location in the FTB Filter System namespace
     *
     * @param path the resource location path component
     * @return a new resource location
     */
    public static ResourceLocation rl(String path) {
        return new ResourceLocation(MOD_ID, path);
    }

    /**
     * Get a resource location from the given string, defaulting to "ftbfiltersystem:" namespace
     *
     * @param str a string
     * @return a resource location
     */
    public static ResourceLocation modDefaultedRL(String str) {
        return str.indexOf(':') > 0 ? new ResourceLocation(str) : new ResourceLocation(FTBFilterSystemAPI.MOD_ID, str);
    }

    /**
     * Stringify a resource location, omitting the namespace if it's "ftbfiltersystem:"
     *
     * @param rl a resource location
     * @return stringified resource location
     */
    public static String modDefaultedString(ResourceLocation rl) {
        return rl.getNamespace().equals(FTBFilterSystemAPI.MOD_ID) ? rl.getPath() : rl.toString();
    }

    /**
     * Do not call this method yourself!
     * @param instance the API instance
     */
    @ApiStatus.Internal
    public static void _init(API instance) {
        if (FTBFilterSystemAPI.instance != null) {
            throw new IllegalStateException("can't init more than once!");
        }
        FTBFilterSystemAPI.instance = instance;
    }

    /**
     * Do not call this method yourself!
     * @param instance the API instance
     */
    @ApiStatus.Internal
    public static void _initClient(FTBFilterSystemClientAPI instance) {
        if (FTBFilterSystemAPI.clientInstance != null) {
            throw new IllegalStateException("can't init more than once!");
        }
        FTBFilterSystemAPI.clientInstance = instance;
    }

    public interface API {
        /**
         * Get the filter registry.
         *
         * @return the filter registry
         */
        FTBFilterSystemRegistry getRegistry();

        /**
         * Check if the given itemstack is an FTB Filter System filter.
         *
         * @param stack the stack to check
         * @return true if it's a filter, false otherwise
         */
        boolean isFilterItem(ItemStack stack);

        /**
         * {@return the Smart Filter item} May be useful for mods which wish to add capabilities and/or components
         * to the Smart Filter. Do not call this before registration has completed.
         */
        Item filterItem();

        /**
         * Check if the given filter stack matches the given item stack
         * .
         * @param filterStack the filter stack, which should be a FTB Filter System filter
         * @param toMatch the item stack to test
         * @return true if the filter stack matches, false otherwise (the filter stack isn't a filter, or the filter NBT is bad)
         */
        boolean doesFilterMatch(ItemStack filterStack, ItemStack toMatch);

        /**
         * Get a list of dumped filter records representing all the subfilters found in this filter, in order. Generally
         * used for display purposes, GUI or otherwise.
         *
         * @param filter the filter whose contents to dump
         * @return the dumped contents, including indent information for display formatting purposes
         */
        List<DumpedFilter> dump(SmartFilter filter);

        /**
         * Create a filter with default settings
         *
         * @param parent the compound filter parent for the new filter
         * @param filterId the filter type ID
         * @return a new filter of the given type, appended to the given parent, or {@code Optional.empty()} if the ID is unknown
         */
        Optional<SmartFilter> createDefaultFilter(@NotNull SmartFilter.Compound parent, ResourceLocation filterId);

        /**
         * Create a new filter, parsed from the given serialized string. Such strings are produced by calling
         * the overridden {@code toString()} on any {@link SmartFilter} implementation.
         *
         * @param filterStr the string to parse
         * @return a new filter
         * @throws FilterException if there's a problem parsing the data
         */
        SmartFilter parseFilter(String filterStr) throws FilterException;

        /**
         * Create a new filter, parsed from the given serialized string. Such strings are produced by calling
         * the overridden {@code toString()} on any {@link SmartFilter} implementation.
         *
         * @param filterStack the itemstack, which should be a valid filter item
         * @return a new filter
         * @throws FilterException if the item stack doesn't have valid filter NBT, or there's a problem parsing the data
         */
        SmartFilter parseFilter(ItemStack filterStack) throws FilterException;

        /**
         * Create a list of new filters, parsed from the given serialized string. Such strings are produced by calling
         * the overridden {@code toString()} method on any {@link SmartFilter} implementation; multiples of such strings
         * can be concatenated to act as input for this method.
         *
         * @param parent the compound filter which will be the parent of all returned filters
         * @param filterStr the string to parse
         * @return a list of filters, parsed from the string data
         * @throws FilterException if there's a problem parsing the data
         */
        List<SmartFilter> parseFilterList(@NotNull SmartFilter.Compound parent, String filterStr) throws FilterException;

        /**
         * Create a simple filter which just filters on a specific item tag.
         *
         * @param tagKey the item tag to filter on
         * @return the new filter itemstack
         */
        ItemStack makeTagFilter(TagKey<Item> tagKey);
    }
}
