package dev.ftb.mods.ftbfiltersystem.api.client.gui;

import dev.ftb.mods.ftbfiltersystem.api.client.FilterScreenFactory;
import dev.ftb.mods.ftbfiltersystem.api.client.Textures;
import dev.ftb.mods.ftbfiltersystem.api.filter.AbstractSmartFilter;
import dev.ftb.mods.ftbfiltersystem.api.filter.SmartFilter;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.ImageWidget;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.layouts.FrameLayout;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Common base class for all GUIs which edit the state of a filter object. You can extend this class and register
 * implementations via {@link dev.ftb.mods.ftbfiltersystem.api.client.FTBFilterSystemClientAPI#registerFilterScreenFactory(ResourceLocation, FilterScreenFactory)}
 *
 * @param <T> the filter implementation type
 */
public abstract class AbstractFilterConfigScreen<T extends SmartFilter> extends Screen {
    protected final T filter;
    protected final AbstractFilterScreen parentScreen;
    protected final int guiWidth, guiHeight;
    protected int leftPos, topPos;
    protected T modifiedFilter;
    private int updateCounter = 0;
    private boolean deleteOnCancel = false;
    private boolean changesApplied = false;

    public AbstractFilterConfigScreen(T filter, AbstractFilterScreen parentScreen, int guiWidth, int guiHeight) {
        super(filter.getDisplayName());

        this.filter = filter;
        this.modifiedFilter = filter;
        this.parentScreen = parentScreen;
        this.guiWidth = guiWidth;
        this.guiHeight = guiHeight + 40;  // space for title and Done/Cancel button panels
    }

    /**
     * Schedule the {@link #doScheduledUpdate()} method to run a given number of ticks from now. Calling this
     * method will reset an existing countdown.
     *
     * @param ticks the number of ticks after which to call {@code doScheduledUpdate()}
     */
    protected final void scheduleUpdate(int ticks) {
        updateCounter = ticks;
    }

    /**
     * This method is automatically a set number of ticks after {@link #scheduleUpdate(int)} is called. Most commonly
     * use to do something after the player has typed into an EditBox widget.
     */
    protected void doScheduledUpdate() {
    }

    /**
     * This method is called when the "Done" button is clicked in the editing GUI; it should create a new filter of
     * this type, using the existing filter's parent, with data constructed from the current widget state of this GUI.
     *
     * @return a new filter object, used to replace the filter object that was passed in to the GUI
     */
    @Nullable
    protected abstract T makeNewFilter();

    @Override
    protected void init() {
        setupGuiDimensions();

        LinearLayout bottomPanel = new LinearLayout(leftPos, topPos + guiHeight - 25, guiWidth, 20, LinearLayout.Orientation.HORIZONTAL);
        bottomPanel.addChild(new FrameLayout(guiWidth / 2, 20))
                .addChild(Button.builder(Component.translatable("gui.done"), b -> applyChanges()).width(70).build());
        bottomPanel.addChild(new FrameLayout(guiWidth / 2, 20))
                .addChild(Button.builder(Component.translatable("gui.cancel"), b -> onClose()).width(70).build());
        bottomPanel.arrangeElements();
        bottomPanel.visitWidgets(this::addRenderableWidget);

        ImageWidget img = addRenderableWidget(new ImageWidget(leftPos + guiWidth - 19, topPos + 3, 16, 16, Textures.INFO_ICON));
        img.setTooltip(Tooltip.create(AbstractSmartFilter.getTooltip(filter.getId())));
    }

    @Override
    public void tick() {
        if (updateCounter > 0 && --updateCounter == 0) {
            doScheduledUpdate();
        }
    }

    @Override
    public void onClose() {
        minecraft.setScreen(parentScreen);
        parentScreen.findAndSelect(filter);
        if (!changesApplied && deleteOnCancel) {
            parentScreen.deleteSelectedFilter(true);
        }
    }

    @Override
    public void render(GuiGraphics guiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        renderBackground(guiGraphics);

        guiGraphics.blitNineSliced(Textures.BACKGROUND, leftPos, topPos, guiWidth, guiHeight, 4, 32, 32, 0, 0);

        guiGraphics.hLine(leftPos + 3, leftPos + guiWidth - 4, topPos + guiHeight - 29, 0x80404040);
        guiGraphics.hLine(leftPos + 3, leftPos + guiWidth - 4, topPos + guiHeight - 28, 0x80FFFFFF);

        guiGraphics.drawString(font, title, leftPos + 8, topPos + 6, 0x404040, false);

        super.render(guiGraphics, pMouseX, pMouseY, pPartialTick);
    }

    protected final EditBox makeSearchEditBox(int x, int y, Supplier<String> prevStrSupplier, Consumer<String> prevStrConsumer) {
        EditBox editBox = new EditBox(font, x, y, 88, font.lineHeight + 1, Component.empty());
        editBox.setMaxLength(15);
        editBox.setBordered(true);
        editBox.setVisible(true);
        editBox.setTextColor(0xFFFFFF);
        editBox.setValue(prevStrSupplier.get());
        editBox.setResponder(s -> {
            if (!s.equals(prevStrSupplier.get())) {
                scheduleUpdate(5);
                prevStrConsumer.accept(s);
            }
        });
        addRenderableWidget(editBox);
        setFocused(editBox);

        return editBox;
    }

    private void setupGuiDimensions() {
        leftPos = (width - guiWidth) / 2;
        topPos = (height - guiHeight) / 2;
    }

    protected final void applyChanges() {
        T newFilter = makeNewFilter();
        if (newFilter != null) {
            parentScreen.replaceFilter(filter, newFilter);
            changesApplied = true;
        }
        onClose();
    }

    public void setDeleteOnCancel(boolean deleteOnCancel) {
        this.deleteOnCancel = deleteOnCancel;
    }
}
