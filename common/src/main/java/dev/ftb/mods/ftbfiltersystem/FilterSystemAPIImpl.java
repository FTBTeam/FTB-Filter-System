package dev.ftb.mods.ftbfiltersystem;

import dev.ftb.mods.ftbfiltersystem.api.FTBFilterSystemAPI;
import dev.ftb.mods.ftbfiltersystem.api.FTBFilterSystemRegistry;
import dev.ftb.mods.ftbfiltersystem.api.FilterException;
import dev.ftb.mods.ftbfiltersystem.api.filter.DumpedFilter;
import dev.ftb.mods.ftbfiltersystem.api.filter.SmartFilter;
import dev.ftb.mods.ftbfiltersystem.filter.ItemTagFilter;
import dev.ftb.mods.ftbfiltersystem.registry.FilterRegistry;
import dev.ftb.mods.ftbfiltersystem.registry.ModItems;
import dev.ftb.mods.ftbfiltersystem.registry.item.SmartFilterItem;
import dev.ftb.mods.ftbfiltersystem.util.FilterParser;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public enum FilterSystemAPIImpl implements FTBFilterSystemAPI.API {
    INSTANCE;

    @Override
    public FTBFilterSystemRegistry getRegistry() {
        return FilterRegistry.getInstance();
    }

    @Override
    public boolean isFilterItem(ItemStack stack) {
        return stack.getItem() instanceof SmartFilterItem;
    }

    @Override
    public Item filterItem() {
        return Objects.requireNonNull(ModItems.SMART_FILTER.get());
    }

    @Override
    public boolean doesFilterMatch(ItemStack filterStack, ItemStack toMatch, HolderLookup.Provider registryAccess) {
        try {
            return isFilterItem(filterStack)
                    && FilterParser.parse(SmartFilterItem.getFilterString(filterStack), registryAccess).test(toMatch);
        } catch (FilterException e) {
            return false;
        }
    }

    @Override
    public List<DumpedFilter> dump(SmartFilter filter) {
        return dump(filter, 0, new ArrayList<>());
    }

    @Override
    public Optional<SmartFilter> createDefaultFilter(SmartFilter.Compound parent, ResourceLocation filterId) {
        return FilterRegistry.getInstance().createDefaultFilter(parent, filterId);
    }

    @Override
    public SmartFilter parseFilter(String filterStr, HolderLookup.Provider registryAccess) throws FilterException {
        return FilterParser.parse(filterStr, registryAccess);
    }

    @Override
    public SmartFilter parseFilter(ItemStack filterStack, HolderLookup.Provider registryAccess) throws FilterException {
        return parseFilter(SmartFilterItem.getFilterString(filterStack), registryAccess);
    }

    @Override
    public List<SmartFilter> parseFilterList(SmartFilter.Compound parent, String filterStr, HolderLookup.Provider registryAccess) throws FilterException {
        return FilterParser.parseFilterList(parent, filterStr, registryAccess);
    }

    @Override
    public ItemStack makeTagFilter(TagKey<Item> tagKey) {
        ItemStack res = new ItemStack(ModItems.SMART_FILTER.get());
        SmartFilterItem.setFilter(res, ItemTagFilter.ID + "(" + tagKey.location() + ")");
        return res;
    }

    private List<DumpedFilter> dump(SmartFilter filter, int indent, List<DumpedFilter> res) {
        res.add(new DumpedFilter(indent, filter));

        if (filter instanceof SmartFilter.Compound cf) {
            cf.getChildren().forEach(f -> dump(f, indent + 1, res));
        }

        return res;
    }
}
