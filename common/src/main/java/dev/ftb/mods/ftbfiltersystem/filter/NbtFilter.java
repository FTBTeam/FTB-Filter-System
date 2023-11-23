package dev.ftb.mods.ftbfiltersystem.filter;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.ftb.mods.ftbfiltersystem.api.filter.AbstractCompoundFilter;
import dev.ftb.mods.ftbfiltersystem.api.filter.AbstractSmartFilter;
import dev.ftb.mods.ftbfiltersystem.api.FTBFilterSystemAPI;
import dev.ftb.mods.ftbfiltersystem.api.FilterException;
import dev.ftb.mods.ftbfiltersystem.api.filter.SmartFilter;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.TagParser;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public class NbtFilter extends AbstractSmartFilter {
    public static final ResourceLocation ID = FTBFilterSystemAPI.rl("nbt");
    protected final CompoundTag tag;

    public NbtFilter(@Nullable SmartFilter.Compound parent) {
        this(parent, new CompoundTag());
    }

    public NbtFilter(SmartFilter.Compound parent, CompoundTag tag) {
        super(parent);

        this.tag = tag;
    }

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    @Override
    public boolean test(ItemStack stack) {
        return tag.isEmpty() ?
                !stack.hasTag() || stack.getTag().isEmpty() :
                tag.equals(stack.getTag());
    }

    @Override
    public String getStringArg() {
        return tag.toString();
    }

    public static NbtFilter fromString(SmartFilter.Compound parent, String str) {
        try {
            return new NbtFilter(parent, doParse(str));
        } catch (CommandSyntaxException e) {
            throw new FilterException("invalid NBT tag: " + str, e);
        }
    }

    static CompoundTag doParse(String str) throws CommandSyntaxException {
        if (!str.startsWith("{") && !str.endsWith("}")) {
            str = "{" + str + "}";  // just a convenience; we'll still emit strings with enclosing braces
        }
        return TagParser.parseTag(str);
    }
}
