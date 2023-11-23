package dev.ftb.mods.ftbfiltersystem.client.gui;

import dev.ftb.mods.ftbfiltersystem.api.FilterException;
import dev.ftb.mods.ftbfiltersystem.api.client.gui.AbstractFilterScreen;
import dev.ftb.mods.ftbfiltersystem.filter.FuzzyNbtFilter;
import org.jetbrains.annotations.Nullable;

public class FuzzyNBTConfigScreen extends AbstractNBTConfigScreen<FuzzyNbtFilter> {
    public FuzzyNBTConfigScreen(FuzzyNbtFilter filter, AbstractFilterScreen parentScreen) {
        super(filter, parentScreen);
    }

    @Override
    protected @Nullable FuzzyNbtFilter makeNewFilter() {
        try {
            return FuzzyNbtFilter.fromString(filter.getParent(), editBox.getValue());
        } catch (FilterException e) {
            return null;
        }
    }
}
