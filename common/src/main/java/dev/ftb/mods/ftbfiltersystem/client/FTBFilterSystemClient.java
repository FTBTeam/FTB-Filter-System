package dev.ftb.mods.ftbfiltersystem.client;

import com.mojang.blaze3d.platform.InputConstants;
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
import dev.ftb.mods.ftbfiltersystem.integration.ftbquests.FTBQuestsIntegrationClient;
import dev.ftb.mods.ftbfiltersystem.network.RequestFilterCreationMessage;
import dev.ftb.mods.ftbfiltersystem.registry.item.SmartFilterItem;
import dev.ftb.mods.ftblibrary.client.util.ClientUtils;
import dev.ftb.mods.ftblibrary.platform.Platform;
import dev.ftb.mods.ftblibrary.platform.client.PlatformClient;
import dev.ftb.mods.ftblibrary.platform.client.input.KeyConflict;
import dev.ftb.mods.ftblibrary.platform.client.input.KeyMappingConfig;
import dev.ftb.mods.ftblibrary.platform.client.input.KeyModifier;
import dev.ftb.mods.ftblibrary.platform.event.NativeEventPosting;
import dev.ftb.mods.ftblibrary.platform.network.Play2ServerNetworking;
import net.minecraft.ChatFormatting;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.permissions.Permissions;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class FTBFilterSystemClient implements FTBFilterSystemClientAPI {
    private final Map<Identifier, FilterScreenFactory<?>> screenFactories = new ConcurrentHashMap<>();
    @Nullable
    private static FTBFilterSystemClient instance = null;

    public static final KeyMapping.Category FFS_CATEGORY = new KeyMapping.Category(FTBFilterSystemAPI.rl("gui"));

    public static final KeyMapping KEY_GET_OR_FILTER_HOTBAR = PlatformClient.get().input().createKeyMapping(
            KeyMappingConfig.builder("get_or_filter_hotbar", FFS_CATEGORY)
                    .conflictContext(KeyConflict.ANY_GUI)
                    .keyboard(InputConstants.KEY_ADD)
                    .build()
    );
    public static final KeyMapping KEY_GET_OR_FILTER_INV = PlatformClient.get().input().createKeyMapping(
            KeyMappingConfig.builder("get_or_filter_inv", FFS_CATEGORY)
                    .conflictContext(KeyConflict.ANY_GUI)
                    .keyboard(InputConstants.KEY_ADD)
                    .modifier(KeyModifier.SHIFT)
                    .build()
    );

    public static HolderLookup.Provider registryAccess() {
        return Objects.requireNonNull(Minecraft.getInstance().level).registryAccess();
    }

    public FTBFilterSystemClient() {
        instance = this;

        FTBFilterSystemAPI._initClient(this);

        PlatformClient.get().input().registerKeyMapping(FTBFilterSystemAPI.MOD_ID,
                KEY_GET_OR_FILTER_HOTBAR,
                KEY_GET_OR_FILTER_INV
        );
    }

    public static FTBFilterSystemClient getInstance() {
        return Objects.requireNonNull(instance);
    }

    public static boolean isShiftHeld() {
        return Minecraft.getInstance().hasShiftDown();
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

    public boolean isPlayerHolding(ItemStack stack) {
        Player player = Minecraft.getInstance().player;
        return player != null && (player.getMainHandItem() == stack || player.getOffhandItem() == stack);
    }

    public boolean shouldShowItemTooltip() {
        return isInventoryScreen(Minecraft.getInstance().screen);
    }

    public void onKeyPressed(KeyEvent keyEvent) {
        ClientUtils.getOptionalClientPlayer().ifPresent(player -> {
            if (player.permissions().hasPermission(Permissions.COMMANDS_GAMEMASTER)) {
                getRelevantItemsForOrFilter().ifPresent(items -> {
                    if (PlatformClient.get().input().matches(KEY_GET_OR_FILTER_HOTBAR, keyEvent)) {
                        Play2ServerNetworking.send(new RequestFilterCreationMessage(true, items));
                    } else if (PlatformClient.get().input().matches(KEY_GET_OR_FILTER_INV, keyEvent)) {
                        Play2ServerNetworking.send(new RequestFilterCreationMessage(false, items));
                    }
                });
            }
        });
    }

    private Optional<List<ItemStack>> getRelevantItemsForOrFilter() {
        if (isInventoryScreen(Minecraft.getInstance().screen)) {
            return Optional.of(List.of());  // can be determined server-side, no need to send items across
        } else if (Platform.get().isModLoaded("ftbquests")) {
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
    public <T extends SmartFilter> void registerFilterScreenFactory(Identifier filterId, FilterScreenFactory<T> factory) {
        screenFactories.put(filterId, factory);
    }
}
