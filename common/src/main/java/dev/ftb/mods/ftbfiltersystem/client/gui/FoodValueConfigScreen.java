package dev.ftb.mods.ftbfiltersystem.client.gui;

import dev.ftb.mods.ftbfiltersystem.api.client.gui.AbstractComparisonConfigScreen;
import dev.ftb.mods.ftbfiltersystem.api.client.gui.AbstractFilterScreen;
import dev.ftb.mods.ftbfiltersystem.filter.FoodValueFilter;

public class FoodValueConfigScreen extends AbstractComparisonConfigScreen<FoodValueFilter> {
    public FoodValueConfigScreen(FoodValueFilter filter, AbstractFilterScreen parentScreen) {
        super(filter, parentScreen, FoodValueFilter::new);
    }
}
