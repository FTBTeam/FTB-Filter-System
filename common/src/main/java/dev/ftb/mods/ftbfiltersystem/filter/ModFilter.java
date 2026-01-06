package dev.ftb.mods.ftbfiltersystem.filter;

import dev.ftb.mods.ftbfiltersystem.api.FTBFilterSystemAPI;
import dev.ftb.mods.ftbfiltersystem.api.filter.AbstractSmartFilter;
import dev.ftb.mods.ftbfiltersystem.api.filter.SmartFilter;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public class ModFilter extends AbstractSmartFilter {
    public static final ResourceLocation ID = FTBFilterSystemAPI.rl("mod");

    private final String modId;

    public ModFilter(SmartFilter.Compound parent) {
        this(parent, "minecraft");
    }

    public ModFilter(SmartFilter.Compound parent, String modId) {
        super(parent);

        this.modId = modId;
    }

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    @Override
    public boolean test(ItemStack stack) {
        return stack.getItem().arch$registryName().getNamespace().equals(modId);
    }

    @Override
    public String getStringArg(HolderLookup.Provider registryAccess) {
        return modId;
    }

    public static ModFilter fromString(SmartFilter.Compound parent, String str, HolderLookup.Provider ignored2) {
        return new ModFilter(parent, str);
    }
}
