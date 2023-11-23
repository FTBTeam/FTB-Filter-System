package dev.ftb.mods.ftbfiltersystem.registry.menu;

import dev.ftb.mods.ftbfiltersystem.registry.ModMenuTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;

public class SmartFilterMenu extends AbstractContainerMenu {
    public SmartFilterMenu(int id, Inventory inventory, FriendlyByteBuf buf) {
        super(ModMenuTypes.SMART_FILTER.get(), id);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int slot) {
        return null;
    }

    @Override
    public boolean stillValid(Player player) {
        return false;
    }
}
