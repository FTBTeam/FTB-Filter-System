package dev.ftb.mods.ftbfiltersystem.client.gui;

import dev.ftb.mods.ftbfiltersystem.api.client.gui.AbstractFilterConfigScreen;
import dev.ftb.mods.ftbfiltersystem.api.client.gui.AbstractFilterScreen;
import dev.ftb.mods.ftbfiltersystem.client.GuiUtil;
import dev.ftb.mods.ftbfiltersystem.client.gui.widget.ItemWidget;
import dev.ftb.mods.ftbfiltersystem.filter.ItemFilter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class ItemConfigScreen extends AbstractFilterConfigScreen<ItemFilter> implements GhostDropReceiver {
    private static final int SEARCH_ROWS = 4;
    private static final int SEARCH_COLS = 9;

    // things we want to remember across gui invocations
    private static ItemSource itemSource = ItemSource.CREATIVE;
    private static String lastSearch = "";

    private static List<SearchEntry> cachedCreativeEntries;
    private static List<SearchEntry> cachedInventoryEntries;

    private final List<ItemStack> currentStacks = new ArrayList<>();
    private Rect2i scrollArea;
    private double currentScroll;
    private boolean isScrolling;

    private EditBox searchField;
    private final List<SearchItemWidget> itemWidgets = new ArrayList<>();
    private ItemWidget selectedWidget;

    public ItemConfigScreen(ItemFilter filter, AbstractFilterScreen parentScreen) {
        super(filter, parentScreen,194, 117);

        cachedInventoryEntries = null; // recheck inventory each time we open
    }

    @Override
    protected void init() {
        super.init();

        searchField = makeSearchEditBox(leftPos + 8, topPos + 38, () -> lastSearch, s -> lastSearch = s);

        addRenderableWidget(searchField);
        setFocused(searchField);

        selectedWidget = addRenderableWidget(new ItemWidget(leftPos + 8, topPos + 17, 18, 18, ItemStack.EMPTY));

        int btnWidth = Arrays.stream(ItemSource.values())
                .map(val -> font.width(val.getDisplayName()))
                .max(Integer::compareTo)
                .orElse(50) + 10;

        addRenderableWidget(CycleButton.builder(ItemSource::getDisplayName, itemSource)
                .withValues(ItemSource.values())
                .displayOnlyValue()
                .create(leftPos + guiWidth - btnWidth - 21, topPos + 5, btnWidth, 16, Component.empty(),
                        (btn, val) -> { itemSource = val; updateSearchEntries(); }
                ));

        itemWidgets.clear();
        for (int row = 0; row < SEARCH_ROWS; ++row) {
            for (int col = 0; col < SEARCH_COLS; ++col) {
                SearchItemWidget w = new SearchItemWidget(row, col);
                itemWidgets.add(w);
                addRenderableWidget(w);
            }
        }

        scrollArea = new Rect2i(leftPos + 174, topPos + 55, 14, 70);

        setSelectedStack(new ItemStack(filter.getMatchItem()));

        updateSearchEntries();
    }

    private void setSelectedStack(ItemStack stack) {
        selectedWidget.setStack(stack.copy());
    }

    @Override
    protected void doScheduledUpdate() {
        updateSearchEntries();
    }

    @Override
    public void render(GuiGraphics guiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        super.render(guiGraphics, pMouseX, pMouseY, pPartialTick);

        int sx = scrollArea.getX();
        int sy1 = scrollArea.getY();
        int sy2 = sy1 + scrollArea.getHeight();

        FormattedText txt = GuiUtil.ellipsize(font, selectedWidget.getStack().getItem().getName(selectedWidget.getStack()), leftPos + guiWidth - (selectedWidget.getX() + selectedWidget.getWidth() + 6));
        guiGraphics.drawString(font, Language.getInstance().getVisualOrder(txt), selectedWidget.getX() + selectedWidget.getWidth() + 3, selectedWidget.getY() + 8, 0xFF404040, false);

        guiGraphics.fill(scrollArea.getX() - 2, scrollArea.getY() - 2, scrollArea.getX() + scrollArea.getWidth(), scrollArea.getY() + scrollArea.getHeight(), 0xFF808080);
        guiGraphics.fill(scrollArea.getX() - 1, scrollArea.getY() - 1, scrollArea.getX() + scrollArea.getWidth() - 1, scrollArea.getY() + scrollArea.getHeight() - 1, 0xFFA0A0A0);
//        guiGraphics.blit(SCROLL_TEXTURE, sx, sy1 + (int) ((sy2 - sy1 - 17) * currentScroll), 232 + (needsScrollBars() ? 0 : 12), 0, 12, 15);
        guiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED, Identifier.withDefaultNamespace("container/creative_inventory/scroller"), sx, sy1 + (int) ((sy2 - sy1 - 17) * currentScroll), 12, 15);
    }

    @Override
    public boolean mouseScrolled(double x, double y, double dirX, double dirY) {
        if (dirY != 0 && needsScrollBars()) {
            int j = currentStacks.size() / (SEARCH_COLS + 1) - (SEARCH_ROWS - 1) + 1;
            float i = dirY > 0 ? 1f : -1f;
            scrollTo(Mth.clamp(currentScroll - i / j, 0.0, 1.0));
            return true;
        }
        return super.mouseScrolled(x, y, dirX, dirY);
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClicked) {
        isScrolling = event.button() == 0 && needsScrollBars() && scrollArea.contains((int) event.x(), (int) event.y());
        if (isScrolling) {
            scrollToMouse(event.y());
            return true;
        }
        return super.mouseClicked(event, doubleClicked);
    }

    @Override
    public boolean mouseDragged(MouseButtonEvent mouseButtonEvent, double dragX, double dragY) {
        if (isScrolling) {
            scrollToMouse(mouseButtonEvent.y());
            return true;
        }
        return super.mouseDragged(mouseButtonEvent, dragX, dragY);
    }

    @Override
    public boolean mouseReleased(MouseButtonEvent event) {
        isScrolling = false;

        return super.mouseReleased(event);
    }

    @Override
    protected ItemFilter makeNewFilter() {
        return new ItemFilter(filter.getParent(), selectedWidget.getStack().getItem());
    }

    private boolean needsScrollBars() {
        return currentStacks.size() > 36;
    }

    private void scrollToMouse(double mouseY) {
        scrollTo(Mth.clamp((mouseY - scrollArea.getY()) / scrollArea.getHeight(), 0.0, 1.0));
    }

    private void scrollTo(double scrollPos) {
        currentScroll = scrollPos;

        int i = currentStacks.size() / SEARCH_COLS - SEARCH_ROWS + 1;
        int j = Math.max(0, (int) (currentScroll * i + 0.5D));

        for (int row = 0; row < SEARCH_ROWS; ++row) {
            for (int col = 0; col < SEARCH_COLS; ++col) {
                int idx = col + (row + j) * SEARCH_COLS;
                ItemStack stack = idx >= 0 && idx < currentStacks.size() ? currentStacks.get(idx) : ItemStack.EMPTY;
                itemWidgets.get(row * SEARCH_COLS + col).setStack(stack);
            }
        }
    }

    private void updateSearchEntries() {
        currentStacks.clear();

        String srch = searchField.getValue().toLowerCase();
        List<ItemStack> applicableEntries = itemSource.get().stream()
                .filter(entry -> entry.test(srch))
                .map(entry -> entry.stack)
                .toList();

        currentStacks.addAll(applicableEntries);

        scrollTo(0.0);
    }


    @Override
    public Rect2i getGhostDropRegion() {
        return new Rect2i(selectedWidget.getX(), selectedWidget.getY(), selectedWidget.getWidth(), selectedWidget.getHeight());
    }

    @Override
    public void receiveGhostDrop(ItemStack stack) {
        selectedWidget.setStack(stack);
    }

    enum ItemSource {
        CREATIVE(ItemSource::getCreativeItems, "creative"),
        INV(ItemSource::getInventoryItems, "inventory");

        private final Supplier<List<SearchEntry>> itemSupplier;
        private final Component name;

        ItemSource(Supplier<List<SearchEntry>> itemSupplier, String name) {
            this.itemSupplier = itemSupplier;
            this.name = Component.translatable("ftbfiltersystem.gui.item_source." + name);
        }

        List<SearchEntry> get() {
            return itemSupplier.get();
        }

        public Component getDisplayName() {
            return name;
        }

        private static List<SearchEntry> getCreativeItems() {
            if (cachedCreativeEntries == null) {
                CreativeModeTabs.tryRebuildTabContents(FeatureFlags.DEFAULT_FLAGS, false, Minecraft.getInstance().level.registryAccess());
                cachedCreativeEntries = CreativeModeTabs.searchTab().getDisplayItems().stream()
                        .map(SearchEntry::new)
                        .toList();
            }
            return cachedCreativeEntries;
        }

        private static List<SearchEntry> getInventoryItems() {
            if (cachedInventoryEntries == null) {
                Inventory inv = Minecraft.getInstance().player.getInventory();
                cachedInventoryEntries = new ArrayList<>();
                for (int i = 9; i < 36; i++) {
                    cachedInventoryEntries.add(new SearchEntry(inv.getNonEquipmentItems().get(i)));
                }
                for (int i = 0; i < 9; i++) {
                    cachedInventoryEntries.add(new SearchEntry(inv.getNonEquipmentItems().get(i)));
                }
            }
            return cachedInventoryEntries;
        }
    }

    static class SearchEntry implements Predicate<String> {
        public final ItemStack stack;
        private final String tooltip;

        SearchEntry(ItemStack stack) {
            this.stack = stack;

            List<String> l;
            try {
                Minecraft mc = Minecraft.getInstance();
                l = stack.getTooltipLines(Item.TooltipContext.of(mc.level), mc.player, mc.options.advancedItemTooltips ? TooltipFlag.Default.ADVANCED : TooltipFlag.Default.NORMAL).stream()
                        .map(Component::getString)
                        .collect(Collectors.toList());
            } catch (Exception ignored) {
                // it's possible some modded item could have a buggy tooltip implementation
                l = Collections.emptyList();
            }
            tooltip = String.join("\n", l).toLowerCase();
        }

        @Override
        public boolean test(String searchString){
            return tooltip.contains(searchString);
        }
    }

    private class SearchItemWidget extends ItemWidget {
        public SearchItemWidget(int row, int col) {
            super(ItemConfigScreen.this.leftPos + 8 + 18 * col, ItemConfigScreen.this.topPos + 53 + 18 * row, 18, 18, ItemStack.EMPTY);
        }

        @Override
        protected void handleClick(boolean doubleClick) {
            if (doubleClick) {
                applyChanges();
            } else {
                setSelectedStack(getStack());
            }
        }
    }
}
