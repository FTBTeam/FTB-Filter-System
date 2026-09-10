package dev.ftb.mods.ftbfiltersystem.fabric;

import dev.ftb.mods.ftbfiltersystem.api.event.client.ClientFilterRegistrationEvent;
import dev.ftb.mods.ftbfiltersystem.api.fabric.FTBFilterSystemClientEvents;
import dev.ftb.mods.ftbfiltersystem.client.FTBFilterSystemClient;
import dev.ftb.mods.ftblibrary.util.fabric.FabricEventHelper;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenKeyboardEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.KeyEvent;

public class FTBFilterSystemFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        var ffsClient = new FTBFilterSystemClient();

        ClientLifecycleEvents.CLIENT_STARTED.register(ffsClient::onClientStarted);

        FTBFilterSystemClientEvents.CLIENT_FILTER_REGISTRATION.register(event -> ffsClient.registerClientFilters(event.api()));

        ScreenEvents.AFTER_INIT.register((_, screen, _, _) ->
                ScreenKeyboardEvents.afterKeyPress(screen).register((_, event) -> ffsClient.onKeyPressed(event))
        );

        FabricEventHelper.registerFabricEventPoster(ClientFilterRegistrationEvent.Data.class, FTBFilterSystemClientEvents.CLIENT_FILTER_REGISTRATION);
    }
}
