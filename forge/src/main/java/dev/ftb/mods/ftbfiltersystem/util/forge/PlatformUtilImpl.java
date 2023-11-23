package dev.ftb.mods.ftbfiltersystem.util.forge;

import net.minecraft.world.item.ItemStack;

public class PlatformUtilImpl {
    public static int getFoodValue(ItemStack stack) {
        return stack.getItem().isEdible() ? stack.getItem().getFoodProperties(stack, null).getNutrition() : 0;
    }
}
