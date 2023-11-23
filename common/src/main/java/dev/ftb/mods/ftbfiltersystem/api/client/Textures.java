package dev.ftb.mods.ftbfiltersystem.api.client;

import dev.ftb.mods.ftbfiltersystem.api.FTBFilterSystemAPI;
import net.minecraft.resources.ResourceLocation;

public class Textures {
    public static final ResourceLocation INFO_ICON = guiTexture("info.png");
    public static final ResourceLocation CHECKBOX = guiTexture("checkbox.png");
    public static final ResourceLocation BACKGROUND = guiTexture("background.png");
    public static final ResourceLocation EDIT_BUTTON = guiTexture("edit.png");

    private static ResourceLocation guiTexture(String name) {
        return FTBFilterSystemAPI.rl("textures/gui/" + name);
    }
}
