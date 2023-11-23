package dev.ftb.mods.ftbfiltersystem.api.filter;

/**
 * Simple record used in dumping a filter hierarchy, storing indentation levels based on the filter's position in
 * the hierarchy. See also {@link dev.ftb.mods.ftbfiltersystem.api.FTBFilterSystemAPI.API#dump(SmartFilter)}.
 *
 * @param indent this filter's indentation level, which is its depth in the hierarchy of filters
 * @param filter this filter
 */
public record DumpedFilter(int indent, SmartFilter filter) {
}
