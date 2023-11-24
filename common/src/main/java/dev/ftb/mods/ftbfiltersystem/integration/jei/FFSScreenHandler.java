package dev.ftb.mods.ftbfiltersystem.integration.jei;

import dev.ftb.mods.ftbfiltersystem.api.client.gui.AbstractFilterConfigScreen;
import mezz.jei.api.gui.handlers.IGuiProperties;
import mezz.jei.api.gui.handlers.IScreenHandler;
import net.minecraft.client.gui.screens.Screen;
import org.jetbrains.annotations.Nullable;

class FFSScreenHandler<T extends AbstractFilterConfigScreen<?>> implements IScreenHandler<T> {
    @Override
    public @Nullable IGuiProperties apply(T guiScreen) {
        return guiScreen.width == 0 || guiScreen.height == 0 ? null : new GuiProps(guiScreen);
    }

    private record GuiProps(AbstractFilterConfigScreen<?> guiScreen) implements IGuiProperties {
        @Override
        public Class<? extends Screen> getScreenClass() {
            return guiScreen.getClass();
        }

        @Override
        public int getGuiLeft() {
            return guiScreen.getGuiBounds().getX();
        }

        @Override
        public int getGuiTop() {
            return guiScreen.getGuiBounds().getY();
        }

        @Override
        public int getGuiXSize() {
            return guiScreen.getGuiBounds().getWidth();
        }

        @Override
        public int getGuiYSize() {
            return guiScreen.getGuiBounds().getHeight();
        }

        @Override
        public int getScreenWidth() {
            return guiScreen.width;
        }

        @Override
        public int getScreenHeight() {
            return guiScreen.height;
        }
    }
}
