package dev.ftb.mods.ftbfiltersystem.api.event.client;

import dev.architectury.event.Event;
import dev.architectury.event.EventFactory;
import dev.ftb.mods.ftbfiltersystem.api.client.FTBFilterSystemClientAPI;

/**
 * Fired when filters are being registered on the client; use this event to register configuration screens for your
 * custom filters.
 */
public interface ClientFilterRegistrationEvent {
    Event<ClientFilterRegistrationEvent> REGISTER = EventFactory.createLoop();

    void registerFilters(FTBFilterSystemClientAPI api);
}
