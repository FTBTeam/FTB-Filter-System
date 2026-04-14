package dev.ftb.mods.ftbfiltersystem.registry;

import dev.ftb.mods.ftbfiltersystem.api.FTBFilterSystemAPI;
import dev.ftb.mods.ftbfiltersystem.registry.item.SmartFilterItem;
import dev.ftb.mods.ftblibrary.platform.registry.XRegistry;
import dev.ftb.mods.ftblibrary.platform.registry.XRegistryRef;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.Item;

public class ModItems {
    public static final XRegistry<Item> ITEMS = XRegistry.create(FTBFilterSystemAPI.MOD_ID, Registries.ITEM);
    public static final XRegistryRef<Item> SMART_FILTER = ITEMS.register("smart_filter", SmartFilterItem::new);

    public static Item.Properties defaultProps() {
        return new Item.Properties();
    }
}
