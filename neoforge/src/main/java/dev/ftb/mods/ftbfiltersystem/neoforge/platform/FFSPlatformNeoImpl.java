package dev.ftb.mods.ftbfiltersystem.neoforge.platform;

import dev.ftb.mods.ftbfiltersystem.FFSPlatform;
import dev.ftb.mods.ftbfiltersystem.neoforge.NeoDataComponents;
import net.minecraft.core.component.DataComponentType;

public class FFSPlatformNeoImpl implements FFSPlatform {
    @Override
    public DataComponentType<String> filterComponent() {
        return NeoDataComponents.FILTER.get();
    }
}
