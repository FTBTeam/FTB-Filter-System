package dev.ftb.mods.ftbfiltersystem.filter;

import dev.ftb.mods.ftbfiltersystem.api.FTBFilterSystemAPI;
import dev.ftb.mods.ftbfiltersystem.api.filter.AbstractSmartFilter;
import dev.ftb.mods.ftbfiltersystem.api.filter.SmartFilter;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class BlockFilter extends AbstractSmartFilter {
    public static final Identifier ID = FTBFilterSystemAPI.rl("block");

    public BlockFilter(SmartFilter.Compound parent) {
        super(parent);
    }

    @Override
    public Identifier getId() {
        return ID;
    }

    @Override
    public boolean test(ItemStack stack) {
        return stack.getItem() instanceof BlockItem && stack.getItem() != Items.AIR;
    }

    @Override
    public boolean isConfigurable() {
        return false;
    }

    @Override
    public String getStringArg(HolderLookup.Provider registryAccess) {
        return "";
    }

    public static BlockFilter fromString(SmartFilter.Compound parent, String ignored, HolderLookup.Provider ignored2) {
        return new BlockFilter(parent);
    }
}
