package dev.ftb.mods.ftbfiltersystem.filter;

import com.mojang.datafixers.util.Either;
import dev.ftb.mods.ftbfiltersystem.api.FTBFilterSystemAPI;
import dev.ftb.mods.ftbfiltersystem.api.FilterException;
import dev.ftb.mods.ftbfiltersystem.api.filter.AbstractSmartFilter;
import dev.ftb.mods.ftbfiltersystem.api.filter.SmartFilter;
import dev.ftb.mods.ftbfiltersystem.util.GlobRegexMatcher;
import net.minecraft.ResourceLocationException;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.regex.PatternSyntaxException;

public class ItemTagFilter extends AbstractSmartFilter {
    public static final ResourceLocation ID = FTBFilterSystemAPI.rl("item_tag");
    private final Either<TagKey<Item>, GlobRegexMatcher> either;

    public ItemTagFilter(SmartFilter.Compound parent) {
        this(parent, ItemTags.DIRT);
    }

    public ItemTagFilter(@Nullable Compound parent, TagKey<Item> tagKey) {
        this(parent, Either.left(tagKey));
    }

    public ItemTagFilter(SmartFilter.Compound parent, Either<TagKey<Item>, GlobRegexMatcher> either) {
        super(parent);
        this.either = either;
    }

    public TagKey<Item> getTagKey() {
        return either.left().orElse(null);
    }

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    @Override
    public boolean test(ItemStack stack) {
        return either.map(
                stack::is,
                compiled -> stack.getItem().builtInRegistryHolder().tags()
                        .anyMatch(tag -> compiled.match(tag.location().toString())));
    }

    @Override
    public String getStringArg(HolderLookup.Provider registryAccess) {
        return either.map(tagKey -> tagKey.location().toString(), GlobRegexMatcher::raw);
    }

    public static ItemTagFilter fromString(SmartFilter.Compound parent, String str, HolderLookup.Provider ignored2) {
        try {
            return new ItemTagFilter(parent, GlobRegexMatcher.parseWithFallback(str, () -> TagKey.create(Registries.ITEM, ResourceLocation.tryParse(str))));
        } catch (ResourceLocationException e) {
            throw new FilterException("invalid tag key " + str, e);
        } catch (PatternSyntaxException e) {
            throw new FilterException("invalid glob/regex " + str, e);
        }
    }
}
