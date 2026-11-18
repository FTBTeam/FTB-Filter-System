package dev.ftb.mods.ftbfiltersystem.client.gui;

import dev.architectury.platform.Platform;
import dev.ftb.mods.ftbfiltersystem.api.client.gui.AbstractFilterConfigScreen;
import dev.ftb.mods.ftbfiltersystem.api.client.gui.AbstractFilterScreen;
import dev.ftb.mods.ftbfiltersystem.client.gui.widget.CustomSelectionList;
import dev.ftb.mods.ftbfiltersystem.filter.ModFilter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ModConfigScreen extends AbstractFilterConfigScreen<ModFilter> implements GhostDropReceiver {
    private static String lastSearch;

    private final List<ModData> matchingModData = new ArrayList<>();

    private EditBox searchField;
    private ModList modList;

    public ModConfigScreen(ModFilter filter, AbstractFilterScreen parentScreen) {
        super(filter, parentScreen, 200, 176);
    }

    @Override
    protected void init() {
        super.init();

        searchField = makeSearchEditBox(leftPos + 8, topPos + 18, () -> lastSearch, s -> lastSearch = s);

        updateSearchEntries();

        modList = new ModList(minecraft, topPos + 32, getListWidth(), getListHeight());
        modList.setX(leftPos + 8);
        addWidget(modList);

        modList.children().stream()
                .filter(child -> child.modData.modId().equals(filter.getStringArg(minecraft.level.registryAccess())))
                .findFirst()
                .ifPresent(entry -> modList.selectAndCenter(entry));
    }

    @Override
    public void render(GuiGraphics guiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        super.render(guiGraphics, pMouseX, pMouseY, pPartialTick);

        modList.render(guiGraphics, pMouseX, pMouseY, pPartialTick);
    }

    @Override
    protected @Nullable ModFilter makeNewFilter() {
        return modList.getSelected() != null ?
                new ModFilter(filter.getParent(), modList.getSelected().modData.modId) :
                null;
    }

    @Override
    protected void doScheduledUpdate() {
        updateSearchEntries();

        modList.addChildren();
    }

    private int getListHeight() {
        return 148;
    }

    private int getListWidth() {
        return 176;
    }

    private void updateSearchEntries() {
        String srch = searchField.getValue().toLowerCase(Locale.ROOT);

        matchingModData.clear();
        matchingModData.addAll(Platform.getMods().stream()
                .filter(mod -> srch.isEmpty() || mod.getModId().toLowerCase(Locale.ROOT).contains(srch))
                .map(mod -> new ModData(mod.getModId(), mod.getName()))
                .sorted()
                .toList());
    }

    @Override
    public Rect2i getGhostDropRegion() {
        return new Rect2i(leftPos + 9, topPos + 33, getListWidth() + 6, getListHeight() + 2);
    }

    @Override
    public void receiveGhostDrop(ItemStack stack) {
        String modId = stack.getItem().arch$registryName().getNamespace();
        modList.children().stream()
                .filter(entry -> entry.modData.modId.equals(modId))
                .findFirst()
                .ifPresent(modEntry -> modList.selectAndCenter(modEntry));
    }

    private record ModData(String modId, String modName) implements Comparable<ModData> {
        @Override
        public int compareTo(@NotNull ModConfigScreen.ModData modData) {
            return modId.compareTo(modData.modId);
        }
    }

    private class ModList extends CustomSelectionList<ModList.ModEntry> {
        private static final int ELEMENT_HEIGHT = 12;

        public ModList(Minecraft minecraft, int y, int width, int height) {
            super(minecraft, width, height, y, ELEMENT_HEIGHT);
        }

        @Override
        protected void renderListBackground(GuiGraphics guiGraphics) {
        }

        @Override
        protected List<ModEntry> buildChildrenList() {
            return matchingModData.stream().map(ModEntry::new).toList();
        }

        private class ModEntry extends Entry<ModEntry> {
            private final ModData modData;

            private ModEntry(ModData modData) {
                this.modData = modData;
            }

            @Override
            public void render(GuiGraphics guiGraphics, int index, int top, int left, int width, int height, int mouseX, int mouseY, boolean isMouseOver, float partialTick) {
                Component txt = Component.literal(modData.modId()).withStyle(Style.EMPTY.withColor(0x202060))
                        .append(Component.literal(" ["))
                        .append(Component.literal(modData.modName()).withStyle(Style.EMPTY.withColor(0x804020)))
                        .append(Component.literal("]"));
                guiGraphics.drawString(font, txt, left + 1, top + 1, 0x404040, false);
            }

            @Override
            protected boolean onMouseClick(double x, double y, int button, boolean isDoubleClick) {
                ModList.this.setSelected(this);
                if (isDoubleClick) {
                    applyChanges();
                }
                return true;
            }

            @Override
            public Component getNarration() {
                return Component.translatable("narrator.select", modData.modId());
            }
        }
    }
}
