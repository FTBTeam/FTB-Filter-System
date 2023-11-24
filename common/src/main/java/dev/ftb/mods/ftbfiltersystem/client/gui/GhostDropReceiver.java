package dev.ftb.mods.ftbfiltersystem.client.gui;

import net.minecraft.client.renderer.Rect2i;
import net.minecraft.world.item.ItemStack;

public interface GhostDropReceiver {
    Rect2i getGhostDropRegion();

    void receiveGhostDrop(ItemStack stack);
}
