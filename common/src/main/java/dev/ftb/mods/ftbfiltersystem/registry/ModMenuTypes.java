package dev.ftb.mods.ftbfiltersystem.registry;

import dev.architectury.registry.menu.MenuRegistry;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import dev.ftb.mods.ftbfiltersystem.api.FTBFilterSystemAPI;
import dev.ftb.mods.ftbfiltersystem.registry.menu.SmartFilterMenu;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.MenuType;

public class ModMenuTypes {
    public static final DeferredRegister<MenuType<?>> MENU_TYPES = DeferredRegister.create(FTBFilterSystemAPI.MOD_ID, Registries.MENU);

    public static final RegistrySupplier<MenuType<SmartFilterMenu>> SMART_FILTER
            = MENU_TYPES.register("smart_filter", () -> MenuRegistry.ofExtended(SmartFilterMenu::new));
}
