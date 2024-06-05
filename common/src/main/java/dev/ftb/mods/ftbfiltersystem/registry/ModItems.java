package dev.ftb.mods.ftbfiltersystem.registry;

import dev.architectury.registry.CreativeTabRegistry;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrarManager;
import dev.architectury.registry.registries.RegistrySupplier;
import dev.ftb.mods.ftbfiltersystem.api.FTBFilterSystemAPI;
import dev.ftb.mods.ftbfiltersystem.registry.item.SmartFilterItem;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(FTBFilterSystemAPI.MOD_ID, Registries.ITEM);
    public static final RegistrySupplier<Item> SMART_FILTER = ITEMS.register("smart_filter", SmartFilterItem::new);

    public static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(FTBFilterSystemAPI.MOD_ID, Registries.CREATIVE_MODE_TAB);
    public static final RegistrySupplier<CreativeModeTab> CREATIVE_TAB = RegistrarManager.get(FTBFilterSystemAPI.MOD_ID)
            .get(Registries.CREATIVE_MODE_TAB)
            .register(new ResourceLocation(FTBFilterSystemAPI.MOD_ID, "default"), ModItems::buildDefaultTab);

    public static Item.Properties defaultProps() {
        return new Item.Properties();
    }

    private static CreativeModeTab buildDefaultTab() {
        return CreativeTabRegistry.create(builder -> builder.title(Component.translatable(FTBFilterSystemAPI.MOD_ID))
                .icon(() -> new ItemStack(ModItems.SMART_FILTER.get()))
                .displayItems((params, output) -> output.accept(new ItemStack(ModItems.SMART_FILTER.get())))
        );
    }
}
