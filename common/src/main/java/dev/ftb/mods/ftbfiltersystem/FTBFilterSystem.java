package dev.ftb.mods.ftbfiltersystem;

import dev.architectury.event.events.common.CommandRegistrationEvent;
import dev.architectury.event.events.common.LifecycleEvent;
import dev.architectury.utils.Env;
import dev.architectury.utils.EnvExecutor;
import dev.ftb.mods.ftbfiltersystem.api.FTBFilterSystemAPI;
import dev.ftb.mods.ftbfiltersystem.api.FTBFilterSystemRegistry;
import dev.ftb.mods.ftbfiltersystem.api.event.FilterRegistrationEvent;
import dev.ftb.mods.ftbfiltersystem.client.FTBFilterSystemClient;
import dev.ftb.mods.ftbfiltersystem.filter.*;
import dev.ftb.mods.ftbfiltersystem.filter.compound.AndFilter;
import dev.ftb.mods.ftbfiltersystem.filter.compound.NotFilter;
import dev.ftb.mods.ftbfiltersystem.filter.compound.OnlyOneFilter;
import dev.ftb.mods.ftbfiltersystem.filter.compound.OrFilter;
import dev.ftb.mods.ftbfiltersystem.network.FTBFilterSystemNet;
import dev.ftb.mods.ftbfiltersystem.registry.FilterRegistry;
import dev.ftb.mods.ftbfiltersystem.registry.ModDataComponents;
import dev.ftb.mods.ftbfiltersystem.registry.ModItems;
import net.minecraft.server.MinecraftServer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class FTBFilterSystem {
    public static FTBFilterSystem instance;

    public static final Logger LOGGER = LogManager.getLogger();

    public FTBFilterSystem() {
        instance = this;

        FTBFilterSystemAPI._init(FilterSystemAPIImpl.INSTANCE);

        LifecycleEvent.SETUP.register(this::onSetup);
        LifecycleEvent.SERVER_STARTED.register(this::serverStarted);

        CommandRegistrationEvent.EVENT.register(FilterSystemCommands::registerCommands);

        FilterRegistrationEvent.REGISTER.register(this::registerBuiltinFilters);
        ModItems.TABS.register();
        ModItems.ITEMS.register();
        ModDataComponents.COMPONENT_TYPES.register();

        EnvExecutor.runInEnv(Env.CLIENT, () -> FTBFilterSystemClient.INSTANCE::init);

        FTBFilterSystemNet.init();
    }

    private void onSetup() {
        FilterRegistrationEvent.REGISTER.invoker().registerFilters(FTBFilterSystemAPI.api().getRegistry());
    }

    private void serverStarted(MinecraftServer minecraftServer) {
        FilterRegistry.INSTANCE.freeze();
    }

    private void registerBuiltinFilters(FTBFilterSystemRegistry reg) {
        reg.register(BlockFilter.ID, BlockFilter::fromString, BlockFilter::new);
        reg.register(CustomFilter.ID, CustomFilter::fromString, CustomFilter::new);
        reg.register(DurabilityFilter.ID, DurabilityFilter::fromString, DurabilityFilter::new);
        reg.register(ExpressionFilter.ID, ExpressionFilter::fromString, ExpressionFilter::new);
        reg.register(FoodValueFilter.ID, FoodValueFilter::fromString, FoodValueFilter::new);
        reg.register(ItemFilter.ID, ItemFilter::fromString, ItemFilter::new);
        reg.register(ItemTagFilter.ID, ItemTagFilter::fromString, ItemTagFilter::new);
        reg.register(MaxStackSizeFilter.ID, MaxStackSizeFilter::fromString, MaxStackSizeFilter::new);
        reg.register(ModFilter.ID, ModFilter::fromString, ModFilter::new);
        reg.register(ComponentFilter.ID, ComponentFilter::fromString, ComponentFilter::new);
        reg.register(StackSizeFilter.ID, StackSizeFilter::fromString, StackSizeFilter::new);

        reg.register(AndFilter.ID, AndFilter::fromString, AndFilter::new);
        reg.register(OrFilter.ID, OrFilter::fromString, OrFilter::new);
        reg.register(NotFilter.ID, NotFilter::fromString, NotFilter::new);
        reg.register(OnlyOneFilter.ID, OnlyOneFilter::fromString, OnlyOneFilter::new);
    }
}
