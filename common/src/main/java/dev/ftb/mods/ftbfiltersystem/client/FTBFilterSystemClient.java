package dev.ftb.mods.ftbfiltersystem.client;

import com.mojang.blaze3d.platform.InputConstants;
import dev.architectury.event.EventResult;
import dev.architectury.event.events.client.ClientLifecycleEvent;
import dev.architectury.event.events.client.ClientScreenInputEvent;
import dev.architectury.networking.NetworkManager;
import dev.architectury.platform.Platform;
import dev.ftb.mods.ftbfiltersystem.api.FTBFilterSystemAPI;
import dev.ftb.mods.ftbfiltersystem.api.FilterException;
import dev.ftb.mods.ftbfiltersystem.api.client.FTBFilterSystemClientAPI;
import dev.ftb.mods.ftbfiltersystem.api.client.FilterScreenFactory;
import dev.ftb.mods.ftbfiltersystem.api.client.gui.AbstractFilterConfigScreen;
import dev.ftb.mods.ftbfiltersystem.api.event.client.ClientFilterRegistrationEvent;
import dev.ftb.mods.ftbfiltersystem.api.filter.SmartFilter;
import dev.ftb.mods.ftbfiltersystem.client.gui.*;
import dev.ftb.mods.ftbfiltersystem.filter.*;
import dev.ftb.mods.ftbfiltersystem.integration.ftbquests.FTBQuestsIntegrationClient;
import dev.ftb.mods.ftbfiltersystem.network.RequestFilterCreationMessage;
import dev.ftb.mods.ftbfiltersystem.registry.FilterRegistry;
import dev.ftb.mods.ftbfiltersystem.registry.item.SmartFilterItem;
import dev.ftb.mods.ftblibrary.util.client.ClientUtils;
import dev.ftb.mods.ftblibrary.util.input.KeyConflict;
import dev.ftb.mods.ftblibrary.util.input.KeyMappingConfig;
import dev.ftb.mods.ftblibrary.util.input.KeyModifier;
import net.minecraft.ChatFormatting;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.client.gui.screens.inventory.EffectRenderingInventoryScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.commands.Commands;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public enum FTBFilterSystemClient implements FTBFilterSystemClientAPI {
    INSTANCE;

    public static final KeyMapping KEY_GET_OR_FILTER_HOTBAR = ClientUtils.input().createKeyMapping(
            KeyMappingConfig.builder("get_or_filter_hotbar", FTBFilterSystemAPI.MOD_ID)
                    .conflictContext(KeyConflict.ANY_GUI)
                    .keyboard(InputConstants.KEY_ADD)
                    .build()
    );
    public static final KeyMapping KEY_GET_OR_FILTER_INV = ClientUtils.input().createKeyMapping(
            KeyMappingConfig.builder("get_or_filter_inv", FTBFilterSystemAPI.MOD_ID)
                    .conflictContext(KeyConflict.ANY_GUI)
                    .keyboard(InputConstants.KEY_ADD)
                    .modifier(KeyModifier.SHIFT)
                    .build()
    );

    private final Map<ResourceLocation, FilterScreenFactory<?>> screenFactories = new ConcurrentHashMap<>();

    public static HolderLookup.Provider registryAccess() {
        return Objects.requireNonNull(Minecraft.getInstance().level).registryAccess();
    }

    public static boolean isShiftHeld() {
        return Screen.hasShiftDown();
    }

    public void init() {
        FTBFilterSystemAPI._initClient(this);

        ClientLifecycleEvent.CLIENT_SETUP.register(this::onClientSetup);
        ClientLifecycleEvent.CLIENT_STARTED.register(this::onClientStarted);

        ClientFilterRegistrationEvent.REGISTER.register(this::registerClientFilters);

        ClientScreenInputEvent.KEY_PRESSED_PRE.register(this::onKeyPressed);

        ClientUtils.input().registerKeyMapping(FTBFilterSystemAPI.MOD_ID,
                KEY_GET_OR_FILTER_HOTBAR,
                KEY_GET_OR_FILTER_INV
        );
    }

    private void registerClientFilters(FTBFilterSystemClientAPI api) {
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

    private void onClientStarted(Minecraft minecraft) {
        ClientFilterRegistrationEvent.REGISTER.invoker().registerFilters(FTBFilterSystemAPI.clientApi());
    }

    public void onClientSetup(Minecraft minecraft) {
        FilterRegistry.getInstance().freeze();
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
                player.displayClientMessage(Component.literal(e.getMessage()).withStyle(ChatFormatting.RED), false);
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
        return Minecraft.getInstance().screen instanceof EffectRenderingInventoryScreen<?>;
    }

    public EventResult onKeyPressed(Minecraft minecraft, Screen screen, int keyCode, int scanCode, int modifiers) {
        if (minecraft.player != null && minecraft.player.hasPermissions(Commands.LEVEL_GAMEMASTERS)) {
            getRelevantItemsForOrFilter().ifPresent(items -> {
                if (ClientUtils.input().matches(KEY_GET_OR_FILTER_HOTBAR, keyCode, scanCode)) {
                    NetworkManager.sendToServer(new RequestFilterCreationMessage(true, items));
                } else if (ClientUtils.input().matches(KEY_GET_OR_FILTER_INV, keyCode, scanCode)) {
                    NetworkManager.sendToServer(new RequestFilterCreationMessage(false, items));
                }
            });
        }

        return EventResult.pass();
    }

    private Optional<List<ItemStack>> getRelevantItemsForOrFilter() {
        if (isInventoryScreen(Minecraft.getInstance().screen)) {
            return Optional.of(List.of());  // can be determined server-side, no need to send items across
        } else if (Platform.isModLoaded("ftbquests")) {
            return Optional.ofNullable(FTBQuestsIntegrationClient.getItemsFromViewQuestPanel());
        }
        return Optional.empty();
    }

    private static boolean isInventoryScreen(@Nullable Screen screen) {
        return screen instanceof InventoryScreen || screen instanceof CreativeModeInventoryScreen;
    }

    //------------------------------------------------------------
    // API implementation below here

    @Override
    public <T extends SmartFilter> void registerFilterScreenFactory(ResourceLocation filterId, FilterScreenFactory<T> factory) {
        screenFactories.put(filterId, factory);
    }
}
