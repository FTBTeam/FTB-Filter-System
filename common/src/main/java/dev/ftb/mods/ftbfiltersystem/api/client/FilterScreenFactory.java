package dev.ftb.mods.ftbfiltersystem.api.client;

import dev.ftb.mods.ftbfiltersystem.api.client.gui.AbstractFilterConfigScreen;
import dev.ftb.mods.ftbfiltersystem.api.client.gui.AbstractFilterScreen;
import dev.ftb.mods.ftbfiltersystem.api.filter.SmartFilter;

@FunctionalInterface
public interface FilterScreenFactory<T extends SmartFilter> {
    AbstractFilterConfigScreen<T> createScreen(T filter, AbstractFilterScreen parentScreen);
}
