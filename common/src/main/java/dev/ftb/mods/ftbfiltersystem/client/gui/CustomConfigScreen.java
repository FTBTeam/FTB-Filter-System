package dev.ftb.mods.ftbfiltersystem.client.gui;

import dev.ftb.mods.ftbfiltersystem.api.client.gui.AbstractFilterConfigScreen;
import dev.ftb.mods.ftbfiltersystem.api.client.gui.AbstractFilterScreen;
import dev.ftb.mods.ftbfiltersystem.filter.CustomFilter;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;

public class CustomConfigScreen extends AbstractFilterConfigScreen<CustomFilter> {
    private EditBox editBox;

    public CustomConfigScreen(CustomFilter filter, AbstractFilterScreen parentScreen) {
        super(filter, parentScreen, 176, 50);
    }

    @Override
    protected void init() {
        super.init();

        editBox = new EditBox(font, leftPos + 8, topPos + 42, guiWidth - 16, font.lineHeight + 1, Component.empty());
        editBox.setMaxLength(1024);
        editBox.setBordered(true);
        editBox.setVisible(true);
        editBox.setTextColor(0xFFFFFF);
        editBox.setValue(filter.getStringArg());
        addRenderableWidget(editBox);

        setInitialFocus(editBox);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        super.render(guiGraphics, pMouseX, pMouseY, pPartialTick);

        guiGraphics.drawString(font, Component.translatable("ftbfiltersystem.gui.custom_data"), leftPos + 8, topPos + 30, 0x404040, false);
    }

    @Override
    protected @Nullable CustomFilter makeNewFilter() {
        return new CustomFilter(filter.getParent(), editBox.getValue());
    }
}
