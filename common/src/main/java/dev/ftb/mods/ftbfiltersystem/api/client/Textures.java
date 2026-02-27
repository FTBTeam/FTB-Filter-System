package dev.ftb.mods.ftbfiltersystem.api.client;

import dev.ftb.mods.ftbfiltersystem.api.FTBFilterSystemAPI;
import net.minecraft.resources.Identifier;

public class Textures {
    public static final Identifier INFO_ICON = guiSpriteTexture("info");
    public static final Identifier BACKGROUND = guiSpriteTexture("background");
    public static final Identifier EDIT_BUTTON = guiSpriteTexture("edit");
    public static final Identifier EDIT_BUTTON_HI = guiSpriteTexture("edit_hi");

    private static Identifier guiSpriteTexture(String name) {
        return FTBFilterSystemAPI.rl(name);
    }
}
