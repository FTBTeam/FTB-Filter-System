package dev.ftb.mods.ftbfiltersystem.api.neoforge;

import dev.ftb.mods.ftbfiltersystem.api.event.client.ClientFilterRegistrationEvent;
import dev.ftb.mods.ftblibrary.api.neoforge.BaseEventWithData;

public class FTBFilterSystemClientEvent {
    public static class RegisterClientFilter extends BaseEventWithData<ClientFilterRegistrationEvent.Data> {
        public RegisterClientFilter(ClientFilterRegistrationEvent.Data data) {
            super(data);
        }
    }
}
