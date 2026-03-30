package dev.ftb.mods.ftbfiltersystem.api.event.client;

import dev.ftb.mods.ftbfiltersystem.api.client.FTBFilterSystemClientAPI;

import java.util.function.Consumer;

/// Fired when filters are being registered on the client; use this event to register configuration screens for your
/// custom filters.
public interface ClientFilterRegistrationEvent extends Consumer<ClientFilterRegistrationEvent.Data> {
//    Event<ClientFilterRegistrationEvent> REGISTER = EventFactory.createLoop();

    record Data(FTBFilterSystemClientAPI api) {
    }

//    void registerFilters(FTBFilterSystemClientAPI api);
}
