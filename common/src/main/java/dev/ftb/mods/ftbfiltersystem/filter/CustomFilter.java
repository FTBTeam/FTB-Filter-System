package dev.ftb.mods.ftbfiltersystem.filter;

import dev.ftb.mods.ftbfiltersystem.api.filter.AbstractCompoundFilter;
import dev.ftb.mods.ftbfiltersystem.api.filter.AbstractSmartFilter;
import dev.ftb.mods.ftbfiltersystem.api.FTBFilterSystemAPI;
import dev.ftb.mods.ftbfiltersystem.api.event.CustomFilterEvent;
import dev.ftb.mods.ftbfiltersystem.api.filter.SmartFilter;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public class CustomFilter extends AbstractSmartFilter {
    public static final ResourceLocation ID = FTBFilterSystemAPI.rl("custom");

    private final String data;

    public CustomFilter(SmartFilter.Compound parent) {
        this(parent, "");
    }

    public CustomFilter(SmartFilter.Compound parent, String data) {
        super(parent);

        this.data = data;
    }

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    @Override
    public boolean test(ItemStack stack) {
        return CustomFilterEvent.MATCH_ITEM.invoker().matchItem(stack, data).isTrue();
    }

    @Override
    public String getStringArg() {
        return data;
    }

    public static CustomFilter fromString(SmartFilter.Compound parent, String str) {
        return new CustomFilter(parent, str);
    }
}
