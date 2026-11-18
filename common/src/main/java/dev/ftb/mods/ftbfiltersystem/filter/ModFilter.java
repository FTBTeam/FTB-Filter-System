package dev.ftb.mods.ftbfiltersystem.filter;

import com.mojang.datafixers.util.Either;
import dev.ftb.mods.ftbfiltersystem.api.FTBFilterSystemAPI;
import dev.ftb.mods.ftbfiltersystem.api.FilterException;
import dev.ftb.mods.ftbfiltersystem.api.filter.AbstractSmartFilter;
import dev.ftb.mods.ftbfiltersystem.api.filter.SmartFilter;
import dev.ftb.mods.ftbfiltersystem.util.GlobRegexMatcher;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.regex.PatternSyntaxException;

public class ModFilter extends AbstractSmartFilter {
    public static final ResourceLocation ID = FTBFilterSystemAPI.rl("mod");

    private final Either<String, GlobRegexMatcher> either;

    public ModFilter(SmartFilter.Compound parent) {
        this(parent, "minecraft");
    }

    public ModFilter(SmartFilter.Compound parent, String modId) {
        this(parent, Either.left(modId));
    }

    public ModFilter(SmartFilter.Compound parent, Either<String, GlobRegexMatcher> either) {
        super(parent);
        this.either = either;
    }

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    @Override
    public boolean test(ItemStack stack) {
        String namespace = stack.getItem().arch$registryName().getNamespace();
        return either.map(
                namespace::equals,
                parser -> parser.match(namespace)
        );
    }

    @Override
    public String getStringArg(HolderLookup.Provider registryAccess) {
        return either.map(modid -> modid, GlobRegexMatcher::raw);
    }

    public static ModFilter fromString(SmartFilter.Compound parent, String str, HolderLookup.Provider ignored2) {
        try {
            return new ModFilter(parent, GlobRegexMatcher.parseWithFallback(str, () -> str));
        } catch (PatternSyntaxException e) {
            throw new FilterException("invalid glob/regex " + str, e);
        }
    }
}
