package dev.ftb.mods.ftbfiltersystem.api.event;

import dev.architectury.event.Event;
import dev.architectury.event.EventFactory;
import dev.ftb.mods.ftbfiltersystem.api.FTBFilterSystemRegistry;

/**
 * Fired when filters are being registered; use this event to register your custom filters.
 */
public interface FilterRegistrationEvent {
    Event<FilterRegistrationEvent> REGISTER = EventFactory.createLoop();

    void registerFilters(FTBFilterSystemRegistry registry);
}
