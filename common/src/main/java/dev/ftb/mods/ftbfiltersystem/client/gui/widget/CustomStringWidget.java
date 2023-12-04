package dev.ftb.mods.ftbfiltersystem.client.gui.widget;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.network.chat.Component;

public class CustomStringWidget extends StringWidget {
    private boolean dropShadow = false;

    public CustomStringWidget(Component component, Font font) {
        super(component, font);
    }

    public CustomStringWidget(int width, int height, Component component, Font font) {
        super(width, height, component, font);
    }

    public CustomStringWidget(int x, int y, int width, int height, Component component, Font font) {
        super(x, y, width, height, component, font);
    }

    public CustomStringWidget setDropShadow(boolean dropShadow) {
        this.dropShadow = dropShadow;
        return this;
    }

    @Override
    public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        Component component = this.getMessage();
        Font font = this.getFont();
        int x = this.getX() + Math.round(this.alignX * (float)(this.getWidth() - font.width(component)));
        int y = this.getY() + (this.getHeight() - 9) / 2;
        guiGraphics.drawString(font, component, x, y, this.getColor(), dropShadow);
    }

    public static CustomStringWidget withText(int x, int y, Component text, Font font) {
        int w = font.width(text);
        return new CustomStringWidget(x, y, w, font.lineHeight + 6, text, font);
    }
}
