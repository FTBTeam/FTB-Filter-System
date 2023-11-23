package dev.ftb.mods.ftbfiltersystem.network;

import dev.architectury.networking.NetworkManager;
import dev.architectury.networking.simple.BaseC2SMessage;
import dev.architectury.networking.simple.MessageType;
import dev.ftb.mods.ftbfiltersystem.FTBFilterSystem;
import dev.ftb.mods.ftbfiltersystem.api.FilterException;
import dev.ftb.mods.ftbfiltersystem.registry.item.SmartFilterItem;
import dev.ftb.mods.ftbfiltersystem.util.FilterParser;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class SyncFilterMessage extends BaseC2SMessage {
    private final String filterStr;
    private final @Nullable String newTitle;
    private final InteractionHand hand;

    public SyncFilterMessage(String filterStr, @Nullable String newTitle, InteractionHand hand) {
        this.filterStr = filterStr;
        this.newTitle = newTitle;
        this.hand = hand;
    }

    public SyncFilterMessage(FriendlyByteBuf buf) {
        filterStr = buf.readUtf();
        newTitle = buf.readNullable(FriendlyByteBuf::readUtf);
        hand = buf.readEnum(InteractionHand.class);
    }

    @Override
    public MessageType getType() {
        return FTBFilterSystemNet.SYNC_FILTER;
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeUtf(filterStr);
        buf.writeNullable(newTitle, FriendlyByteBuf::writeUtf);
        buf.writeEnum(hand);
    }

    @Override
    public void handle(NetworkManager.PacketContext context) {
        ItemStack stack = context.getPlayer().getItemInHand(hand);
        if (stack.getItem() instanceof SmartFilterItem) {
            try {
                SmartFilterItem.setFilter(stack, FilterParser.parse(filterStr).toString());
                if (newTitle != null) {
                    if (newTitle.isEmpty()) {
                        stack.resetHoverName();
                    } else {
                        stack.setHoverName(Component.literal(newTitle));
                    }
                }
            } catch (FilterException e) {
                FTBFilterSystem.LOGGER.error("received filter sync message with bad filter data from client {}: {}",
                        context.getPlayer().getGameProfile().getName(), e.getMessage());
            }
        }
    }
}
