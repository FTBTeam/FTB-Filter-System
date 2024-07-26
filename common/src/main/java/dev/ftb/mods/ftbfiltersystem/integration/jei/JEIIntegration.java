package dev.ftb.mods.ftbfiltersystem.integration.jei;

import dev.ftb.mods.ftbfiltersystem.api.FTBFilterSystemAPI;
import dev.ftb.mods.ftbfiltersystem.client.gui.ComponentConfigScreen;
import dev.ftb.mods.ftbfiltersystem.client.gui.ItemConfigScreen;
import dev.ftb.mods.ftbfiltersystem.client.gui.ModConfigScreen;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.IGuiHandlerRegistration;
import net.minecraft.resources.ResourceLocation;

@JeiPlugin
public class JEIIntegration implements IModPlugin {
    private final ResourceLocation ID = FTBFilterSystemAPI.rl("default");

    @Override
    public ResourceLocation getPluginUid() {
        return ID;
    }

    @Override
    public void registerGuiHandlers(IGuiHandlerRegistration registration) {
        registration.addGuiScreenHandler(ItemConfigScreen.class, new FFSScreenHandler<>());
        registration.addGhostIngredientHandler(ItemConfigScreen.class, new FFSGhostHandler<>());

        registration.addGuiScreenHandler(ComponentConfigScreen.class, new FFSScreenHandler<>());
        registration.addGhostIngredientHandler(ComponentConfigScreen.class, new FFSGhostHandler<>(s -> false));

        registration.addGuiScreenHandler(ModConfigScreen.class, new FFSScreenHandler<>());
        registration.addGhostIngredientHandler(ModConfigScreen.class, new FFSGhostHandler<>());
    }
}
