package dev.ftb.mods.ftbfiltersystem.client.gui.widget;

import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import org.jetbrains.annotations.NotNull;

public class ItemWidget extends AbstractWidget {
    private ItemStack stack;
    private long lastClick;

    public ItemWidget(int pWidth, int pHeight, @NotNull ItemStack stack) {
        this(0, 0, pWidth, pHeight, stack);
    }

    public ItemWidget(int pX, int pY, int pWidth, int pHeight, @NotNull ItemStack stack) {
        super(pX, pY, pWidth, pHeight, Component.empty());
        setStack(stack);
    }

    public ItemStack getStack() {
        return stack;
    }

    public void setStack(ItemStack stack) {
        this.stack = stack;
        updateTooltip();
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        guiGraphics.fill(getX(), getY(), getX() + getWidth(), getY() + getWidth(), 0xFF808080);
        guiGraphics.fill(getX() + 1, getY() + 1, getX() + getWidth() - 1, getY() + getWidth() - 1, 0xFFA0A0A0);
        guiGraphics.renderItem(this.stack, this.getX() + 1, this.getY() + 1);
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {
    }

    @Override
    public void onClick(double mouseX, double mouseY) {
        if (!getStack().isEmpty()) {
            long now = Util.getMillis();
            if (now - lastClick < 250L) {
                handleClick(true);
            } else {
                handleClick(false);
                lastClick = now;
            }
        }
    }

    protected void handleClick(boolean doubleClick) {
    }

    private void updateTooltip() {
        if (stack.isEmpty()) {
            setTooltip(null);
        } else {
            TooltipFlag.Default flag = Minecraft.getInstance().options.advancedItemTooltips ? TooltipFlag.ADVANCED : TooltipFlag.NORMAL;
            stack.getTooltipLines(Item.TooltipContext.of(Minecraft.getInstance().level), Minecraft.getInstance().player, flag).stream()
                    .reduce((c1, c2) -> c1.copy().append("\n").append(c2))
                    .ifPresent(c -> setTooltip(Tooltip.create(c)));
        }
    }
}
