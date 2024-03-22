package dev.ftb.mods.ftbfiltersystem.api.client;

import dev.ftb.mods.ftbfiltersystem.api.FTBFilterSystemAPI;
import net.minecraft.resources.ResourceLocation;

public class Textures {
    public static final ResourceLocation INFO_ICON = guiSpriteTexture("info");
    public static final ResourceLocation BACKGROUND = guiSpriteTexture("background");
    public static final ResourceLocation EDIT_BUTTON = guiSpriteTexture("edit");
    public static final ResourceLocation EDIT_BUTTON_HI = guiSpriteTexture("edit_hi");

    private static ResourceLocation guiSpriteTexture(String name) {
        return FTBFilterSystemAPI.rl(name);
    }
}
