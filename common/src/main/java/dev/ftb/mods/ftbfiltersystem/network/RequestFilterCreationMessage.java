package dev.ftb.mods.ftbfiltersystem.network;

import dev.ftb.mods.ftbfiltersystem.api.FTBFilterSystemAPI;
import dev.ftb.mods.ftbfiltersystem.util.FFSUtil;
import dev.ftb.mods.ftblibrary.platform.network.PacketContext;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.permissions.Permissions;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public record RequestFilterCreationMessage(boolean hotbarOnly) implements CustomPacketPayload {
    public static final StreamCodec<FriendlyByteBuf, RequestFilterCreationMessage> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.BOOL, RequestFilterCreationMessage::hotbarOnly,
            RequestFilterCreationMessage::new
    );

    static final Type<RequestFilterCreationMessage> TYPE = new Type<>(FTBFilterSystemAPI.rl("request_filter_creation"));

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(RequestFilterCreationMessage message, PacketContext packetContext) {
        if (packetContext.player() instanceof ServerPlayer sp && sp.permissions().hasPermission(Permissions.COMMANDS_GAMEMASTER)) {
            List<ItemStack> invItems = new ArrayList<>();
            if (message.hotbarOnly) {
                for (int i = 0; i < 9; i++) {
                    invItems.add(sp.getInventory().getItem(i));
                }
            } else {
                invItems.addAll(sp.getInventory().getNonEquipmentItems());
            }
            var result = FFSUtil.buildItemOrFilter(invItems);
            sp.getInventory().placeItemBackInInventory(result.filterStack());
            sp.sendSystemMessage(Component.translatable("ftbfiltersystem.message.gave_or_filter", result.itemCount()));
        }
    }
}
