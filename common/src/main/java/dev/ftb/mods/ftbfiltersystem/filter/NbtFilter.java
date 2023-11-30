package dev.ftb.mods.ftbfiltersystem.filter;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.ftb.mods.ftbfiltersystem.api.FTBFilterSystemAPI;
import dev.ftb.mods.ftbfiltersystem.api.FilterException;
import dev.ftb.mods.ftbfiltersystem.api.filter.AbstractSmartFilter;
import dev.ftb.mods.ftbfiltersystem.api.filter.SmartFilter;
import dev.ftb.mods.ftbfiltersystem.util.NBTUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.TagParser;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class NbtFilter extends AbstractSmartFilter {
    public static final ResourceLocation ID = FTBFilterSystemAPI.rl("nbt");
    protected final CompoundTag tag;
    private final boolean fuzzyMatch;

    public NbtFilter(@Nullable SmartFilter.Compound parent) {
        this(parent, true, new CompoundTag());
    }

    public NbtFilter(SmartFilter.Compound parent, boolean fuzzyMatch, CompoundTag tag) {
        super(parent);

        this.fuzzyMatch = fuzzyMatch;
        this.tag = tag;
    }

    @NotNull
    public static String getNBTPrefix(boolean fuzzy) {
        return fuzzy ? "fuzzy:" : "strict:";
    }

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    @Override
    public boolean test(ItemStack stack) {
        return tag.isEmpty() ?
                stack.getTag() == null || stack.getTag().isEmpty() :
                (fuzzyMatch ? fuzzyMatch(stack.getTag()) : tag.equals(stack.getTag()));
    }

    private boolean fuzzyMatch(CompoundTag toMatch) {
        return tag != null && NBTUtil.compareNbt(tag, toMatch, true, true);
    }

    @Override
    public String getStringArg() {
        return getNBTPrefix(fuzzyMatch) + tag.toString();
    }

    public CompoundTag getTag() {
        return tag;
    }

    public boolean isFuzzyMatch() {
        return fuzzyMatch;
    }

    public static NbtFilter fromString(SmartFilter.Compound parent, String str) {
        try {
            boolean fuzzy = true;
            if (str.startsWith("strict:") || str.startsWith("fuzzy:")) {
                fuzzy = str.startsWith("fuzzy:");
                str = str.substring(str.indexOf(':') + 1);
            }
            return new NbtFilter(parent, fuzzy, parseNBT(str));
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
