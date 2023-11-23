package dev.ftb.mods.ftbfiltersystem.network;

import dev.architectury.networking.simple.MessageType;
import dev.architectury.networking.simple.SimpleNetworkManager;
import dev.ftb.mods.ftbfiltersystem.api.FTBFilterSystemAPI;

public interface FTBFilterSystemNet {
    SimpleNetworkManager NET = SimpleNetworkManager.create(FTBFilterSystemAPI.MOD_ID);

    MessageType SYNC_FILTER = NET.registerC2S("sync_filter", SyncFilterMessage::new);

    static void init() {
    }
}
