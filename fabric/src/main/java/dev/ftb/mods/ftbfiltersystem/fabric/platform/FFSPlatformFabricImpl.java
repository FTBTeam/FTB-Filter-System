package dev.ftb.mods.ftbfiltersystem.fabric.platform;

import dev.ftb.mods.ftbfiltersystem.FFSPlatform;
import dev.ftb.mods.ftbfiltersystem.fabric.FabricDataComponents;
import net.minecraft.core.component.DataComponentType;

public class FFSPlatformFabricImpl implements FFSPlatform {
    @Override
    public DataComponentType<String> filterComponent() {
        return FabricDataComponents.FILTER;
    }
}
