package dev.ftb.mods.ftbfiltersystem.filter;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.ftb.mods.ftbfiltersystem.api.FTBFilterSystemAPI;
import dev.ftb.mods.ftbfiltersystem.api.FilterException;
import dev.ftb.mods.ftbfiltersystem.api.filter.AbstractSmartFilter;
import dev.ftb.mods.ftbfiltersystem.api.filter.SmartFilter;
import dev.ftb.mods.ftbfiltersystem.util.PlatformUtil;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.TagParser;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ComponentFilter extends AbstractSmartFilter {
    public static final ResourceLocation ID = FTBFilterSystemAPI.rl("component");
    protected final DataComponentMap map;
    private final boolean fuzzyMatch;

    public ComponentFilter(@Nullable SmartFilter.Compound parent) {
        this(parent, true, DataComponentMap.EMPTY);
    }

    public ComponentFilter(SmartFilter.Compound parent, boolean fuzzyMatch, DataComponentMap map) {
        super(parent);

        this.fuzzyMatch = fuzzyMatch;
        this.map = map;
    }

    @NotNull
    public static String getPrefixStr(boolean fuzzy) {
        return fuzzy ? "fuzzy:" : "strict:";
    }

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    @Override
    public boolean test(ItemStack stack) {
        //noinspection UnreachableCode
        return PlatformUtil.hasComponentPatch(stack) ?
                (fuzzyMatch ? fuzzyMatch(stack.getComponents()) : stack.getComponents().equals(map)) :
                map.isEmpty();
    }

    private boolean fuzzyMatch(DataComponentMap toMatch) {
        return map.stream().allMatch(tc -> toMatch.has(tc.type()) && toMatch.get(tc.type()).equals(tc.value()));
    }

    @Override
    public String getStringArg() {
        try {
            Tag tag = DataComponentMap.CODEC.encodeStart(NbtOps.INSTANCE, map).getOrThrow();
            return getPrefixStr(fuzzyMatch) + tag.toString();
        } catch (IllegalStateException e) {
            return "";
        }
    }

    public DataComponentMap getComponentMap() {
        return map;
    }

    public boolean isFuzzyMatch() {
        return fuzzyMatch;
    }

    public static ComponentFilter fromString(SmartFilter.Compound parent, String str) {
        try {
            boolean fuzzy = true;
            if (str.startsWith("strict:") || str.startsWith("fuzzy:")) {
                fuzzy = str.startsWith("fuzzy:");
                str = str.substring(str.indexOf(':') + 1);
            }
            DataComponentMap map = DataComponentMap.CODEC.parse(NbtOps.INSTANCE, parseNBT(str)).getOrThrow(FilterException::new);
            return new ComponentFilter(parent, fuzzy, map);
        } catch (CommandSyntaxException e) {
            throw new FilterException("invalid NBT tag: " + str, e);
        }
    }

    private static CompoundTag parseNBT(String str) throws CommandSyntaxException {
        if (!str.startsWith("{") && !str.endsWith("}")) {
            str = "{" + str + "}";  // just a convenience; we'll still emit strings with enclosing braces
        }
        return TagParser.parseTag(str);
    }
}
