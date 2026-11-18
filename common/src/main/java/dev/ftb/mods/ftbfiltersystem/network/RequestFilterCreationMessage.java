package dev.ftb.mods.ftbfiltersystem.network;

import dev.architectury.networking.NetworkManager;
import dev.ftb.mods.ftbfiltersystem.api.FTBFilterSystemAPI;
import dev.ftb.mods.ftbfiltersystem.util.FFSUtil;
import net.minecraft.commands.Commands;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public record RequestFilterCreationMessage(boolean hotbarOnly, List<ItemStack> items) implements CustomPacketPayload {
    public static final StreamCodec<RegistryFriendlyByteBuf, RequestFilterCreationMessage> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.BOOL, RequestFilterCreationMessage::hotbarOnly,
            ItemStack.STREAM_CODEC.apply(ByteBufCodecs.list()), RequestFilterCreationMessage::items,
            RequestFilterCreationMessage::new
    );

    static final Type<RequestFilterCreationMessage> TYPE = new Type<>(FTBFilterSystemAPI.rl("request_filter_creation"));

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(RequestFilterCreationMessage message, NetworkManager.PacketContext packetContext) {
        if (packetContext.getPlayer() instanceof ServerPlayer sp && sp.hasPermissions(Commands.LEVEL_GAMEMASTERS)) {
            List<ItemStack> invItems = message.getItems(sp);
            var result = FFSUtil.buildItemOrFilter(invItems);
            sp.getInventory().placeItemBackInInventory(result.filterStack());
            sp.sendSystemMessage(Component.translatable("ftbfiltersystem.message.gave_or_filter", result.itemCount()));
        }
    }

    private List<ItemStack> getItems(Player player) {
        if (!items.isEmpty()) {
            return items;
        }
        if (hotbarOnly) {
            List<ItemStack> res = new ArrayList<>();
            for (int i = 0; i < 9; i++) {
                res.add(player.getInventory().getItem(i));  // empty items are filtered out in FFSUtil.buildItemOrFilter
            }
            return res;
        } else {
            return player.getInventory().items;
        }
    }
}
