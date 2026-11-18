package dev.ftb.mods.ftbfiltersystem.util;

import dev.ftb.mods.ftbfiltersystem.api.FTBFilterSystemAPI;
import dev.ftb.mods.ftbfiltersystem.filter.ItemFilter;
import dev.ftb.mods.ftbfiltersystem.filter.compound.OrFilter;
import dev.ftb.mods.ftbfiltersystem.registry.ModItems;
import dev.ftb.mods.ftbfiltersystem.registry.item.SmartFilterItem;
import it.unimi.dsi.fastutil.Hash;
import it.unimi.dsi.fastutil.objects.ObjectOpenCustomHashSet;
import net.minecraft.Util;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

public class FFSUtil {
    private static final Hash.Strategy<? super ItemStack> TYPE_AND_TAG = new Hash.Strategy<>() {
        public int hashCode(final @Nullable ItemStack item) {
            return ItemStack.hashItemAndComponents(item);
        }

        public boolean equals(final @Nullable ItemStack a, final @Nullable ItemStack b) {
            return a == b || a != null && b != null && a.isEmpty() == b.isEmpty() && ItemStack.isSameItemSameComponents(a, b);
        }
    };

    public static OrFilterResult buildItemOrFilter(Collection<ItemStack> items) {
        Set<ItemStack> set = new ObjectOpenCustomHashSet<>(items, TYPE_AND_TAG);

        var actualItems = set.stream()
                .filter(s -> !s.isEmpty() && !FTBFilterSystemAPI.api().isFilterItem(s))
                .toList();
        var str = actualItems.stream().map(stack -> BuiltInRegistries.ITEM.getKey(stack.getItem()).toString())
                .sorted()
                .map(s -> ItemFilter.ID + "(" + s + ")")
                .collect(Collectors.joining(" "));

        return new OrFilterResult(Util.make(new ItemStack(ModItems.SMART_FILTER.get()),
                s -> {
                    SmartFilterItem.setFilter(s, OrFilter.ID + "(" + str + ")");
                    s.set(DataComponents.CUSTOM_NAME, Component.translatable("ftbfiltersystem.message.or_filter", actualItems.size()));
                }),
                actualItems.size()
        );
    }

    public record OrFilterResult(ItemStack filterStack, int itemCount) {
    }
}
