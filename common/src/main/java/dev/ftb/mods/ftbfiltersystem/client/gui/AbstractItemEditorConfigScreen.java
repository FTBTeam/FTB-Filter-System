package dev.ftb.mods.ftbfiltersystem.client.gui;

import dev.ftb.mods.ftbfiltersystem.api.client.gui.AbstractFilterConfigScreen;
import dev.ftb.mods.ftbfiltersystem.api.client.gui.AbstractFilterScreen;
import dev.ftb.mods.ftbfiltersystem.api.filter.SmartFilter;
import dev.ftb.mods.ftbfiltersystem.client.gui.widget.CustomStringWidget;
import dev.ftb.mods.ftbfiltersystem.client.gui.widget.ItemWidget;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.MultiLineEditBox;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public abstract class AbstractItemEditorConfigScreen<T extends SmartFilter> extends AbstractFilterConfigScreen<T> {
    public AbstractItemEditorConfigScreen(T filter, AbstractFilterScreen parentScreen, int guiWidth, int guiHeight) {
        super(filter, parentScreen, guiWidth, guiHeight);
    }

    private static final int SEARCH_ROWS = 4;
    private static final int SEARCH_COLS = 9;

    protected MultiLineEditBox editBox;
    private CustomStringWidget statusLine;
    private final List<SearchItemWidget> itemWidgets = new ArrayList<>();
    protected Component customHoverName = null;

    @Override
    protected void init() {
        super.init();

        editBox = addRenderableWidget(new MultiLineEditBox(font, leftPos + 8, topPos + 20, 304, 75,
                Component.empty(), Component.empty()) {
            @Override
            public boolean mouseClicked(double mouseX, double mouseY, int button) {
                // this is a dirty hack, but default vanilla mouse-clicked behaviour is really stupid
                this.visible = false;
                boolean res =  super.mouseClicked(mouseX, mouseY, button);
                this.visible = true;
                return res;
            }
        });
        setFocused(editBox);
        editBox.setValueListener(s -> scheduleUpdate(5));

        statusLine = addRenderableWidget(new CustomStringWidget(leftPos + 8, topPos + 98, guiWidth - 16, font.lineHeight, Component.empty(), font));
        statusLine.alignRight();

        itemWidgets.clear();
        for (int row = 0; row < SEARCH_ROWS; ++row) {
            for (int col = 0; col < SEARCH_COLS; ++col) {
                SearchItemWidget w = new SearchItemWidget(row, col);
                itemWidgets.add(w);
                addRenderableWidget(w);
            }
        }

        Inventory inv = Minecraft.getInstance().player.getInventory();
        for (int i = 0; i < 36; i++) {
            int idx = i < 9 ? i + 27 : i - 9;
            itemWidgets.get(idx).setStack(inv.items.get(i));
        }
    }

    @Override
    public void render(GuiGraphics guiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        super.render(guiGraphics, pMouseX, pMouseY, pPartialTick);

        guiGraphics.renderOutline(leftPos + 7, topPos + 109, 164, 74, 0xFFA0A0A0);
    }

    protected abstract Predicate<ItemStack> inventoryChecker();

    protected void onItemWidgetClicked() {
    }

    protected void setStatus(boolean ok, Component message, String detail) {
        statusLine.setMessage(message.copy().withStyle(ok ? ChatFormatting.DARK_GREEN : ChatFormatting.DARK_RED));
        statusLine.setTooltip(detail == null || detail.isEmpty() ? null : Tooltip.create(Component.literal(detail)));
    }

    protected abstract String serialize(ItemStack stack);

    private class SearchItemWidget extends ItemWidget {

        public SearchItemWidget(int row, int col) {
            super(leftPos + 8 + 18 * col, topPos + 110 + 18 * row, 18, 18, ItemStack.EMPTY);
        }

        @Override
        protected void handleClick(boolean doubleClick) {
            if (inventoryChecker().test(getStack()) && minecraft.player.hasPermissions(2)) {
                editBox.setValue(serialize(getStack()));
                customHoverName = getStack().hasCustomHoverName() ? getStack().getHoverName() : null;
                AbstractItemEditorConfigScreen.this.setFocused(editBox);
                AbstractItemEditorConfigScreen.this.onItemWidgetClicked();
            }
        }
        @Override
        protected void renderWidget(GuiGraphics guiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
            super.renderWidget(guiGraphics, pMouseX, pMouseY, pPartialTick);
            if (!inventoryChecker().test(getStack())) {
                guiGraphics.pose().pushPose();
                guiGraphics.pose().translate(0, 0, 200);
                guiGraphics.fill(getX(), getY(), getX() + getWidth(), getY() + getWidth(), 0xA0FFFFFF);
                guiGraphics.pose().popPose();
            }
        }

    }
}
