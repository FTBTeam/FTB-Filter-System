package dev.ftb.mods.ftbfiltersystem.util;

import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.world.item.ItemStack;

public class PlatformUtil {
    @ExpectPlatform
    public static boolean hasComponentPatch(ItemStack stack) {
        throw new AssertionError();
    }
}
