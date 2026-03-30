package dev.ftb.mods.ftbfiltersystem.registry;

import com.mojang.serialization.Codec;
import dev.ftb.mods.ftbfiltersystem.api.FTBFilterSystemAPI;
import dev.ftb.mods.ftblibrary.platform.registry.XRegistry;
import dev.ftb.mods.ftblibrary.platform.registry.XRegistryRef;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;

// TODO XRegistry doesn't work with data components or other parameterised registry types atm
//      using platform-native registration for now
public class ModDataComponents {
//    public static final XRegistry<DataComponentType<?>> COMPONENT_TYPES
//            = XRegistry.create(FTBFilterSystemAPI.MOD_ID, Registries.DATA_COMPONENT_TYPE);
//
//    public static final XRegistryRef<DataComponentType<String>> FILTER_STRING
//            = COMPONENT_TYPES.register("filter", () -> DataComponentType.<String>builder()
//            .persistent(Codec.STRING)
//            .networkSynchronized(ByteBufCodecs.STRING_UTF8)
//            .build()
//    );
}
