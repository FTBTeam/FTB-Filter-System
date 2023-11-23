package dev.ftb.mods.ftbfiltersystem.fabric;

import dev.ftb.mods.ftbfiltersystem.FTBFilterSystem;
import net.fabricmc.api.ModInitializer;

public class FTBFilterSystemFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        new FTBFilterSystem();
    }
}
