package dev.ftb.mods.ftbfiltersystem.neoforge;

import dev.ftb.mods.ftbfiltersystem.api.FTBFilterSystemAPI;
import dev.ftb.mods.ftbfiltersystem.api.event.client.ClientFilterRegistrationEvent;
import dev.ftb.mods.ftbfiltersystem.api.neoforge.FTBFilterSystemClientEvent;
import dev.ftb.mods.ftbfiltersystem.client.FTBFilterSystemClient;
import dev.ftb.mods.ftblibrary.platform.event.NativeEventPosting;
import dev.ftb.mods.ftblibrary.util.neoforge.NeoEventHelper;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.lifecycle.ClientStartedEvent;
import net.neoforged.neoforge.common.NeoForge;

@Mod(value = FTBFilterSystemAPI.MOD_ID, dist = Dist.CLIENT)
public class FTBFilterSystemNeoForgeClient {
    public FTBFilterSystemNeoForgeClient() {
        var ffsClient = new FTBFilterSystemClient();

        IEventBus bus = NeoForge.EVENT_BUS;

        bus.addListener(ClientStartedEvent.class, _ -> ffsClient.onClientStarted(Minecraft.getInstance()));

        bus.addListener(FTBFilterSystemClientEvent.RegisterClientFilter.class,
                event -> ffsClient.registerClientFilters(event.getEventData().api()));

        NeoEventHelper.registerNeoEventPoster(bus, ClientFilterRegistrationEvent.Data.class, FTBFilterSystemClientEvent.RegisterClientFilter::new);
    }
}
