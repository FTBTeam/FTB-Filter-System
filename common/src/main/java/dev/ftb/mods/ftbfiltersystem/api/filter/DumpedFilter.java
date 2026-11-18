package dev.ftb.mods.ftbfiltersystem.api.filter;

import net.minecraft.ChatFormatting;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

/**
 * Simple record used in dumping a filter hierarchy, storing indentation levels based on the filter's position in
 * the hierarchy. See also {@link dev.ftb.mods.ftbfiltersystem.api.FTBFilterSystemAPI.API#dump(SmartFilter)}.
 *
 * @param indent this filter's indentation level, which is its depth in the hierarchy of filters
 * @param filter this filter
 */
public record DumpedFilter(int indent, SmartFilter filter) {
    public Component display(HolderLookup.Provider lookup) {
        MutableComponent txt = filter().getDisplayName().copy().withStyle(ChatFormatting.AQUA);
        if (!(filter() instanceof SmartFilter.Compound)) {
            txt.append(" ").append(filter().getDisplayArg(lookup).copy().withStyle(ChatFormatting.YELLOW));
        }
        return Component.literal(makePrefix(indent())).withStyle(ChatFormatting.YELLOW).append(txt);
    }

    private static String makePrefix(int indent) {
        if (indent == 0) return "";
        String s1 = "┃ ".repeat(indent - 1);
        return s1 + "┣━";
    }
}
