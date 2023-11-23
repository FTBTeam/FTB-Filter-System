package dev.ftb.mods.ftbfiltersystem.client;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.resources.ResourceLocation;

public class GuiUtil {
    public static void drawPanel(GuiGraphics graphics, Rect2i area, int bgColor, int borderColor, BorderStyle borderStyle, int borderWidth) {
        int xMax = area.getX() + area.getWidth();
        int yMax = area.getY() + area.getHeight();

        graphics.fill(area.getX(), area.getY(), xMax, yMax, bgColor);

        graphics.pose().pushPose();
        // yeah I really want integer division here.  float division would look all mixely
        //noinspection IntegerDivisionInFloatingPointContext
        graphics.pose().translate(-borderWidth / 2, -borderWidth / 2, 0);

        int brCol = borderStyle.bottomRight(bgColor, borderColor);
        graphics.fill(area.getX() + 1, yMax, xMax, yMax + borderWidth, brCol); // bottom
        graphics.fill(xMax, area.getY() + 1, xMax + borderWidth, yMax, brCol); // right

        int tlCol = borderStyle.topLeft(bgColor, borderColor);
        graphics.fill(area.getX(), area.getY(), xMax, area.getY() + borderWidth, tlCol); // top
        graphics.fill(area.getX(), area.getY(), area.getX() + borderWidth, yMax, tlCol); // left

        graphics.pose().popPose();
    }

    public static int brighten(int color, float factor) {
        int a = color >> 24 & 0xff;
        int r = color >> 16 & 0xff;
        int g = color >> 8 & 0xff;
        int b = color & 0xff;

        /* From 2D group:
         * 1. black.brighter() should return grey
         * 2. applying brighter to blue will always return blue, brighter
         * 3. non pure color (non zero rgb) will eventually return white
         */
        int i = (int) (1.0 / (1.0 - factor));
        if (r == 0 && g == 0 && b == 0) {
            return makeRGB(a, i, i, i);
        }
        if (r > 0 && r < i) r = i;
        if (g > 0 && g < i) g = i;
        if (b > 0 && b < i) b = i;

        return makeRGB(a,
                Math.min((int) (r / factor), 255),
                Math.min((int) (g / factor), 255),
                Math.min((int) (b / factor), 255)
        );
    }

    public static int darken(int color, float factor) {
        int a = color >> 24 & 0xff;
        int r = color >> 16 & 0xff;
        int g = color >> 8 & 0xff;
        int b = color & 0xff;

        return makeRGB(a,
                Math.max((int) (r * factor), 0),
                Math.max((int) (g * factor), 0),
                Math.max((int) (b * factor), 0)
        );
    }

    private static int makeRGB(int a, int r, int g, int b) {
        return a << 24 | r << 16 | g << 8 | b;
    }

    public static ImageButton make16x16ImageButton(ResourceLocation texture, Button.OnPress onPress) {
        return new ImageButton(0, 0, 16, 16, 0, 0, 0, texture, 16, 16, onPress);
    }

    public static Rect2i outsetRect(Rect2i orig, int amount) {
        return new Rect2i(orig.getX() - amount, orig.getY() - amount, orig.getWidth() + amount * 2, orig.getHeight() + amount * 2);
    }

    public static FormattedText ellipsize(Font font, FormattedText text, int maxWidth) {
        final int width = font.width(text);
        final int eWidth = font.width(CommonComponents.ELLIPSIS);
        if (width > maxWidth) {
            return eWidth >= maxWidth ?
                    font.substrByWidth(text, maxWidth) :
                    FormattedText.composite(font.substrByWidth(text, maxWidth - eWidth), CommonComponents.ELLIPSIS);
        }
        return text;
    }

    public enum BorderStyle {
        NONE,
        PLAIN,
        OUTSET,
        INSET;

        public int topLeft(int bgColor, int borderColor) {
            return switch (this) {
                case NONE -> bgColor;
                case PLAIN -> borderColor;
                case OUTSET -> brighten(borderColor, 0.7f);
                case INSET -> darken(borderColor, 0.7f);
            };
        }

        public int bottomRight(int bgColor, int borderColor) {
            return switch (this) {
                case NONE -> bgColor;
                case PLAIN -> borderColor;
                case OUTSET -> darken(borderColor, 0.7f);
                case INSET -> brighten(borderColor, 0.7f);
            };
        }
    }
}
