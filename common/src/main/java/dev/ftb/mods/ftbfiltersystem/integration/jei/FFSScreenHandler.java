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
        public Class<? extends Screen> screenClass() {
            return guiScreen.getClass();
        }

        @Override
        public int guiLeft() {
            return guiScreen.getGuiBounds().getX();
        }

        @Override
        public int guiTop() {
            return guiScreen.getGuiBounds().getY();
        }

        @Override
        public int guiXSize() {
            return guiScreen.getGuiBounds().getWidth();
        }

        @Override
        public int guiYSize() {
            return guiScreen.getGuiBounds().getHeight();
        }

        @Override
        public int screenWidth() {
            return guiScreen.width;
        }

        @Override
        public int screenHeight() {
            return guiScreen.height;
        }
    }
}
