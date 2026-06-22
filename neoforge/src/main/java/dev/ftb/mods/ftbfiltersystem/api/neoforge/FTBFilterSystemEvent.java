package dev.ftb.mods.ftbfiltersystem.api.neoforge;

import dev.ftb.mods.ftbfiltersystem.api.event.CustomFilterEvent;
import dev.ftb.mods.ftbfiltersystem.api.event.FilterRegistrationEvent;
import dev.ftb.mods.ftblibrary.api.neoforge.BaseEventWithData;
import dev.ftb.mods.ftblibrary.util.result.Outcome;
import net.neoforged.bus.api.ICancellableEvent;

public class FTBFilterSystemEvent {
    public static class RegisterFilter extends BaseEventWithData<FilterRegistrationEvent.Data> {
        public RegisterFilter(FilterRegistrationEvent.Data data) {
            super(data);
        }
    }

    public static class CustomFilter extends BaseEventWithData<CustomFilterEvent.Data> implements ICancellableEvent {
        private Outcome outcome = Outcome.PASS;

        public CustomFilter(CustomFilterEvent.Data data) {
            super(data);
        }

        public void setOutcome(Outcome outcome) {
            this.outcome = outcome;
            if (!outcome.isPass()) {
                setCanceled(true);
            }
        }

        public Outcome getOutcome() {
            return outcome;
        }
    }
}
