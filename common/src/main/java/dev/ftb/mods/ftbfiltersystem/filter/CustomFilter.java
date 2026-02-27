package dev.ftb.mods.ftbfiltersystem.filter;

import dev.ftb.mods.ftbfiltersystem.api.FTBFilterSystemAPI;
import dev.ftb.mods.ftbfiltersystem.api.event.CustomFilterEvent;
import dev.ftb.mods.ftbfiltersystem.api.filter.AbstractSmartFilter;
import dev.ftb.mods.ftbfiltersystem.api.filter.SmartFilter;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;

public class CustomFilter extends AbstractSmartFilter {
    public static final Identifier ID = FTBFilterSystemAPI.rl("custom");

    private final String eventId;
    private final String extraData;

    public CustomFilter(SmartFilter.Compound parent) {
        this(parent, "", "");
    }

    public CustomFilter(SmartFilter.Compound parent, String eventId, String extraData) {
        super(parent);

        this.eventId = eventId;
        this.extraData = extraData;
    }

    public String getEventId() {
        return eventId;
    }

    public String getExtraData() {
        return extraData;
    }

    @Override
    public Identifier getId() {
        return ID;
    }

    @Override
    public boolean test(ItemStack stack) {
        return CustomFilterEvent.MATCH_ITEM.invoker().matchItem(stack, eventId, extraData).isTrue();
    }

    @Override
    public String getStringArg(HolderLookup.Provider registryAccess) {
        return extraData.isEmpty() ? eventId : eventId + "/" + extraData;
    }

    public static CustomFilter fromString(SmartFilter.Compound parent, String str, HolderLookup.Provider ignored2) {
        String[] parts = str.split("/");

        return new CustomFilter(parent, parts[0], parts.length > 1 ? parts[1] : "");
    }
}
