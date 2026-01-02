package dev.ftb.mods.ftbfiltersystem.filter;

import dev.ftb.mods.ftbfiltersystem.api.FTBFilterSystemAPI;
import dev.ftb.mods.ftbfiltersystem.api.FilterException;
import dev.ftb.mods.ftbfiltersystem.api.filter.AbstractSmartFilter;
import dev.ftb.mods.ftbfiltersystem.api.filter.SmartFilter;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.NoSuchElementException;

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
    public String getStringArg(HolderLookup.Provider registryAccess) {
        return matchItem.arch$registryName().toString();
    }

    public static ItemFilter fromString(SmartFilter.Compound parent, String str, HolderLookup.Provider registryAccess) {
        try {
            var item = registryAccess.lookup(Registries.ITEM).orElseThrow()
                    .getOrThrow(ResourceKey.create(Registries.ITEM, ResourceLocation.tryParse(str)));
            return new ItemFilter(parent, item.value());
        } catch (IllegalArgumentException | IllegalStateException | NoSuchElementException e) {
            throw new FilterException(e.getMessage(), e);
        }
    }

    @Override
    public ResourceLocation getId() {
        return ID;
    }
}
