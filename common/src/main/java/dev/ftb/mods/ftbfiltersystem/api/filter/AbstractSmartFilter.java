package dev.ftb.mods.ftbfiltersystem.api.filter;

import dev.ftb.mods.ftbfiltersystem.api.FTBFilterSystemAPI;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Base class for all non-compound smart filters.
 */
public abstract class AbstractSmartFilter implements SmartFilter {
    private final SmartFilter.Compound parent;
    private Component displayName = null;

    protected AbstractSmartFilter(@Nullable SmartFilter.Compound parent) {
        this.parent = parent;
    }

    @Override
    public @Nullable SmartFilter.Compound getParent() {
        return parent;
    }

    @Override
    public Component getDisplayName() {
        if (displayName == null) {
            displayName = getDisplayName(this.getId());
        }
        return displayName;
    }

    @NotNull
    public static MutableComponent getDisplayName(ResourceLocation id) {
        return Component.translatable("filter." + id.toString().replace(':', '.') + ".name");
    }

    @NotNull
    public static MutableComponent getTooltip(ResourceLocation id) {
        return Component.translatable("filter." + id.toString().replace(':', '.') + ".tooltip");
    }
}
