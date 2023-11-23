package dev.ftb.mods.ftbfiltersystem.util.fabric;

import net.minecraft.world.item.ItemStack;

public class PlatformUtilImpl {
    public static int getFoodValue(ItemStack stack) {
        return stack.getItem().getFoodProperties() == null ? 0 : stack.getItem().getFoodProperties().getNutrition();
    }
}
