package dev.ftb.mods.ftbfiltersystem.api.event;

import dev.ftb.mods.ftblibrary.platform.event.TypedEvent;
import dev.ftb.mods.ftblibrary.util.result.Outcome;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

/// Fired when a Custom Filter tries to match an item. Mods can hook into this to provide custom matching
/// functionality without registering a specific filter.
@FunctionalInterface
public interface CustomFilterEvent {
    //    Event<CustomFilterEvent> MATCH_ITEM = EventFactory.createEventResult();
    TypedEvent<CustomFilterEvent.Data, Outcome> TYPE = TypedEvent.of(CustomFilterEvent.Data.class);

    Outcome match(Data data);

    /// Called to match an item stack. Return a true result if the item matches, and a false result if the item
    /// does not match. Returning a pass result indicates the handler doesn't care; if all handlers for the event
    /// pass, then it's considered to be a no-match outcome.
    ///
    /// @param stack the item stack to be tested
    /// @param id an identifier for the event; must be non-null. Primarily used for KubeJS event id purposes.
    /// @param extraData free-form text data; the event handler can interpret this as it wishes,
    ///                  including ignoring it completely
    record Data(ItemStack stack, @NotNull String id, String extraData) {
    }

//    /**
//     * Called to match an item stack. Return a true result if the item matches, and a false result if the item
//     * does not match. Returning a pass result indicates the handler doesn't care; if all handlers for the event
//     * pass, then it's considered to be a no-match outcome.
//     *
//     * @param stack the item stack to be tested
//     * @param id an identifier for the event; must be non-null. Primarily used for KubeJS event id purposes.
//     * @param extraData free-form text data; the event handler can interpret this as it wishes,
//     *                  including ignoring it completely
//     * @return an event result indicating whether the stack matches
//     */
//    EventResult matchItem(ItemStack stack, @NotNull String id, String extraData);
}
