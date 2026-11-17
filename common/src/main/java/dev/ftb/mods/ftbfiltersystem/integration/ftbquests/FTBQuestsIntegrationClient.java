package dev.ftb.mods.ftbfiltersystem.integration.ftbquests;

import dev.ftb.mods.ftblibrary.client.util.ClientUtils;
import dev.ftb.mods.ftbquests.client.gui.quests.QuestScreen;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.Nullable;

import java.util.List;

public class FTBQuestsIntegrationClient {
    @Nullable
    public static List<ItemStack> getItemsFromViewQuestPanel() {
        if (ClientUtils.getCurrentGuiAs(QuestScreen.class) instanceof QuestScreen questScreen) {
            return questScreen.viewQuestPanel.getItemTaskItems();
        }
        return null;
    }
}
