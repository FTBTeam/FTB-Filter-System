package dev.ftb.mods.ftbfiltersystem.client.gui.widget;

import dev.ftb.mods.ftbfiltersystem.client.GuiUtil;
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
        replaceEntries(buildChildrenList());
        setScrollAmount(0.0);
    }

    public void selectAndCenter(T entry) {
        setSelected(entry);
        centerScrollOn(entry);
    }

    @Override
    public int getRowWidth() {
        return width - 4;
    }

    @Override
    protected int scrollBarX() {
        return getX() + width - 6;
    }

    @Override
    public void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {
    }

    @Override
    protected void renderListItems(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        int w = maxScrollAmount() > 0 ? width - 6 : width;
        GuiUtil.drawPanel(guiGraphics, new Rect2i(getX(), getY() - 1, w, height + 4),
                0xFFA0A0A0, 0xFFA0A0A0, GuiUtil.BorderStyle.INSET, 1);

        super.renderListItems(guiGraphics, mouseX, mouseY, partialTick);
    }

    @Override
    protected void renderSelection(GuiGraphics guiGraphics, T entry, int i) {
        int minX = getX() + (this.width - entry.getWidth()) / 2;
        int maxX = getX() + (this.width + entry.getWidth()) / 2 + (maxScrollAmount() > 0 ? -3 : 3);
        int col = isFocused() ? 0xFFE1F1FD : 0xFFC8D9ED;
        GuiUtil.drawPanel(guiGraphics, new Rect2i(minX - 1, entry.getY(), maxX - minX - 1, entry.getHeight()), col, 0xFF4663AC, GuiUtil.BorderStyle.PLAIN, 1);
    }
}
