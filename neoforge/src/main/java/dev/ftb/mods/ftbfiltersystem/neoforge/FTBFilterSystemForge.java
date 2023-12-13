package dev.ftb.mods.ftbfiltersystem.neoforge;

import dev.ftb.mods.ftbfiltersystem.FTBFilterSystem;
import dev.ftb.mods.ftbfiltersystem.api.FTBFilterSystemAPI;
import net.neoforged.fml.common.Mod;

@Mod(FTBFilterSystemAPI.MOD_ID)
public class FTBFilterSystemForge {
    public FTBFilterSystemForge() {
        // Submit our event bus to let architectury register our content on the right time
//        EventBuses.registerModEventBus(FTBFilterSystemAPI.MOD_ID, FMLJavaModLoadingContext.get().getModEventBus());
        new FTBFilterSystem();
    }
}
