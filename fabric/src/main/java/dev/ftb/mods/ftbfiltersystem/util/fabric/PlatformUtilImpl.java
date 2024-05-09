package dev.ftb.mods.ftbfiltersystem.util.fabric;

import dev.ftb.mods.ftbfiltersystem.fabric.mixin.PatchedDataComponentMapAccess;
import net.minecraft.core.component.PatchedDataComponentMap;
import net.minecraft.world.item.ItemStack;

@SuppressWarnings("unused")
public class PlatformUtilImpl {
    public static boolean hasComponentPatch(ItemStack stack) {
        if (stack.getComponents() instanceof PatchedDataComponentMap) {
            return !((PatchedDataComponentMapAccess) stack.getComponents()).getPatch().isEmpty();
        }
        return false;
    }
}
