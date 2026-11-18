package dev.ftb.mods.ftbfiltersystem.integration.ftbquests;

import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class FTBQuestsIntegrationClient {
    @Nullable
    public static List<ItemStack> getItemsFromViewQuestPanel() {
        // TODO stub - not yet implemented in FTB Quests 1.21
//        if (ClientUtils.getCurrentGuiAs(QuestScreen.class) instanceof QuestScreen questScreen) {
//            return questScreen.viewQuestPanel.getItemTaskItems();
//        }
        return null;
    }
}
