package dev.ftb.mods.ftbfiltersystem.api.event;

import dev.ftb.mods.ftbfiltersystem.api.FTBFilterSystemRegistry;

import java.util.function.Consumer;

/// Fired when filters are being registered; use this event to register your custom filters.
public interface FilterRegistrationEvent extends Consumer<FilterRegistrationEvent.Data> {
//    Event<FilterRegistrationEvent> REGISTER = EventFactory.createLoop();

    record Data(FTBFilterSystemRegistry registry) {
    }

//    void registerFilters(FTBFilterSystemRegistry registry);
}
