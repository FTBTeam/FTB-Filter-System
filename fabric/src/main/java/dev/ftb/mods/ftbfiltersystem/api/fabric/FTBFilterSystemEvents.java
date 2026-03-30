package dev.ftb.mods.ftbfiltersystem.api.fabric;

import dev.ftb.mods.ftbfiltersystem.api.event.CustomFilterEvent;
import dev.ftb.mods.ftbfiltersystem.api.event.FilterRegistrationEvent;
import dev.ftb.mods.ftblibrary.util.result.Outcome;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

public class FTBFilterSystemEvents {
    public static final Event<FilterRegistrationEvent> FILTER_REGISTRATION
            = EventFactory.createArrayBacked(FilterRegistrationEvent.class,
            callbacks -> data -> {
                for (var c : callbacks) {
                    c.accept(data);
                }
            });


    public static final Event<CustomFilterEvent> CUSTOM_FILTER
            = EventFactory.createArrayBacked(CustomFilterEvent.class,
            callbacks -> data -> {
                for (var c : callbacks) {
                    Outcome outcome = c.match(data);
                    if (!outcome.isPass()) {
                        return outcome;
                    }
                }
                return Outcome.PASS;
            });
}
