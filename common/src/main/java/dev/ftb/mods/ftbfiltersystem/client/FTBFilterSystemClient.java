package dev.ftb.mods.ftbfiltersystem.client;

import dev.ftb.mods.ftbfiltersystem.api.FTBFilterSystemAPI;
import dev.ftb.mods.ftbfiltersystem.api.FilterException;
import dev.ftb.mods.ftbfiltersystem.api.client.FTBFilterSystemClientAPI;
import dev.ftb.mods.ftbfiltersystem.api.client.FilterScreenFactory;
import dev.ftb.mods.ftbfiltersystem.api.client.gui.AbstractFilterConfigScreen;
import dev.ftb.mods.ftbfiltersystem.api.event.FilterRegistrationEvent;
import dev.ftb.mods.ftbfiltersystem.api.event.client.ClientFilterRegistrationEvent;
import dev.ftb.mods.ftbfiltersystem.api.filter.SmartFilter;
import dev.ftb.mods.ftbfiltersystem.client.gui.*;
import dev.ftb.mods.ftbfiltersystem.filter.*;
import dev.ftb.mods.ftbfiltersystem.registry.item.SmartFilterItem;
import dev.ftb.mods.ftblibrary.platform.event.NativeEventPosting;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.Nullable;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class FTBFilterSystemClient implements FTBFilterSystemClientAPI {
    private final Map<Identifier, FilterScreenFactory<?>> screenFactories = new ConcurrentHashMap<>();
    @Nullable
    private static FTBFilterSystemClient instance = null;

    public static HolderLookup.Provider registryAccess() {
        return Objects.requireNonNull(Minecraft.getInstance().level).registryAccess();
    }

    public FTBFilterSystemClient() {
        instance = this;

        FTBFilterSystemAPI._initClient(this);
    }

    public static FTBFilterSystemClient getInstance() {
        return Objects.requireNonNull(instance);
    }

    public void registerClientFilters(FTBFilterSystemClientAPI api) {
        api.registerFilterScreenFactory(ItemFilter.ID, ItemConfigScreen::new);
        api.registerFilterScreenFactory(DurabilityFilter.ID, DurabilityConfigScreen::new);
        api.registerFilterScreenFactory(MaxStackSizeFilter.ID, MaxCountConfigScreen::new);
        api.registerFilterScreenFactory(StackSizeFilter.ID, StackSizeConfigScreen::new);
        api.registerFilterScreenFactory(FoodValueFilter.ID, FoodValueConfigScreen::new);
        api.registerFilterScreenFactory(ItemTagFilter.ID, ItemTagConfigScreen::new);
        api.registerFilterScreenFactory(ModFilter.ID, ModConfigScreen::new);
        api.registerFilterScreenFactory(ComponentFilter.ID, ComponentConfigScreen::new);
        api.registerFilterScreenFactory(CustomFilter.ID, CustomConfigScreen::new);
        api.registerFilterScreenFactory(ExpressionFilter.ID, ExpressionConfigScreen::new);
    }

    public void onClientStarted(Minecraft ignored) {
        NativeEventPosting.get().postEvent(new FilterRegistrationEvent.Data(FTBFilterSystemAPI.api().getRegistry()));
        NativeEventPosting.get().postEvent(new ClientFilterRegistrationEvent.Data(FTBFilterSystemAPI.clientApi()));
    }

    public void openFilterScreen(InteractionHand interactionHand) {
        Player player = Minecraft.getInstance().player;
        if (player == null) {
            return;
        }
        ItemStack stack = player.getItemInHand(interactionHand);

        if (stack.getItem() instanceof SmartFilterItem) {
            try {
                Minecraft.getInstance().setScreen(new FilterScreen(stack.getHoverName(), SmartFilterItem.getFilter(stack, player.registryAccess()), interactionHand));
            } catch (FilterException e) {
                player.sendSystemMessage(Component.literal(e.getMessage()).withStyle(ChatFormatting.RED));
            }
        }
    }

    public <T extends SmartFilter> void openFilterConfigScreen(T filter, FilterScreen parent, boolean deleteOnCancel) {
        FilterScreenFactory<?> factory = screenFactories.get(filter.getId());
        if (factory != null) {
            //noinspection unchecked
            AbstractFilterConfigScreen<T> screen = ((FilterScreenFactory<T>) factory).createScreen(filter, parent);
            screen.setDeleteOnCancel(deleteOnCancel);
            Minecraft.getInstance().setScreen(screen);
        }
    }

    public static boolean isPlayerHolding(ItemStack stack) {
        Player player = Minecraft.getInstance().player;
        return player != null && (player.getMainHandItem() == stack || player.getOffhandItem() == stack);
    }

    public static boolean shouldShowItemTooltip() {
        return Minecraft.getInstance().screen instanceof InventoryScreen;
    }

    //------------------------------------------------------------
    // API implementation below here

    @Override
    public <T extends SmartFilter> void registerFilterScreenFactory(Identifier filterId, FilterScreenFactory<T> factory) {
        screenFactories.put(filterId, factory);
    }
}
