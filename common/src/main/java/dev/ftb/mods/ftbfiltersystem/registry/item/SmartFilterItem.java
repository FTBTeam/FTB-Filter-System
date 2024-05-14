package dev.ftb.mods.ftbfiltersystem.registry.item;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.ftb.mods.ftbfiltersystem.FilterSystemCommands;
import dev.ftb.mods.ftbfiltersystem.api.FilterException;
import dev.ftb.mods.ftbfiltersystem.api.filter.SmartFilter;
import dev.ftb.mods.ftbfiltersystem.client.FTBFilterSystemClient;
import dev.ftb.mods.ftbfiltersystem.registry.ModItems;
import dev.ftb.mods.ftbfiltersystem.util.FilterParser;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class SmartFilterItem extends Item {
    private static final String FILTER_TAG_NAME = "ftbfiltersystem:filter";

    public SmartFilterItem() {
        super(ModItems.defaultProps());
    }

    public static String getFilterString(ItemStack filterStack) {
        return filterStack.hasTag() ? filterStack.getTag().getString(FILTER_TAG_NAME) : "";
    }

    @NotNull
    public static SmartFilter getFilter(ItemStack filterStack) throws FilterException {
        return FilterParser.parse(getFilterString(filterStack));
    }

    public static void setFilter(ItemStack filterStack, String string) {
        filterStack.getOrCreateTag().putString(FILTER_TAG_NAME, string);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand interactionHand) {
        ItemStack stack = player.getItemInHand(interactionHand);
        if (level.isClientSide && !player.isCrouching()) {
            FTBFilterSystemClient.INSTANCE.openFilterScreen(interactionHand);
        } else if (!level.isClientSide && player.isCrouching()) {
            try {
                FilterSystemCommands.tryMatch(player.createCommandSourceStack());
            } catch (CommandSyntaxException | FilterException e) {
                player.displayClientMessage(Component.literal(e.getMessage()).withStyle(ChatFormatting.RED), false);
            }
        }
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
    }

    @Override
    public void appendHoverText(ItemStack itemStack, @Nullable Level level, List<Component> list, TooltipFlag tooltipFlag) {
        if (level == null || level.isClientSide && !FTBFilterSystemClient.shouldShowItemTooltip()) {
            return;  // avoids spurious tooltips in places like FTB Quests where a filter could be a matching display item
        }
        list.add(Component.translatable("item.ftbfiltersystem.smart_filter.tooltip.1").withStyle(ChatFormatting.GRAY));
        list.add(Component.translatable("item.ftbfiltersystem.smart_filter.tooltip.2").withStyle(ChatFormatting.GRAY));
    }
}
