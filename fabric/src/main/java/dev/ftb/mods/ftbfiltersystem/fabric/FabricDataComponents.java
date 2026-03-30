package dev.ftb.mods.ftbfiltersystem.fabric;

import com.mojang.serialization.Codec;
import dev.ftb.mods.ftbfiltersystem.api.FTBFilterSystemAPI;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.resources.Identifier;

public class FabricDataComponents {
    public static void init() {
    }

    public static final DataComponentType<String> FILTER = Registry.register(
            BuiltInRegistries.DATA_COMPONENT_TYPE,
            Identifier.fromNamespaceAndPath(FTBFilterSystemAPI.MOD_ID, "filter"),
            DataComponentType.<String>builder().persistent(Codec.STRING).networkSynchronized(ByteBufCodecs.STRING_UTF8).build()
    );
}
