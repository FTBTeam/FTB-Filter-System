package dev.ftb.mods.ftbfiltersystem.client.gui.widget;

import dev.ftb.mods.ftbfiltersystem.client.GuiUtil;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.renderer.Rect2i;

import java.util.List;

public abstract class CustomSelectionList<T extends ObjectSelectionList.Entry<T>> extends ObjectSelectionList<T> {
    public CustomSelectionList(Minecraft minecraft, int width, int height, int y, int elementHeight) {
        super(minecraft, width, height, y, elementHeight);

        addChildren();
    }

    protected abstract List<T> buildChildrenList();

    public final void addChildren() {
        children().clear();
        children().addAll(buildChildrenList());
        setScrollAmount(0.0);
    }

    public void selectAndCenter(T entry) {
        setSelected(entry);
        centerScrollOn(entry);
    }

    @Override
    public int getRowWidth() {
        return width - 8;
    }

    @Override
    protected int getScrollbarPosition() {
        return getX() + width - 6;
    }

    @Override
    public void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {
    }

    @Override
    protected void renderList(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        int w = getMaxScroll() > 0 ? width - 6 : width;
        GuiUtil.drawPanel(guiGraphics, new Rect2i(getX(), getY(), w, height + 4),
                0xFFA0A0A0, 0xFFA0A0A0, GuiUtil.BorderStyle.INSET, 1);

        super.renderList(guiGraphics, mouseX, mouseY, partialTick);
    }

    @Override
    protected void renderSelection(GuiGraphics guiGraphics, int pTop, int pWidth, int pHeight, int pOuterColor, int pInnerColor) {
        int minX = getX() + (this.width - pWidth) / 2;
        int maxX = getX() + (this.width + pWidth) / 2 + (getMaxScroll() > 0 ? -3 : 3);
        int col = isFocused() ? 0xFFE1F1FD : 0xFFC8D9ED;
        GuiUtil.drawPanel(guiGraphics, new Rect2i(minX + 1, pTop - 2, maxX - minX - 2, pHeight + 4), col, 0xFF4663AC, GuiUtil.BorderStyle.PLAIN, 1);
    }

    public abstract static class Entry<E extends CustomSelectionList.Entry<E>> extends ObjectSelectionList.Entry<E> {
        private long lastClickTime;

        protected abstract boolean onMouseClick(double x, double y, int button, boolean isDoubleClick);

        @Override
        public boolean mouseClicked(double x, double y, int button) {
            if (Util.getMillis() - lastClickTime < 250L) {
                return onMouseClick(x, y, button, true);
            } else {
                lastClickTime = Util.getMillis();
                return onMouseClick(x, y, button, false);
            }
        }
    }
}
