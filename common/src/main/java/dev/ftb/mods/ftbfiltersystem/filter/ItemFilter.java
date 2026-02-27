package dev.ftb.mods.ftbfiltersystem.filter;

import com.mojang.datafixers.util.Either;
import dev.ftb.mods.ftbfiltersystem.api.FTBFilterSystemAPI;
import dev.ftb.mods.ftbfiltersystem.api.FilterException;
import dev.ftb.mods.ftbfiltersystem.api.filter.AbstractSmartFilter;
import dev.ftb.mods.ftbfiltersystem.api.filter.SmartFilter;
import dev.ftb.mods.ftbfiltersystem.util.GlobRegexMatcher;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.NoSuchElementException;

public class ItemFilter extends AbstractSmartFilter {
    public static final Identifier ID = FTBFilterSystemAPI.rl("item");

    private final Either<Item, GlobRegexMatcher> either;

    public ItemFilter(SmartFilter.Compound parent) {
        this(parent, Items.STONE);
    }

    public ItemFilter(SmartFilter.Compound parent, Item matchItem) {
        this(parent, Either.left(matchItem));
    }

    public ItemFilter(SmartFilter.Compound parent, Either<Item, GlobRegexMatcher> either) {
        super(parent);
        this.either = either;
    }

    public Item getMatchItem() {
        return either.left().orElse(Items.AIR);
    }

    @Override
    public boolean test(ItemStack stack) {
        return either.map(
                stack::is,
                compiled -> compiled.match(stack.getItem().arch$registryName().toString())
        );
    }

    @Override
    public String getStringArg(HolderLookup.Provider registryAccess) {
        return either.map(item -> item.arch$registryName().toString(), GlobRegexMatcher::raw);
    }

    public static ItemFilter fromString(SmartFilter.Compound parent, String str, HolderLookup.Provider registryAccess) {
        try {
            return new ItemFilter(parent, GlobRegexMatcher.parseWithFallback(str, () ->
                    registryAccess.lookup(Registries.ITEM).orElseThrow()
                            .getOrThrow(ResourceKey.create(Registries.ITEM, Identifier.tryParse(str))).value())
            );
        } catch (IllegalArgumentException | IllegalStateException | NoSuchElementException e) {
            throw new FilterException(e.getMessage(), e);
        }
    }

    @Override
    public Identifier getId() {
        return ID;
    }
}
