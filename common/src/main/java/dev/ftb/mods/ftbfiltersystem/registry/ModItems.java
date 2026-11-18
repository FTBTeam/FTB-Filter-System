package dev.ftb.mods.ftbfiltersystem.registry;

import dev.architectury.registry.CreativeTabRegistry;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import dev.ftb.mods.ftbfiltersystem.api.FTBFilterSystemAPI;
import dev.ftb.mods.ftbfiltersystem.registry.item.SmartFilterItem;
import dev.ftb.mods.ftblibrary.FTBLibrary;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(FTBFilterSystemAPI.MOD_ID, Registries.ITEM);
    public static final RegistrySupplier<Item> SMART_FILTER = ITEMS.register("smart_filter", SmartFilterItem::new);

    public static void init() {
        CreativeTabRegistry.appendStack(FTBLibrary.getCreativeModeTab(), () -> new ItemStack(SMART_FILTER.get()));
    }

    public static Item.Properties defaultProps() {
        return new Item.Properties();
    }
}
