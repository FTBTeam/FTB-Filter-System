package dev.ftb.mods.ftbfiltersystem.client.gui;

import com.mojang.blaze3d.platform.InputConstants;
import dev.architectury.networking.NetworkManager;
import dev.ftb.mods.ftbfiltersystem.api.FTBFilterSystemAPI;
import dev.ftb.mods.ftbfiltersystem.api.client.Textures;
import dev.ftb.mods.ftbfiltersystem.api.client.gui.AbstractFilterScreen;
import dev.ftb.mods.ftbfiltersystem.api.filter.AbstractCompoundFilter;
import dev.ftb.mods.ftbfiltersystem.api.filter.DumpedFilter;
import dev.ftb.mods.ftbfiltersystem.api.filter.SmartFilter;
import dev.ftb.mods.ftbfiltersystem.client.FTBFilterSystemClient;
import dev.ftb.mods.ftbfiltersystem.client.GuiUtil;
import dev.ftb.mods.ftbfiltersystem.client.SelectionPanel;
import dev.ftb.mods.ftbfiltersystem.network.SyncFilterMessage;
import dev.ftb.mods.ftbfiltersystem.registry.item.SmartFilterItem;
import dev.ftb.mods.ftbfiltersystem.util.FilterParser;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.*;
import net.minecraft.client.gui.layouts.FrameLayout;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.ConfirmScreen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Util;
import net.minecraft.world.InteractionHand;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class FilterScreen extends AbstractFilterScreen {
    private static SelectionPanel selectionPanel = null;

    private final SmartFilter filter;
    private final String origFilterStr;
    private final InteractionHand interactionHand;

    private int leftPos, topPos;
    private int guiWidth, guiHeight;
    private FilterList filterList;
    private Button addFilterBtn;
    private Button deleteFilterBtn;
    private Button configFilterBtn;
    private Button titleEditBtn;
    private EditBox titleEditBox;

    private SmartFilter newSelection = null;
    private Component newTitle = null;
    private boolean showingTitleEdit = false;

    public FilterScreen(Component component, SmartFilter filter, InteractionHand interactionHand) {
        super(component);
        this.interactionHand = interactionHand;

        selectionPanel = null;

        this.filter = filter;
        this.origFilterStr = filter.asString(FTBFilterSystemClient.registryAccess());
    }

    @Override
    protected void init() {
        super.init();

        setupGuiDimensions();

        // add these early; they will render above other widgets so need to get mouse clicks first
        getSelectionPanel().visitWidgets(this::addWidget);

        addWidget(filterList = new FilterList(minecraft, leftPos + 8, topPos + 20, getListWidth(), getListHeight()));

        titleEditBtn = addRenderableWidget(new ImageButton(leftPos, topPos + 3, 16, 16,
                new WidgetSprites(Textures.EDIT_BUTTON, Textures.EDIT_BUTTON_HI),
                b -> showingTitleEdit = true));

        titleEditBox = addRenderableWidget(new EditBox(font, leftPos + 5, topPos + 4, getListWidth(), font.lineHeight + 4, Component.empty()));
        titleEditBox.visible = false;
        titleEditBox.setValue(title.getString());

        int buttonWidth = guiWidth - getListWidth() - 25;
        LinearLayout buttonPanel = new LinearLayout(leftPos + getListWidth() + 15, topPos + 20, LinearLayout.Orientation.VERTICAL).spacing(2);
        addFilterBtn = buttonPanel.addChild(Button.builder(Component.translatable("ftbfiltersystem.gui.add"),
                b -> getSelectionPanel().setVisible(true)).width(buttonWidth).build());
        deleteFilterBtn = buttonPanel.addChild(Button.builder(Component.translatable("ftbfiltersystem.gui.delete"),
                b -> deleteSelectedFilter(true)).width(buttonWidth).build());
        configFilterBtn = buttonPanel.addChild(Button.builder(Component.translatable("ftbfiltersystem.gui.configure"),
                b -> configureSelectedFilter(false)).width(buttonWidth).build());
        buttonPanel.arrangeElements();
        buttonPanel.visitWidgets(this::addRenderableWidget);

        LinearLayout bottomPanel = new LinearLayout(leftPos, topPos + guiHeight - 25, LinearLayout.Orientation.HORIZONTAL);
        bottomPanel.addChild(new FrameLayout(guiWidth / 2, 20))
                .addChild(Button.builder(CommonComponents.GUI_DONE, b -> applyChanges()).size(80, 20).build());
        bottomPanel.addChild(new FrameLayout(guiWidth / 2, 20))
                .addChild(Button.builder(CommonComponents.GUI_CANCEL, b -> closeWithConfirmation()).size(80, 20).build());
        bottomPanel.arrangeElements();
        bottomPanel.visitWidgets(this::addRenderableWidget);

        if (filterList.getSelected() == null) {
            findAndSelect(filter);
        } else {
            setupButtons();
        }
    }

    private void applyNewTitle() {
        newTitle = Component.literal(titleEditBox.getValue());
        if (newTitle.getString().equals(title.getString())) {
            newTitle = null;
        }
        showingTitleEdit = false;
    }

    private SelectionPanel getSelectionPanel() {
        if (selectionPanel == null) {
            selectionPanel = new SelectionPanel(font, this::createNewFilter, height - (topPos + 20) - 5);
        }
        return selectionPanel;
    }

    private void setupButtons() {
        FilterList.FilterEntry selected = filterList.getSelected();
        addFilterBtn.active = selected != null && canAddMoreFilters(selected.dumpedFilter.filter());
        deleteFilterBtn.active = selected != null && selected.dumpedFilter.filter().getParent() != null;
        configFilterBtn.active = selected != null && selected.dumpedFilter.filter().isConfigurable();
    }

    private boolean canAddMoreFilters(SmartFilter filter) {
        SmartFilter.Compound parent = filter instanceof AbstractCompoundFilter acf ? acf : filter.getParent();
        return parent != null && parent.getChildren().size() < parent.maxChildren();
    }

    private void applyChanges() {
        NetworkManager.sendToServer(new SyncFilterMessage(
                filter.asString(FTBFilterSystemClient.registryAccess()), newTitle == null ? Optional.empty() : Optional.of(newTitle.getString()), interactionHand)
        );

        onClose();

        if (changesHaveBeenMade()) {
            Minecraft.getInstance().player.displayClientMessage(
                    Component.translatable("ftbfiltersystem.message.changes_saved").withStyle(ChatFormatting.GREEN),
                    true);
        }
    }

    private boolean changesHaveBeenMade() {
        return !filter.asString(FTBFilterSystemClient.registryAccess()).equals(origFilterStr) || newTitle != null;
    }

    private void createNewFilter(Identifier filterId) {
        getSelectionPanel().setVisible(false);

        if (filterList.getSelected() != null) {
            SmartFilter selectedFilter = filterList.getSelected().dumpedFilter.filter();
            SmartFilter.Compound parent = selectedFilter instanceof AbstractCompoundFilter acf ? acf : selectedFilter.getParent();
            if (parent != null) {
                FTBFilterSystemAPI.api().createDefaultFilter(parent, filterId).ifPresent(newFilter -> {
                    parent.getChildren().add(newFilter);
                    filterList.addChildren();
                    filterList.findAndSelect(newFilter);
                    configureSelectedFilter(true);
                });
            }
        }
    }

    public void deleteSelectedFilter(boolean rebuildList) {
        if (filterList.getSelected() != null) {
            SmartFilter subFilter = filterList.getSelected().dumpedFilter.filter();
            SmartFilter parent = subFilter.getParent();
            if (parent instanceof SmartFilter.Compound compound) { // ignore attempt to delete root filter
                List<SmartFilter> l = compound.getChildren().stream().filter(f -> f != subFilter).toList();
                compound.getChildren().clear();
                compound.getChildren().addAll(l);
                if (rebuildList) {
                    filterList.addChildren();
                    filterList.findAndSelect(parent);
                }
            }
        }
    }

    private void configureSelectedFilter(boolean deleteOnCancel) {
        FilterList.FilterEntry selected = filterList.getSelected();
        if (selected != null) {
            FTBFilterSystemClient.INSTANCE.openFilterConfigScreen(selected.dumpedFilter.filter(), this, deleteOnCancel);
        }
    }

    public <T extends SmartFilter> void replaceFilter(T filter, T newFilter) {
        if (filter.getParent() != null) {
            List<SmartFilter> children = filter.getParent().getChildren();
            int idx = children.indexOf(filter);
            if (idx >= 0) {
                children.set(idx, newFilter);
                newSelection = newFilter;
            }
        }
    }

    @Override
    public void tick() {
        if (!(minecraft.player.getItemInHand(interactionHand).getItem() instanceof SmartFilterItem)) {
            // shouldn't normally happen, but some mod could possibly modify the held item while we're in the GUI
            onClose();
        } else {
            if (showingTitleEdit && !titleEditBox.isVisible()) {
                titleEditBox.visible = true;
                titleEditBtn.visible = false;
                if (Minecraft.getInstance().hasShiftDown()) {
                    titleEditBox.moveCursorToEnd(false);
                    titleEditBox.setHighlightPos(0);
                }
                setFocused(titleEditBox);
            } else if (!showingTitleEdit && titleEditBox.isVisible()) {
                titleEditBox.visible = false;
                titleEditBtn.visible = true;
                setFocused(filterList);
            }
        }
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);

        if (guiHeight > 0) {
            GuiUtil.drawPanel(guiGraphics, new Rect2i(leftPos + 7, topPos + 19, getListWidth() + 2, getListHeight() + 1),
                    0xFFA0A0A0, 0xFFA0A0A0, GuiUtil.BorderStyle.INSET, 1);
            filterList.renderWidget(guiGraphics, mouseX, mouseY, partialTick);

            Component displayTitle = newTitle == null ? title : newTitle;
            if (!titleEditBox.isVisible()) {
                guiGraphics.drawString(font, displayTitle, leftPos + 8, topPos + 7, 0xFF404040, false);
            }
            titleEditBtn.setX(leftPos + font.width(displayTitle) + 8);
        }

        if (getSelectionPanel().isVisible()) {
            guiGraphics.pose().pushMatrix();
            guiGraphics.pose().translate(0, 0);
            guiGraphics.fill(leftPos, topPos, leftPos + guiWidth, topPos + guiHeight, 0xA0202020);
            getSelectionPanel().positionAndRender(guiGraphics, addFilterBtn.getY(), addFilterBtn.getX() - 10, mouseX, mouseY, partialTick);
            guiGraphics.pose().popMatrix();
        }
    }

    @Override
    public void renderBackground(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.renderBackground(guiGraphics, mouseX, mouseY, partialTick);

        guiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED, Textures.BACKGROUND, leftPos, topPos, guiWidth, guiHeight);
    }

    @Override
    public void onClose() {
        getSelectionPanel().setVisible(false);

        super.onClose();
    }

    @Override
    public boolean keyPressed(KeyEvent keyEvent) {
        int keyCode = keyEvent.key();
        if (keyCode == InputConstants.KEY_ESCAPE || minecraft.options.keyInventory.matches(keyEvent)) {
            if (getSelectionPanel().isVisible()) {
                getSelectionPanel().setVisible(false);
                return true;
            } else if (titleEditBox.canConsumeInput()) {
                if (keyCode == InputConstants.KEY_ESCAPE) {
                    titleEditBox.setValue(newTitle == null ? title.getString() : newTitle.getString());
                    showingTitleEdit = false;
                } else {
                    titleEditBox.keyPressed(keyEvent);
                }
                return true;
            } else {
                closeWithConfirmation();
                return true;
            }
        } else if (keyCode == InputConstants.KEY_RETURN || keyCode == InputConstants.KEY_NUMPADENTER) {
            if (titleEditBox.canConsumeInput()) {
                applyNewTitle();
                return true;
            } else if (Minecraft.getInstance().hasShiftDown()) {
                applyChanges();
                return true;
            }
        }
        return super.keyPressed(keyEvent);
    }

    private void closeWithConfirmation() {
        if (changesHaveBeenMade()) {
            newSelection = filterList.getSelectedFilter();
            minecraft.setScreen(new ConfirmScreen(this::exitCallback,
                    Component.translatable("ftbfiltersystem.gui.changes_made"),
                    Component.translatable("ftbfiltersystem.gui.changes_made.question")
            ));
        } else {
            onClose();
        }
    }

    private void exitCallback(boolean confirmed) {
        if (confirmed) {
            onClose();
        } else {
            minecraft.setScreen(this);
            findAndSelect(newSelection);
        }
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClicked) {
        if (getSelectionPanel().isVisible() && !getSelectionPanel().isMouseOver(event.x(), event.y())) {
            getSelectionPanel().setVisible(false);
            return true;
        }
        return super.mouseClicked(event, doubleClicked);
    }

    private int getListWidth() {
        return guiWidth - 100;
    }

    private int getListHeight() {
        return guiHeight - 50;
    }

    @Override
    public void resize(int width, int height) {
        this.width = width;
        this.height = height;

        setupGuiDimensions();

        SmartFilter selected = filterList.getSelected() == null ? null : filterList.getSelected().dumpedFilter.filter();

        repositionElements();

        getSelectionPanel().resize(height - (topPos + 20) - 5);

        findAndSelect(selected);
    }

    private void setupGuiDimensions() {
        guiWidth = width * 2 / 3;
        guiHeight = height * 3 / 4;
        leftPos = (width - guiWidth) / 2;
        topPos = (height - guiHeight) / 2;
    }

    public void findAndSelect(@Nullable SmartFilter filter) {
        if (filter != null) {
            filterList.findAndSelect(filter);
        }
    }

    private class FilterList extends ObjectSelectionList<FilterList.FilterEntry> {
        private static final int ELEMENT_HEIGHT = 12;
        private FilterEntry dragging = null;
        private SmartFilter.Compound dragTarget = null;

        public FilterList(Minecraft minecraft, int x, int y, int width, int height) {
            super(minecraft, width, height, y, ELEMENT_HEIGHT);

            setX(x);
            addChildren();
        }

        @Override
        protected void renderListBackground(GuiGraphics guiGraphics) {
        }

        private void addChildren() {
            List<FilterEntry> entries = new ArrayList<>();

            List<DumpedFilter> dumped = FTBFilterSystemAPI.api().dump(filter);
            int idx = 0;
            for (DumpedFilter entry : dumped) {
                entries.add(new FilterEntry(entry, idx++));
            }

            replaceEntries(entries);

            if (newSelection != null) {
                findAndSelect(newSelection);
                newSelection = null;
            }
        }

        private SmartFilter getSelectedFilter() {
            return getSelected() == null ? null : getSelected().dumpedFilter.filter();
        }

        @Override
        public int getRowWidth() {
            return getListWidth();
        }

        @Override
        protected int scrollBarX() {
            return leftPos + width + 8;
        }

        @Override
        public void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {
        }

        @Override
        public void setSelected(@Nullable FilterEntry entry) {
            super.setSelected(entry);

            setupButtons();
        }

        @Override
        public boolean mouseReleased(MouseButtonEvent event) {
            if (dragging != null && dragTarget != null) {
                FilterParser.parseFilterList(dragTarget, dragging.dumpedFilter.filter().asString(FTBFilterSystemClient.registryAccess()), FTBFilterSystemClient.registryAccess()).stream()
                        .findFirst()
                        .ifPresent(newFilter -> {
                            dragTarget.getChildren().add(newFilter);
                            deleteSelectedFilter(false);
                            addChildren();
                            findAndSelect(newFilter);
                        });
            }
            dragging = null;
            dragTarget = null;

            return super.mouseReleased(event);
        }

        @Override
        public boolean mouseDragged(MouseButtonEvent event, double pDragX, double pDragY) {
            if (dragging != null) {
                FilterEntry entry = getEntryAtPosition(event.x(), event.y());
                if (entry != null && entry.dumpedFilter.filter() instanceof SmartFilter.Compound compound
                        && (!(dragging.dumpedFilter.filter() instanceof SmartFilter.Compound) || entry.dumpedFilter.indent() <= dragging.dumpedFilter.indent())
                        && compound != dragging.dumpedFilter.filter()
                        && compound != dragging.dumpedFilter.filter().getParent()
                        && compound.getChildren().size() < compound.maxChildren())
                {
                    dragTarget = compound;
                } else {
                    dragTarget = null;
                }
                return true;
            } else {
                return super.mouseDragged(event, pDragX, pDragY);
            }
        }

        @Override
        public boolean keyPressed(KeyEvent event) {
            if (event.key() == InputConstants.KEY_SPACE || event.key() == InputConstants.KEY_RETURN) {
                configureSelectedFilter(false);
                return true;
            }
            return super.keyPressed(event);
        }

        @Override
        public void renderWidget(GuiGraphics guiGraphics, int pMouseX, int pMouseY, float partialTick) {
            super.renderWidget(guiGraphics, pMouseX, pMouseY, partialTick);

            if (dragging != null) {
                FilterEntry entry = getEntryAtPosition(pMouseX, pMouseY);
                if (entry != null && entry.dumpedFilter.filter() != dragging.dumpedFilter.filter()) {
                    int w = font.width(dragging.dumpedFilter.filter().getDisplayName());
                    guiGraphics.fill(pMouseX, pMouseY - ELEMENT_HEIGHT / 2 + 1, pMouseX + w + 10, pMouseY + ELEMENT_HEIGHT / 2, 0xC0E1F1FD);
                    guiGraphics.renderOutline(pMouseX, pMouseY - ELEMENT_HEIGHT / 2 + 1, w + 10, ELEMENT_HEIGHT, 0xC0404040);
                    guiGraphics.drawString(font, dragging.dumpedFilter.filter().getDisplayName(), pMouseX + 5, pMouseY - ELEMENT_HEIGHT / 2 + 3, 0xC0404040, false);
                }
            }
        }

        @Override
        protected void renderSelection(GuiGraphics guiGraphics, FilterEntry entry, int color) {
            int minX = this.getX() + (this.width - entry.getWidth()) / 2;
            int maxX = this.getX() + (this.width + entry.getWidth()) / 2;
            int col = isFocused() ? 0xFFE1F1FD : 0xFFA6B4C4;
            GuiUtil.drawPanel(guiGraphics, new Rect2i(minX + 1, entry.getY() - 2, maxX - minX - 2, entry.getHeight()), col,
                    0xFF4663AC, GuiUtil.BorderStyle.PLAIN, 1);
        }

        @Override
        protected void renderListSeparators(GuiGraphics guiGraphics) {
        }

        private void findAndSelect(SmartFilter filter) {
            children().stream().filter(entry -> entry.dumpedFilter.filter() == filter)
                    .findFirst()
                    .ifPresent(this::setSelected);
        }

        private class FilterEntry extends ObjectSelectionList.Entry<FilterEntry> {
            private final DumpedFilter dumpedFilter;
            private final int index;
            private long lastClickTime;

            public FilterEntry(DumpedFilter dumpedFilter, int index) {
                this.dumpedFilter = dumpedFilter;
                this.index = index;
            }

            @Override
            public void renderContent(GuiGraphics guiGraphics, int i, int j, boolean isMouseOver, float partialTick) {
                int labelLeft = getContentX() + dumpedFilter.indent() * 10;
                if (dumpedFilter.filter() == dragTarget && dragging.dumpedFilter.filter() != dragTarget) {
                    guiGraphics.fill(labelLeft - 2, getY() - 3, labelLeft + font.width(getLabel()) + 2, getY() + font.lineHeight, 0xFFCAE9BE);
                    guiGraphics.renderOutline(labelLeft - 2, getY() - 3, font.width(getLabel()) + 4, font.lineHeight + 4, 0xFF306844);
                }
                guiGraphics.drawString(font, getLabel(), labelLeft, getY(),  0xFF404040, false);
                if (index > 0) {
                    int yBase = getY() + getContentHeight() / 2;
                    guiGraphics.hLine(labelLeft - 8, labelLeft - 2, yBase, 0xFF505080);
                    int yOff = calcYoffset(index);
                    guiGraphics.vLine(labelLeft - 8, yBase, yBase - yOff, 0xFF505080);
                }
            }

            private int calcYoffset(int index) {
                int res = 0;
                for (int i = index - 1; i >= 0; i--) {
                    res += getHeight();
                    if (children().get(i).dumpedFilter.indent() < dumpedFilter.indent()) break;
                }
                return res - 4;
            }

            @Override
            public boolean mouseClicked(MouseButtonEvent event, boolean doubleClicked) {
                FilterList.this.setSelected(this);

                if (doubleClicked) {
                    FilterScreen.this.configureSelectedFilter(false);
                } else if (dumpedFilter.filter().getParent() != null) {
                    dragging = this;
                }

//                if (Util.getMillis() - this.lastClickTime < 250L) {
//                    FilterScreen.this.configureSelectedFilter(false);
//                } else {
//                    this.lastClickTime = Util.getMillis();
//                }
                return true;
            }

            private Component getLabel() {
                Component disp = dumpedFilter.filter().getDisplayName();
                if (dumpedFilter.filter() instanceof SmartFilter.Compound) {
                    return disp;
                } else {
                    Component text = dumpedFilter.filter().getDisplayArg(minecraft.level.registryAccess());
                    return disp.copy().append(" ").append(text.copy().withStyle(ChatFormatting.DARK_BLUE));
                }
            }

            @Override
            public Component getNarration() {
                return Component.translatable("narrator.select", getLabel());
            }
        }
    }
}
