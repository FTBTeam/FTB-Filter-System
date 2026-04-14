package dev.ftb.mods.ftbfiltersystem.network;

import dev.ftb.mods.ftblibrary.util.NetworkHelper;

public interface FTBFilterSystemNet {
    static void init() {
        NetworkHelper.registerC2S(SyncFilterMessage.TYPE, SyncFilterMessage.STREAM_CODEC, SyncFilterMessage::handle);
    }
}
