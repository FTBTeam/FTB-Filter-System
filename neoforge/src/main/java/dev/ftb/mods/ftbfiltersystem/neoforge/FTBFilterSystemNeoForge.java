package dev.ftb.mods.ftbfiltersystem.neoforge;

import dev.ftb.mods.ftbfiltersystem.FTBFilterSystem;
import dev.ftb.mods.ftbfiltersystem.api.FTBFilterSystemAPI;
import net.neoforged.fml.common.Mod;

@Mod(FTBFilterSystemAPI.MOD_ID)
public class FTBFilterSystemNeoForge {
    public FTBFilterSystemNeoForge() {
        new FTBFilterSystem();
    }
}
