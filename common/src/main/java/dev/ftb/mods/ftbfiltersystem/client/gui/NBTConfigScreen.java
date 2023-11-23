package dev.ftb.mods.ftbfiltersystem.client.gui;

import dev.ftb.mods.ftbfiltersystem.api.FilterException;
import dev.ftb.mods.ftbfiltersystem.api.client.gui.AbstractFilterScreen;
import dev.ftb.mods.ftbfiltersystem.filter.NbtFilter;
import org.jetbrains.annotations.Nullable;

public class NBTConfigScreen extends AbstractNBTConfigScreen<NbtFilter> {
    public NBTConfigScreen(NbtFilter filter, AbstractFilterScreen parentScreen) {
        super(filter, parentScreen);
    }

    @Override
    protected @Nullable NbtFilter makeNewFilter() {
        try {
            return NbtFilter.fromString(filter.getParent(), editBox.getValue());
        } catch (FilterException e) {
            return null;
        }
    }
}
