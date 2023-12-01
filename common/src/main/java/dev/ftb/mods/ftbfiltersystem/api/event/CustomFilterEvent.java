package dev.ftb.mods.ftbfiltersystem.api.event;

import dev.architectury.event.Event;
import dev.architectury.event.EventFactory;
import dev.architectury.event.EventResult;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * Fired when a Custom Filter tries to match an item. Mods can hook into this to provide custom matching
 * functionality without registering a specific filter.
 */
public interface CustomFilterEvent {
    Event<CustomFilterEvent> MATCH_ITEM = EventFactory.createEventResult();

    /**
     * Called to match an item stack. Return a true result if the item matches, and a false result if the item
     * does not match. Returning a pass result indicates the handler doesn't care; if all handlers for the event
     * pass, then it's considered to be a no-match outcome.
     *
     * @param stack the item stack to be tested
     * @param id an identifier for the event; must be non-null. Primarily used for KubeJS event id purposes.
     * @param extraData free-form text data; the event handler can interpret this as it wishes,
     *                  including ignoring it completely
     * @return an event result indicating whether the stack matches
     */
    EventResult matchItem(ItemStack stack, @NotNull String id, String extraData);
}
