package dev.ftb.mods.ftbfiltersystem.client.gui;

import dev.ftb.mods.ftbfiltersystem.api.client.gui.AbstractFilterScreen;
import dev.ftb.mods.ftbfiltersystem.filter.MaxStackSizeFilter;

public class MaxCountConfigScreen extends AbstractComparisonConfigScreen<MaxStackSizeFilter> {
    public MaxCountConfigScreen(MaxStackSizeFilter filter, AbstractFilterScreen parentScreen) {
        super(filter, parentScreen, MaxStackSizeFilter::new);
    }
}
