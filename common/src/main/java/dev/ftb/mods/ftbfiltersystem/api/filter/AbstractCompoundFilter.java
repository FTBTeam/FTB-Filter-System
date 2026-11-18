package dev.ftb.mods.ftbfiltersystem.api.filter;

import dev.ftb.mods.ftbfiltersystem.api.FTBFilterSystemAPI;
import dev.ftb.mods.ftbfiltersystem.api.FilterException;
import net.minecraft.ChatFormatting;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;

/**
 * Base class for all compound smart filters.
 */
public abstract class AbstractCompoundFilter extends AbstractSmartFilter implements SmartFilter.Compound {
    private final List<SmartFilter> children;

    protected AbstractCompoundFilter(SmartFilter.Compound parent, List<SmartFilter> children) {
        super(parent);

        if (children.size() > maxChildren()) {
            throw new FilterException("Too many children! " + children.size() + " > " + maxChildren());
        }
        this.children = children;
    }

    public AbstractCompoundFilter(SmartFilter.Compound parent) {
        super(parent);

        children = new ArrayList<>();
    }

    @Override
    public List<SmartFilter> getChildren() {
        return children;
    }

    @Override
    public String getStringArg(HolderLookup.Provider registryAccess) {
        return String.join("", children.stream().map(sf -> sf.asString(registryAccess)).toList());
    }

    @Override
    public boolean isConfigurable() {
        return false;
    }

    @Override
    public Component getDisplayName() {
        return super.getDisplayName().copy().withStyle(ChatFormatting.ITALIC).append(" ▶");
    }

    public static <F extends AbstractCompoundFilter> F createCompoundFilter(
            BiFunction<SmartFilter.Compound, List<SmartFilter>, F> creator,
            SmartFilter.Compound parent,
            String str,
            HolderLookup.Provider registryAccess)
    {
        F filter = creator.apply(parent, new ArrayList<>());
        List<SmartFilter> children = FTBFilterSystemAPI.api().parseFilterList(filter, str, registryAccess);
        if (children.size() > filter.maxChildren()) {
            throw new FilterException("Too many children for " + filter.getId() + " - expected " + filter.maxChildren() + ", got " + children.size());
        }
        filter.getChildren().addAll(children);
        return filter;
    }
}
