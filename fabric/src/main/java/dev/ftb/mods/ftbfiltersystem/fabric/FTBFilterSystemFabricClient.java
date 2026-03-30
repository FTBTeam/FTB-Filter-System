package dev.ftb.mods.ftbfiltersystem.fabric;

import dev.ftb.mods.ftbfiltersystem.api.event.client.ClientFilterRegistrationEvent;
import dev.ftb.mods.ftbfiltersystem.api.fabric.FTBFilterSystemClientEvents;
import dev.ftb.mods.ftbfiltersystem.client.FTBFilterSystemClient;
import dev.ftb.mods.ftblibrary.util.fabric.FabricEventHelper;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;

public class FTBFilterSystemFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        var ffsClient = new FTBFilterSystemClient();

        ClientLifecycleEvents.CLIENT_STARTED.register(ffsClient::onClientStarted);

        FTBFilterSystemClientEvents.CLIENT_FILTER_REGISTRATION.register(event -> ffsClient.registerClientFilters(event.api()));

        FabricEventHelper.registerFabricEventPoster(ClientFilterRegistrationEvent.Data.class, FTBFilterSystemClientEvents.CLIENT_FILTER_REGISTRATION);
    }
}
