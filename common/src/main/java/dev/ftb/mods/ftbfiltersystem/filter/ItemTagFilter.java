package dev.ftb.mods.ftbfiltersystem.filter;

import dev.ftb.mods.ftbfiltersystem.api.FTBFilterSystemAPI;
import dev.ftb.mods.ftbfiltersystem.api.FilterException;
import dev.ftb.mods.ftbfiltersystem.api.filter.AbstractSmartFilter;
import dev.ftb.mods.ftbfiltersystem.api.filter.SmartFilter;
import net.minecraft.ResourceLocationException;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class ItemTagFilter extends AbstractSmartFilter {
    public static final ResourceLocation ID = FTBFilterSystemAPI.rl("item_tag");
    private final TagKey<Item> tagKey;

    public ItemTagFilter(SmartFilter.Compound parent) {
        this(parent, ItemTags.DIRT);
    }

    public ItemTagFilter(SmartFilter.Compound parent, TagKey<Item> tagKey) {
        super(parent);

        this.tagKey = tagKey;
    }

    public TagKey<Item> getTagKey() {
        return tagKey;
    }

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    @Override
    public boolean test(ItemStack stack) {
        return stack.is(tagKey);
    }

    @Override
    public String getStringArg(HolderLookup.Provider registryAccess) {
        return tagKey.location().toString();
    }

    public static ItemTagFilter fromString(SmartFilter.Compound parent, String str, HolderLookup.Provider ignored2) {
        try {
            return new ItemTagFilter(parent, TagKey.create(Registries.ITEM, ResourceLocation.tryParse(str)));
        } catch (ResourceLocationException e) {
            throw new FilterException("invalid tag key " + str, e);
        }
    }
}
