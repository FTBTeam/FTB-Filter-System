package dev.ftb.mods.ftbfiltersystem.filter;

import dev.ftb.mods.ftbfiltersystem.api.*;
import dev.ftb.mods.ftbfiltersystem.api.filter.AbstractCompoundFilter;
import dev.ftb.mods.ftbfiltersystem.api.filter.AbstractSmartFilter;
import dev.ftb.mods.ftbfiltersystem.api.filter.SmartFilter;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class ItemFilter extends AbstractSmartFilter {
    public static final ResourceLocation ID = FTBFilterSystemAPI.rl("item");

    private final Item matchItem;

    public ItemFilter(SmartFilter.Compound parent) {
        this(parent, Items.STONE);
    }

    public ItemFilter(SmartFilter.Compound parent, Item matchItem) {
        super(parent);

        this.matchItem = matchItem;
    }

    public Item getMatchItem() {
        return matchItem;
    }

    @Override
    public boolean test(ItemStack stack) {
        return stack.is(matchItem);
    }

    @Override
    public String getStringArg() {
        return matchItem.arch$registryName().toString();
    }

    public static ItemFilter fromString(SmartFilter.Compound parent, String str) {
        try {
            Item item = BuiltInRegistries.ITEM.getOrThrow(ResourceKey.create(Registries.ITEM, new ResourceLocation(str)));
            return new ItemFilter(parent, item);
        } catch (IllegalArgumentException | IllegalStateException e) {
            throw new FilterException(e.getMessage(), e);
        }
    }

    @Override
    public ResourceLocation getId() {
        return ID;
    }
}
