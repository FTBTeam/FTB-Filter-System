package dev.ftb.mods.ftbfiltersystem.client.gui;

import dev.ftb.mods.ftbfiltersystem.api.client.gui.AbstractFilterConfigScreen;
import dev.ftb.mods.ftbfiltersystem.api.client.gui.AbstractFilterScreen;
import dev.ftb.mods.ftbfiltersystem.client.gui.widget.CustomSelectionList;
import dev.ftb.mods.ftbfiltersystem.filter.ItemTagFilter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

public class ItemTagConfigScreen extends AbstractFilterConfigScreen<ItemTagFilter> {
    private static String lastSearch;

    private final List<TagKey<Item>> matchingTags = new ArrayList<>();

    private EditBox searchField;
    private ItemTagList itemTagList;

    public ItemTagConfigScreen(ItemTagFilter filter, AbstractFilterScreen parentScreen) {
        super(filter, parentScreen, 200, 176);
    }

    @Override
    protected void init() {
        super.init();

        searchField = makeSearchEditBox(leftPos + 8, topPos + 18, () -> lastSearch, s -> lastSearch = s);

        updateSearchEntries();

        itemTagList = new ItemTagList(minecraft,topPos + 32, getListWidth(), getListHeight());
        itemTagList.setX(leftPos + 8);
        addWidget(itemTagList);

        itemTagList.children().stream()
                .filter(child -> child.tagKey.equals(filter.getTagKey()))
                .findFirst()
                .ifPresent(entry -> itemTagList.selectAndCenter(entry));
    }

    @Override
    public void render(GuiGraphics guiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        super.render(guiGraphics, pMouseX, pMouseY, pPartialTick);

        itemTagList.render(guiGraphics, pMouseX, pMouseY, pPartialTick);
    }

    @Override
    protected @Nullable ItemTagFilter makeNewFilter() {
        ResourceLocation location;
        if (itemTagList.getSelected() != null) {
            return new ItemTagFilter(filter.getParent(), itemTagList.getSelected().tagKey);
        } else if (itemTagList.children().isEmpty() && (location = ResourceLocation.tryParse(searchField.getValue())) != null) {
            return new ItemTagFilter(filter.getParent(), TagKey.create(Registries.ITEM, location));
        } else {
            return null;
        }
    }

    @Override
    protected void doScheduledUpdate() {
        updateSearchEntries();

        itemTagList.addChildren();
    }

    private int getListHeight() {
        return 148;
    }

    private int getListWidth() {
        return 176;
    }

    private void updateSearchEntries() {
        String srch = searchField.getValue().toLowerCase(Locale.ROOT);

        matchingTags.clear();
        matchingTags.addAll(BuiltInRegistries.ITEM.getTagNames()
                .filter(tagKey -> srch.isEmpty() || tagKey.location().toString().toLowerCase(Locale.ROOT).contains(srch))
                .sorted(Comparator.comparing(TagKey::location))
                .toList());
    }

    private class ItemTagList extends CustomSelectionList<ItemTagList.ItemTagEntry> {
        private static final int ELEMENT_HEIGHT = 12;

        public ItemTagList(Minecraft minecraft, int y, int width, int height) {
            super(minecraft, width, height, y, ELEMENT_HEIGHT);
        }

        @Override
        protected void renderListBackground(GuiGraphics guiGraphics) {
        }

        @Override
        protected List<ItemTagEntry> buildChildrenList() {
            return matchingTags.stream().map(ItemTagEntry::new).toList();
        }

        private class ItemTagEntry extends Entry<ItemTagEntry> {
            private final TagKey<Item> tagKey;

            private ItemTagEntry(TagKey<Item> tagKey) {
                this.tagKey = tagKey;
            }

            @Override
            public void render(GuiGraphics guiGraphics, int index, int top, int left, int width, int height, int mouseX, int mouseY, boolean isMouseOver, float partialTick) {
                Component txt = Component.literal(tagKey.location().getNamespace()).withStyle(Style.EMPTY.withColor(0x202060))
                        .append(Component.literal(":"))
                        .append(Component.literal(tagKey.location().getPath()).withStyle(Style.EMPTY.withColor(0x804020)));
                guiGraphics.drawString(font, txt, left + 1, top + 1, 0x404040, false);
            }

            @Override
            protected boolean onMouseClick(double x, double y, int button, boolean isDoubleClick) {
                ItemTagList.this.setSelected(this);
                if (isDoubleClick) {
                    applyChanges();
                }
                return true;
            }

            @Override
            public Component getNarration() {
                return Component.translatable("narrator.select", tagKey.location().toString());
            }
        }

    }
}
