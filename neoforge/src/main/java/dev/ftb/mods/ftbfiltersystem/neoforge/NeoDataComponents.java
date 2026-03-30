package dev.ftb.mods.ftbfiltersystem.neoforge;

import com.mojang.serialization.Codec;
import dev.ftb.mods.ftbfiltersystem.api.FTBFilterSystemAPI;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class NeoDataComponents {
    public static final DeferredRegister.DataComponents COMPONENTS
            = DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, FTBFilterSystemAPI.MOD_ID);

    private static <T> Supplier<DataComponentType<T>> register(String name, Codec<T> codec, StreamCodec<? super RegistryFriendlyByteBuf, T> streamCodec) {
        return COMPONENTS.registerComponentType(name, builder -> builder
                .persistent(codec)
                .networkSynchronized(streamCodec)
        );
    }

    public static final Supplier<DataComponentType<String>> FILTER
            = register("filter", Codec.STRING, ByteBufCodecs.STRING_UTF8);
}
