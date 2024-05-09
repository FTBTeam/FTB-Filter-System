package dev.ftb.mods.ftbfiltersystem.network;

import dev.architectury.networking.NetworkManager;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public interface FTBFilterSystemNet {
    static void init() {
        NetworkManager.registerReceiver(NetworkManager.c2s(), SyncFilterMessage.TYPE, SyncFilterMessage.STREAM_CODEC, SyncFilterMessage::handle);
    }

    static <B extends FriendlyByteBuf, V extends Enum<V>> StreamCodec<B, V> enumCodec(Class<V> enumClass) {
        return new StreamCodec<>() {
            @Override
            public V decode(B buf) {
                return buf.readEnum(enumClass);
            }

            @Override
            public void encode(B buf, V value) {
                buf.writeEnum(value);
            }
        };
    }
}
