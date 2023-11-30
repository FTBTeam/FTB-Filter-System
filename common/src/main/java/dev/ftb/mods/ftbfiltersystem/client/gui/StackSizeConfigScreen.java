package dev.ftb.mods.ftbfiltersystem.client.gui;

import dev.ftb.mods.ftbfiltersystem.api.client.gui.AbstractFilterScreen;
import dev.ftb.mods.ftbfiltersystem.filter.StackSizeFilter;

public class StackSizeConfigScreen extends AbstractComparisonConfigScreen<StackSizeFilter> {
    public StackSizeConfigScreen(StackSizeFilter filter, AbstractFilterScreen parentScreen) {
        super(filter, parentScreen, StackSizeFilter::new);
    }
}
