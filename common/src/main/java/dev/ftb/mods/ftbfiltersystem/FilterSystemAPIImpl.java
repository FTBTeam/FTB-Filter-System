package dev.ftb.mods.ftbfiltersystem;

import dev.ftb.mods.ftbfiltersystem.api.*;
import dev.ftb.mods.ftbfiltersystem.api.filter.AbstractCompoundFilter;
import dev.ftb.mods.ftbfiltersystem.api.filter.DumpedFilter;
import dev.ftb.mods.ftbfiltersystem.api.filter.SmartFilter;
import dev.ftb.mods.ftbfiltersystem.registry.item.SmartFilterItem;
import dev.ftb.mods.ftbfiltersystem.registry.FilterRegistry;
import dev.ftb.mods.ftbfiltersystem.util.FilterParser;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public enum FilterSystemAPIImpl implements FTBFilterSystemAPI.API {
    INSTANCE;

    @Override
    public FTBFilterSystemRegistry getRegistry() {
        return FilterRegistry.INSTANCE;
    }

    @Override
    public boolean isFilterItem(ItemStack stack) {
        return stack.getItem() instanceof SmartFilterItem;
    }

    @Override
    public boolean doesFilterMatch(ItemStack filterStack, ItemStack toMatch) {
        return isFilterItem(filterStack)
                && FilterParser.parse(SmartFilterItem.getFilterString(filterStack)).test(toMatch);
    }

    @Override
    public List<DumpedFilter> dump(SmartFilter filter) {
        return dump(filter, 0, new ArrayList<>());
    }

    @Override
    public Optional<SmartFilter> createDefaultFilter(SmartFilter.Compound parent, ResourceLocation filterId) {
        return FilterRegistry.INSTANCE.createDefaultFilter(parent, filterId);
    }

    @Override
    public SmartFilter parseFilter(String filterStr) throws FilterException {
        return FilterParser.parse(filterStr);
    }

    @Override
    public List<SmartFilter> parseFilterList(SmartFilter.Compound parent, String filterStr) throws FilterException {
        return FilterParser.parseFilterList(parent, filterStr);
    }

    private List<DumpedFilter> dump(SmartFilter filter, int indent, List<DumpedFilter> res) {
        res.add(new DumpedFilter(indent, filter));

        if (filter instanceof SmartFilter.Compound cf) {
            cf.getChildren().forEach(f -> dump(f, indent + 1, res));
        }

        return res;
    }
}
