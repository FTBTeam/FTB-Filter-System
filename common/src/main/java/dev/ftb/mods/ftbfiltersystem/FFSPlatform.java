package dev.ftb.mods.ftbfiltersystem;

import net.minecraft.core.component.DataComponentType;

import java.util.ServiceLoader;

public interface FFSPlatform {
    FFSPlatform INSTANCE = ServiceLoader.load(FFSPlatform.class).findFirst().orElseThrow();

    static FFSPlatform get() {
        return INSTANCE;
    }

    DataComponentType<String> filterComponent();
}
