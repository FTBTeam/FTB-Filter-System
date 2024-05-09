package dev.ftb.mods.ftbfiltersystem.api.client.gui;

import dev.ftb.mods.ftbfiltersystem.api.filter.SmartFilter;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.ApiStatus;

/**
 * Base class for the top-level editor GUI, passed as a parameter when constructing a
 * {@link AbstractFilterConfigScreen}. Do not extend this class yourself.
 */
@ApiStatus.NonExtendable
public abstract class AbstractFilterScreen extends Screen {
    protected AbstractFilterScreen(Component component) {
        super(component);
    }

    public abstract void findAndSelect(SmartFilter filter);

    public abstract <T extends SmartFilter> void replaceFilter(T filter, T newFilter);

    public abstract void deleteSelectedFilter(boolean rebuildList);
}
