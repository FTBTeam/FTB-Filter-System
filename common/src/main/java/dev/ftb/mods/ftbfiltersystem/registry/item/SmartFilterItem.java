package dev.ftb.mods.ftbfiltersystem.registry.item;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.ftb.mods.ftbfiltersystem.FilterSystemCommands;
import dev.ftb.mods.ftbfiltersystem.api.FTBFilterSystemAPI;
import dev.ftb.mods.ftbfiltersystem.api.FilterException;
import dev.ftb.mods.ftbfiltersystem.api.filter.SmartFilter;
import dev.ftb.mods.ftbfiltersystem.client.FTBFilterSystemClient;
import dev.ftb.mods.ftbfiltersystem.registry.ModDataComponents;
import dev.ftb.mods.ftbfiltersystem.registry.ModItems;
import dev.ftb.mods.ftbfiltersystem.util.FilterParser;
import net.minecraft.ChatFormatting;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class SmartFilterItem extends Item {
    public SmartFilterItem() {
        super(ModItems.defaultProps().setId(ResourceKey.create(Registries.ITEM, FTBFilterSystemAPI.rl("smart_filter")))
                .component(ModDataComponents.FILTER_STRING.get(), "")
        );
    }

    public static String getFilterString(ItemStack filterStack) {
        return filterStack.getOrDefault(ModDataComponents.FILTER_STRING.get(), "");
    }

    @NotNull
    public static SmartFilter getFilter(ItemStack filterStack, HolderLookup.Provider registryAccess) throws FilterException {
        // don't pull from cache here; we could be editing this filter,
        //   and don't want to edit something in the cache already
        return FilterParser.parseRaw(getFilterString(filterStack), registryAccess);
    }

    public static void setFilter(ItemStack filterStack, String string) {
        filterStack.set(ModDataComponents.FILTER_STRING.get(), string);
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand interactionHand) {
        ItemStack stack = player.getItemInHand(interactionHand);
        if (level.isClientSide() && !player.isCrouching()) {
            FTBFilterSystemClient.INSTANCE.openFilterScreen(interactionHand);
        } else if (player instanceof ServerPlayer sp && player.isCrouching()) {
            try {
                FilterSystemCommands.tryMatch(sp.createCommandSourceStack());
            } catch (CommandSyntaxException | FilterException e) {
                player.displayClientMessage(Component.literal(e.getMessage()).withStyle(ChatFormatting.RED), false);
            }
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public void appendHoverText(ItemStack itemStack, TooltipContext tooltipContext, TooltipDisplay tooltipDisplay, Consumer<Component> consumer, TooltipFlag tooltipFlag) {
        if (tooltipContext.registries() == null || !FTBFilterSystemClient.shouldShowItemTooltip()) {
            return;  // avoids spurious tooltips in places like FTB Quests where a filter could be a matching display item
        }
        consumer.accept(Component.translatable("item.ftbfiltersystem.smart_filter.tooltip.1").withStyle(ChatFormatting.GRAY));
        consumer.accept(Component.translatable("item.ftbfiltersystem.smart_filter.tooltip.2").withStyle(ChatFormatting.GRAY));
    }
}
