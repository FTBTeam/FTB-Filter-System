package dev.ftb.mods.ftbfiltersystem.api.fabric;

import dev.ftb.mods.ftbfiltersystem.api.event.client.ClientFilterRegistrationEvent;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

public class FTBFilterSystemClientEvents {
    public static final Event<ClientFilterRegistrationEvent> CLIENT_FILTER_REGISTRATION
            = EventFactory.createArrayBacked(ClientFilterRegistrationEvent.class,
            callbacks -> data -> {
                for (var c : callbacks) {
                    c.accept(data);
                }
            });
}
