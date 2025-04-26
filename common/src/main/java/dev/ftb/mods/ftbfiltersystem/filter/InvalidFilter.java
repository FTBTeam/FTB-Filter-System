package dev.ftb.mods.ftbfiltersystem.filter;

import dev.ftb.mods.ftbfiltersystem.api.FTBFilterSystemAPI;
import dev.ftb.mods.ftbfiltersystem.api.filter.SmartFilter;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

/**
 * Special filter which isn't registered, but is cached when parsing a filter string threw an exception.
 */
public class InvalidFilter implements SmartFilter {
    private static final ResourceLocation ID = FTBFilterSystemAPI.rl("_invalid");

    private final String reason;

    public InvalidFilter(String reason) {
        this.reason = "<" + reason + ">";
    }

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    @Override
    public @Nullable SmartFilter.Compound getParent() {
        return null;
    }

    @Override
    public Component getDisplayName() {
        return Component.literal("invalid").withStyle(ChatFormatting.DARK_RED, ChatFormatting.ITALIC);
    }

    @Override
    public String getStringArg() {
        return reason;
    }

    @Override
    public boolean test(ItemStack stack) {
        return false;
    }
}
