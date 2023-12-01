package dev.ftb.mods.ftbfiltersystem.client.gui;

import com.mojang.blaze3d.platform.InputConstants;
import dev.ftb.mods.ftbfiltersystem.api.client.gui.AbstractFilterConfigScreen;
import dev.ftb.mods.ftbfiltersystem.api.client.gui.AbstractFilterScreen;
import dev.ftb.mods.ftbfiltersystem.filter.CustomFilter;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;

public class CustomConfigScreen extends AbstractFilterConfigScreen<CustomFilter> {
    private EditBox idEditBox;
    private EditBox extraEditBox;

    public CustomConfigScreen(CustomFilter filter, AbstractFilterScreen parentScreen) {
        super(filter, parentScreen, 176, 90);
    }

    @Override
    protected void init() {
        super.init();

        idEditBox = new EditBox(font, leftPos + 8, topPos + 42, guiWidth - 16, font.lineHeight + 1, Component.empty());
        idEditBox.setMaxLength(1024);
        idEditBox.setBordered(true);
        idEditBox.setVisible(true);
        idEditBox.setTextColor(0xFFFFFF);
        idEditBox.setValue(filter.getEventId());
        idEditBox.setFilter(s -> s.isEmpty() || StringUtils.isAlphanumeric(s));
        addRenderableWidget(idEditBox);

        extraEditBox = new EditBox(font, leftPos + 8, topPos + 72, guiWidth - 16, font.lineHeight + 1, Component.empty());
        extraEditBox.setMaxLength(1024);
        extraEditBox.setBordered(true);
        extraEditBox.setVisible(true);
        extraEditBox.setTextColor(0xFFFFFF);
        extraEditBox.setValue(filter.getExtraData());
        addRenderableWidget(extraEditBox);

        setInitialFocus(idEditBox);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        super.render(guiGraphics, pMouseX, pMouseY, pPartialTick);

        guiGraphics.drawString(font, Component.translatable("ftbfiltersystem.gui.custom_id"), leftPos + 8, topPos + 30, 0x404040, false);
        guiGraphics.drawString(font, Component.translatable("ftbfiltersystem.gui.custom_data"), leftPos + 8, topPos + 60, 0x404040, false);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifier) {
        if (keyCode == InputConstants.KEY_RETURN && (idEditBox.canConsumeInput() || extraEditBox.canConsumeInput())) {
            applyChanges();
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifier);
    }

    @Override
    protected @Nullable CustomFilter makeNewFilter() {
        return new CustomFilter(filter.getParent(), idEditBox.getValue(), extraEditBox.getValue());
    }
}
