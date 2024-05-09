package dev.ftb.mods.ftbfiltersystem.network;

import dev.architectury.networking.NetworkManager;
import dev.ftb.mods.ftbfiltersystem.FTBFilterSystem;
import dev.ftb.mods.ftbfiltersystem.api.FTBFilterSystemAPI;
import dev.ftb.mods.ftbfiltersystem.api.FilterException;
import dev.ftb.mods.ftbfiltersystem.registry.item.SmartFilterItem;
import dev.ftb.mods.ftbfiltersystem.util.FilterParser;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;

import java.util.Optional;

public record SyncFilterMessage(String filterStr, Optional<String> newTitle, InteractionHand hand) implements CustomPacketPayload {
    public static final Type<SyncFilterMessage> TYPE = new Type<>(FTBFilterSystemAPI.rl("sync_filter"));

    public static StreamCodec<FriendlyByteBuf, SyncFilterMessage> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8, SyncFilterMessage::filterStr,
            ByteBufCodecs.STRING_UTF8.apply(ByteBufCodecs::optional), SyncFilterMessage::newTitle,
            FTBFilterSystemNet.enumCodec(InteractionHand.class), SyncFilterMessage::hand,
            SyncFilterMessage::new
    );

    public static void handle(SyncFilterMessage message, NetworkManager.PacketContext context) {
        ItemStack stack = context.getPlayer().getItemInHand(message.hand);
        if (stack.getItem() instanceof SmartFilterItem) {
            try {
                SmartFilterItem.setFilter(stack, FilterParser.parse(message.filterStr).toString());
                message.newTitle.ifPresent(title -> {
                    if (title.isEmpty()) {
                        stack.remove(DataComponents.CUSTOM_NAME);
                    } else {
                        stack.set(DataComponents.CUSTOM_NAME, Component.literal(title));
                    }
                });
            } catch (FilterException e) {
                FTBFilterSystem.LOGGER.error("received filter sync message with bad filter data from client {}: {}",
                        context.getPlayer().getGameProfile().getName(), e.getMessage());
            }
        }
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
