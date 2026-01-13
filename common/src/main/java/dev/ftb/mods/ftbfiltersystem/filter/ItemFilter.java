package dev.ftb.mods.ftbfiltersystem.filter;

import dev.ftb.mods.ftbfiltersystem.api.FTBFilterSystemAPI;
import dev.ftb.mods.ftbfiltersystem.api.FilterException;
import dev.ftb.mods.ftbfiltersystem.api.filter.AbstractSmartFilter;
import dev.ftb.mods.ftbfiltersystem.api.filter.SmartFilter;
import dev.ftb.mods.ftbfiltersystem.util.RegExParser;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.NoSuchElementException;
import java.util.regex.Pattern;

public class ItemFilter extends AbstractSmartFilter {
    public static final ResourceLocation ID = FTBFilterSystemAPI.rl("item");

    private final Item matchItem;
    private final String patternArg;
    private final Pattern patternRegex;

    public ItemFilter(SmartFilter.Compound parent) {
        this(parent, Items.STONE);
    }

    public ItemFilter(SmartFilter.Compound parent, Item matchItem) {
        super(parent);

        this.matchItem = matchItem;
        this.patternArg = null;
        this.patternRegex = null;
    }

    private ItemFilter(SmartFilter.Compound parent, String patternArg, Pattern patternRegex) {
        super(parent);

        this.matchItem = Items.AIR;
        this.patternArg = patternArg;
        this.patternRegex = patternRegex;
    }

    public Item getMatchItem() {
        return matchItem;
    }

    @Override
    public boolean test(ItemStack stack) {
        if (patternRegex != null) {
            var name = stack.getItem().arch$registryName().toString();
            return patternRegex.matcher(name).matches();
        }

        return stack.is(matchItem);
    }

    @Override
    public String getStringArg(HolderLookup.Provider registryAccess) {
        return patternArg == null ? matchItem.arch$registryName().toString() : patternArg;
    }

    public static ItemFilter fromString(SmartFilter.Compound parent, String str, HolderLookup.Provider registryAccess) {
        try {
            Pattern p = RegExParser.parseRegex(str);
            if (p != null) {
                return new ItemFilter(parent, str, p);
            }
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
