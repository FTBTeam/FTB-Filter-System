package dev.ftb.mods.ftbfiltersystem.neoforge;

import dev.ftb.mods.ftbfiltersystem.FTBFilterSystem;
import dev.ftb.mods.ftbfiltersystem.FilterSystemCommands;
import dev.ftb.mods.ftbfiltersystem.api.FTBFilterSystemAPI;
import dev.ftb.mods.ftbfiltersystem.api.event.CustomFilterEvent;
import dev.ftb.mods.ftbfiltersystem.api.event.FilterRegistrationEvent;
import dev.ftb.mods.ftbfiltersystem.api.neoforge.FTBFilterSystemEvent;
import dev.ftb.mods.ftblibrary.platform.event.NativeEventPosting;
import dev.ftb.mods.ftblibrary.util.neoforge.NeoEventHelper;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;

@Mod(FTBFilterSystemAPI.MOD_ID)
public class FTBFilterSystemNeoForge {
    public FTBFilterSystemNeoForge(IEventBus modBus) {
        var ffs = new FTBFilterSystem();

        var bus = NeoForge.EVENT_BUS;

        bus.addListener(ServerStartingEvent.class, event -> ffs.serverStarting());
        bus.addListener(RegisterCommandsEvent.class, event ->
                FilterSystemCommands.registerCommands(event.getDispatcher(), event.getBuildContext(), event.getCommandSelection()));

        bus.addListener(FTBFilterSystemEvent.RegisterFilter.class, event -> ffs.registerBuiltinFilters(event.getEventData().registry()));

        NeoDataComponents.COMPONENTS.register(modBus);

        NeoEventHelper.registerNeoEventPoster(bus, FilterRegistrationEvent.Data.class, FTBFilterSystemEvent.RegisterFilter::new);

        NativeEventPosting.get().registerEventWithResult(CustomFilterEvent.TYPE, data -> {
            var event = new FTBFilterSystemEvent.CustomFilter(data);
            NeoForge.EVENT_BUS.post(event);
            return event.getOutcome();
        });
    }
}
