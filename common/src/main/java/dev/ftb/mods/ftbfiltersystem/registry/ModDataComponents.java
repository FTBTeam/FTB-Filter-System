package dev.ftb.mods.ftbfiltersystem.registry;

import com.mojang.serialization.Codec;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import dev.ftb.mods.ftbfiltersystem.api.FTBFilterSystemAPI;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;

public class ModDataComponents {
    public static final DeferredRegister<DataComponentType<?>> COMPONENT_TYPES
            = DeferredRegister.create(FTBFilterSystemAPI.MOD_ID, Registries.DATA_COMPONENT_TYPE);

    public static final RegistrySupplier<DataComponentType<String>> FILTER_STRING
            = COMPONENT_TYPES.register(FTBFilterSystemAPI.rl("filter"), () -> DataComponentType.<String>builder()
            .persistent(Codec.STRING)
            .networkSynchronized(ByteBufCodecs.STRING_UTF8)
            .build()
    );
}
