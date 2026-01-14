package dev.ftb.mods.ftbfiltersystem.api.client.gui.widget;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.input.InputWithModifiers;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;

/**
 * Like the vanilla checkbox, but renders the text without a shadow
 */
public class CustomCheckbox /*extends AbstractButton*/ {
//    private boolean selected;
//    private final boolean showLabel;
//
//    public CustomCheckbox(int pX, int pY, int pWidth, int pHeight, Component pMessage, boolean pSelected) {
//        this(pX, pY, pWidth, pHeight, pMessage, pSelected, true);
//    }
//
//    public CustomCheckbox(int pX, int pY, int pWidth, int pHeight, Component pMessage, boolean pSelected, boolean pShowLabel) {
//        super(pX, pY, pWidth, pHeight, pMessage);
//        this.selected = pSelected;
//        this.showLabel = pShowLabel;
//    }
//
//    public void onPress() {
//        selected = !selected;
//    }
//
//    public boolean selected() {
//        return selected;
//    }
//
//    @Override
//    public void updateWidgetNarration(NarrationElementOutput pNarrationElementOutput) {
//        pNarrationElementOutput.add(NarratedElementType.TITLE, createNarrationMessage());
//        if (this.active) {
//            if (this.isFocused()) {
//                pNarrationElementOutput.add(NarratedElementType.USAGE, Component.translatable("narration.checkbox.usage.focused"));
//            } else {
//                pNarrationElementOutput.add(NarratedElementType.USAGE, Component.translatable("narration.checkbox.usage.hovered"));
//            }
//        }
//    }
//
//    @Override
//    public void onPress(InputWithModifiers inputWithModifiers) {
//        selected = !selected;
//    }
//
//    @Override
//    public void renderContents(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
//        Minecraft minecraft = Minecraft.getInstance();
//        RenderSystem.enableDepthTest();
//        Font font = minecraft.font;
//        pGuiGraphics.setColor(1.0F, 1.0F, 1.0F, this.alpha);
//        RenderSystem.enableBlend();
//        int boxSize = 2 * (font.lineHeight / 2) + 4;
//        int yBase = getY() + (height - boxSize) / 2;
//        if (isFocused()) {
//            pGuiGraphics.fill(getX() - 1, yBase - 1, getX() + boxSize + 1, yBase + boxSize + 1, 0xFFFFFFFF);
//        }
//        pGuiGraphics.fill(getX(), yBase, getX() + boxSize, yBase + boxSize, 0xFF808080);
//        pGuiGraphics.fill(getX() + 1, yBase + 1, getX() + boxSize - 1, yBase + boxSize - 1, 0xFF202020);
//
//        if (selected) {
//            pGuiGraphics.drawString(font, Component.literal("✓").withStyle(ChatFormatting.BOLD), getX() + 2, yBase + 1, 0xFF00B000);
//        }
//        pGuiGraphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
//        if (showLabel) {
//            pGuiGraphics.drawString(font, this.getMessage(), this.getX() + boxSize + 2, this.getY() + (this.height - 8) / 2, 0x404040 | Mth.ceil(this.alpha * 255.0F) << 24, false);
//        }
//
//    }
}
