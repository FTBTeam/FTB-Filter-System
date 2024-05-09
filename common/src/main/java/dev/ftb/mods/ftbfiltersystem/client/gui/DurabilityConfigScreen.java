package dev.ftb.mods.ftbfiltersystem.client.gui;

import dev.ftb.mods.ftbfiltersystem.api.client.gui.AbstractComparisonConfigScreen;
import dev.ftb.mods.ftbfiltersystem.api.client.gui.AbstractFilterScreen;
import dev.ftb.mods.ftbfiltersystem.filter.DurabilityFilter;

public class DurabilityConfigScreen extends AbstractComparisonConfigScreen<DurabilityFilter> {
    public DurabilityConfigScreen(DurabilityFilter filter, AbstractFilterScreen parentScreen) {
        super(filter, parentScreen, DurabilityFilter::new);
    }
}
