package dev.ftb.mods.ftbfiltersystem.forge;

import dev.architectury.platform.forge.EventBuses;
import dev.ftb.mods.ftbfiltersystem.FTBFilterSystem;
import dev.ftb.mods.ftbfiltersystem.api.FTBFilterSystemAPI;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(FTBFilterSystemAPI.MOD_ID)
public class FTBFilterSystemForge {
    public FTBFilterSystemForge() {
        // Submit our event bus to let architectury register our content on the right time
        EventBuses.registerModEventBus(FTBFilterSystemAPI.MOD_ID, FMLJavaModLoadingContext.get().getModEventBus());
        new FTBFilterSystem();
    }
}
