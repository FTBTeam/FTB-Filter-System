package dev.ftb.mods.ftbfiltersystem.api.filter;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.Nullable;

import java.util.Objects;

/// Base class for all non-compound smart filters.
public abstract class AbstractSmartFilter implements SmartFilter {
    private final SmartFilter.@Nullable Compound parent;
    @Nullable
    private Component displayName = null;

    protected AbstractSmartFilter(SmartFilter.@Nullable Compound parent) {
        this.parent = parent;
    }

    @Override
    public SmartFilter.@Nullable Compound getParent() {
        return parent;
    }

    @Override
    public SmartFilter.Compound requireParent() {
        return Objects.requireNonNull(parent);
    }

    @Override
    public Component getDisplayName() {
        if (displayName == null) {
            displayName = getDisplayName(this.getId());
        }
        return displayName;
    }

    public static MutableComponent getDisplayName(Identifier id) {
        return Component.translatable("filter." + id.toString().replace(':', '.') + ".name");
    }

    public static MutableComponent getTooltip(Identifier id) {
        return Component.translatable("filter." + id.toString().replace(':', '.') + ".tooltip");
    }
}
