package dev.ftb.mods.ftbfiltersystem.filter;

import dev.ftb.mods.ftbfiltersystem.api.FTBFilterSystemAPI;
import dev.ftb.mods.ftbfiltersystem.api.FilterException;
import dev.ftb.mods.ftbfiltersystem.api.filter.AbstractSmartFilter;
import dev.ftb.mods.ftbfiltersystem.api.filter.SmartFilter;
import dev.ftb.mods.ftbfiltersystem.util.RegExParser;
import net.minecraft.ResourceLocationException;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import java.util.regex.Pattern;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class ItemTagFilter extends AbstractSmartFilter {
    public static final ResourceLocation ID = FTBFilterSystemAPI.rl("item_tag");
    private TagKey<Item> tagKey;
    private final String patternArg;
    private final Pattern patternRegex;

    public ItemTagFilter(SmartFilter.Compound parent) {
        this(parent, ItemTags.DIRT);
    }

    public ItemTagFilter(SmartFilter.Compound parent, TagKey<Item> tagKey) {
        super(parent);

        this.tagKey = tagKey;
        this.patternArg = null;
        this.patternRegex = null;
    }

    private ItemTagFilter(SmartFilter.Compound parent, String patternArg, Pattern patternRegex) {
        super(parent);

        this.tagKey = null;
        this.patternArg = patternArg;
        this.patternRegex = patternRegex;
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
        if (patternRegex != null) {
            var holder = stack.getItem().builtInRegistryHolder();
            return holder.tags().anyMatch(tag -> patternRegex.matcher(tag.location().toString()).matches());
        }

        return stack.is(tagKey);
    }

    @Override
    public String getStringArg(HolderLookup.Provider registryAccess) {
        return patternArg == null ? tagKey.location().toString() : patternArg;
    }

    public static ItemTagFilter fromString(SmartFilter.Compound parent, String str, HolderLookup.Provider ignored2) {
        try {
            Pattern p = RegExParser.parseRegex(str);
            if (p != null) {
                return new ItemTagFilter(parent, str, p);
            }
            return new ItemTagFilter(parent, TagKey.create(Registries.ITEM, ResourceLocation.tryParse(str)));
        } catch (ResourceLocationException e) {
            throw new FilterException("invalid tag key " + str, e);
        }
    }
}
