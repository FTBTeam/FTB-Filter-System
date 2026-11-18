package dev.ftb.mods.ftbfiltersystem.client;

import dev.ftb.mods.ftbfiltersystem.api.filter.AbstractSmartFilter;
import dev.ftb.mods.ftbfiltersystem.api.filter.SmartFilter;
import dev.ftb.mods.ftbfiltersystem.client.gui.widget.CustomStringWidget;
import dev.ftb.mods.ftbfiltersystem.registry.FilterRegistry;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.client.gui.layouts.LayoutSettings;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;

public class SelectionPanel {
    private static final int BUTTON_HEIGHT = 16;
    private boolean visible;
    private GridLayout layout;

    private static final Component COMPOUND = Component.translatable("ftbfiltersystem.gui.compound");
    private static final Component BASIC = Component.translatable("ftbfiltersystem.gui.basic");
    private final Font font;
    private Rect2i bounds;
    private final List<Button> compoundButtons = new ArrayList<>();
    private final List<Button> basicButtons = new ArrayList<>();

    public SelectionPanel(Font font, Consumer<ResourceLocation> onClicked, int availableHeight) {
        this.font = font;

        createButtons(font, onClicked);

        int maxPerColumn = Math.max(1, availableHeight / (BUTTON_HEIGHT + 2)) - 1;
        arrangeButtons(maxPerColumn);
    }

    public void resize(int newAvailableHeight) {
        int maxPerColumn = Math.max(1, newAvailableHeight / (BUTTON_HEIGHT + 2)) - 1;
        arrangeButtons(maxPerColumn);
    }

    private void createButtons(Font font, Consumer<ResourceLocation> onClicked) {
        int widestButton = Math.max(font.width(BASIC), font.width(COMPOUND));
        for (SmartFilter filter : FilterRegistry.getInstance().defaultFilterInstances()) {
            widestButton = Math.max(widestButton, font.width(filter.getDisplayName()) + 10);
        }

        for (SmartFilter filter : FilterRegistry.getInstance().defaultFilterInstances().stream()
                .sorted(Comparator.comparing(filter -> filter.getDisplayName().getString()))
                .toList())
        {
            ResourceLocation id = filter.getId();
            Button button = Button.builder(filter.getDisplayName(), b -> onClicked.accept(id))
                    .size(widestButton, BUTTON_HEIGHT)
                    .tooltip(Tooltip.create(AbstractSmartFilter.getTooltip(id)))
                    .build();
            button.visible = false;

            (filter instanceof SmartFilter.Compound ? compoundButtons : basicButtons).add(button);
        }
    }

    private void arrangeButtons(int maxPerColumn) {
        List<List<Button>> buttons = new ArrayList<>();
        buttons.add(compoundButtons);  // all compound buttons in column 0

        int nBasic = basicButtons.size();
        if (nBasic > maxPerColumn) {
            int div = nBasic / ((nBasic / maxPerColumn) + 1) + 1;
            List<Button> l = new ArrayList<>();
            for (int i = 0; i < nBasic; i++) {
                l.add(basicButtons.get(i));
                if (i % div == div - 1 || i == nBasic - 1) {
                    buttons.add(new ArrayList<>(l));
                    l.clear();
                }
            }
        } else {
            buttons.add(basicButtons);
        }

        layout = new GridLayout();
        LayoutSettings padding = LayoutSettings.defaults().padding(1);
        LayoutSettings paddingR = LayoutSettings.defaults().padding(1).paddingRight(10);
        layout.addChild(new CustomStringWidget(COMPOUND, font).alignCenter().setColor(0xFF202080), 0, 0, paddingR);
        layout.addChild(new CustomStringWidget(BASIC, font).alignCenter().setColor(0xFF202080), 0, 1, padding);
        for (int col = 0; col < buttons.size(); col++) {
            for (int row = 0; row < buttons.get(col).size(); row++) {
                layout.addChild(buttons.get(col).get(row), row + 1, col, col == 0 ? paddingR : padding);
            }
        }
        layout.arrangeElements();
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
        layout.visitWidgets(w -> w.visible = visible);
    }

    public void visitWidgets(Consumer<AbstractWidget> consumer) {
        layout.visitWidgets(consumer);
    }

    public void positionAndRender(GuiGraphics guiGraphics, int topEdge, int rightEdge, int mouseX, int mouseY, float partialTick) {
        int xBase = Math.max(5, rightEdge - layout.getWidth());
        bounds = new Rect2i(xBase, topEdge, layout.getWidth(), layout.getHeight());
        layout.setPosition(bounds.getX(), bounds.getY());

        GuiUtil.drawPanel(guiGraphics, GuiUtil.outsetRect(bounds, 3), 0xFFD6D6D6, 0xFF404040, GuiUtil.BorderStyle.PLAIN, 1);
        guiGraphics.vLine(xBase + compoundButtons.getFirst().getWidth() + 5, topEdge, topEdge + layout.getHeight(), 0xFFA0A0A0);
        visitWidgets(w -> w.render(guiGraphics, mouseX, mouseY, partialTick));
    }

    public boolean isMouseOver(double mouseX, double mouseY) {
        return bounds != null && bounds.contains((int) mouseX, (int) mouseY);
    }
}
