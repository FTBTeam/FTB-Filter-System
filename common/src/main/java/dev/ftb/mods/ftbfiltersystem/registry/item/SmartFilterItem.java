package dev.ftb.mods.ftbfiltersystem.registry.item;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.ftb.mods.ftbfiltersystem.FilterSystemCommands;
import dev.ftb.mods.ftbfiltersystem.api.FilterException;
import dev.ftb.mods.ftbfiltersystem.api.filter.SmartFilter;
import dev.ftb.mods.ftbfiltersystem.client.FTBFilterSystemClient;
import dev.ftb.mods.ftbfiltersystem.registry.ModDataComponents;
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

import java.util.List;

public class SmartFilterItem extends Item {
    public SmartFilterItem() {
        super(ModItems.defaultProps()
                .component(ModDataComponents.FILTER_STRING.get(), "")
        );
    }

    public static String getFilterString(ItemStack filterStack) {
        return filterStack.getOrDefault(ModDataComponents.FILTER_STRING.get(), "");
    }

    @NotNull
    public static SmartFilter getFilter(ItemStack filterStack) throws FilterException {
        // don't pull from cache here; we could be editing this filter,
        //   and don't want to edit something in the cache already
        return FilterParser.parseRaw(getFilterString(filterStack));
    }

    public static void setFilter(ItemStack filterStack, String string) {
        filterStack.set(ModDataComponents.FILTER_STRING.get(), string);
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
    public void appendHoverText(ItemStack itemStack, TooltipContext context, List<Component> list, TooltipFlag tooltipFlag) {
        if (context.registries() == null || !FTBFilterSystemClient.shouldShowItemTooltip()) {
            return;  // avoids spurious tooltips in places like FTB Quests where a filter could be a matching display item
        }
        list.add(Component.translatable("item.ftbfiltersystem.smart_filter.tooltip.1").withStyle(ChatFormatting.GRAY));
        list.add(Component.translatable("item.ftbfiltersystem.smart_filter.tooltip.2").withStyle(ChatFormatting.GRAY));
    }
}
