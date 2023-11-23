package dev.ftb.mods.ftbfiltersystem.filter;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.ftb.mods.ftbfiltersystem.api.*;
import dev.ftb.mods.ftbfiltersystem.api.filter.AbstractCompoundFilter;
import dev.ftb.mods.ftbfiltersystem.api.filter.SmartFilter;
import dev.ftb.mods.ftbfiltersystem.util.NBTUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public class FuzzyNbtFilter extends NbtFilter {
    public static final ResourceLocation ID = FTBFilterSystemAPI.rl("fuzzy_nbt");

    public FuzzyNbtFilter(@Nullable SmartFilter.Compound parent) {
        this(parent, new CompoundTag());
    }

    public FuzzyNbtFilter(SmartFilter.Compound parent, CompoundTag tag) {
        super(parent, tag);
    }

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    @Override
    public boolean test(ItemStack stack) {
        return tag.isEmpty() ?
                !stack.hasTag() || stack.getTag().isEmpty() :
                stack.hasTag() && fuzzyMatch(stack.getTag());
    }

    private boolean fuzzyMatch(CompoundTag toMatch) {
        return NBTUtil.compareNbt(tag, toMatch, true, true);
    }

    public static FuzzyNbtFilter fromString(SmartFilter.Compound parent, String str) {
        try {
            return new FuzzyNbtFilter(parent, NbtFilter.doParse(str));
        } catch (CommandSyntaxException e) {
            throw new FilterException("invalid NBT tag: " + str, e);
        }
    }
}
