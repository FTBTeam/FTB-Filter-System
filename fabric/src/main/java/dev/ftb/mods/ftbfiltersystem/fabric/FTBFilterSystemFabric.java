package dev.ftb.mods.ftbfiltersystem.fabric;

import dev.ftb.mods.ftbfiltersystem.FTBFilterSystem;
import dev.ftb.mods.ftbfiltersystem.FilterSystemCommands;
import dev.ftb.mods.ftbfiltersystem.api.event.CustomFilterEvent;
import dev.ftb.mods.ftbfiltersystem.api.event.FilterRegistrationEvent;
import dev.ftb.mods.ftbfiltersystem.api.fabric.FTBFilterSystemEvents;
import dev.ftb.mods.ftbfiltersystem.registry.ModItems;
import dev.ftb.mods.ftblibrary.FTBLibrary;
import dev.ftb.mods.ftblibrary.platform.event.NativeEventPosting;
import dev.ftb.mods.ftblibrary.util.fabric.FabricEventHelper;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.creativetab.v1.CreativeModeTabEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;

public class FTBFilterSystemFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        var ffs = new FTBFilterSystem();

        ServerLifecycleEvents.SERVER_STARTING.register(_ -> ffs.serverStarting());
        CommandRegistrationCallback.EVENT.register(FilterSystemCommands::registerCommands);

        FTBFilterSystemEvents.FILTER_REGISTRATION.register(event -> ffs.registerBuiltinFilters(event.registry()));

        CreativeModeTabEvents.MODIFY_OUTPUT_ALL.register((tab, output) -> {
            if (tab == FTBLibrary.getCreativeModeTab().get()) {
                output.accept(ModItems.SMART_FILTER.get());
            }
        });

        FabricEventHelper.registerFabricEventPoster(FilterRegistrationEvent.Data.class, FTBFilterSystemEvents.FILTER_REGISTRATION);

        NativeEventPosting.get().registerEventWithResult(CustomFilterEvent.TYPE,
                data -> FTBFilterSystemEvents.CUSTOM_FILTER.invoker().match(data));
    }
}
