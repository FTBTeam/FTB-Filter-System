package dev.ftb.mods.ftbfiltersystem.util;

import dev.ftb.mods.ftbfiltersystem.api.FTBFilterSystemAPI;
import dev.ftb.mods.ftbfiltersystem.api.FilterException;
import dev.ftb.mods.ftbfiltersystem.api.filter.SmartFilter;
import dev.ftb.mods.ftbfiltersystem.filter.compound.RootFilter;
import dev.ftb.mods.ftbfiltersystem.registry.FilterRegistry;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;

public class FilterParser {
    public static SmartFilter parse(String str) throws FilterException {
        RootFilter root = new RootFilter();
        root.getChildren().addAll(parseFilterList(root, str));
        return root;
    }

    public static List<SmartFilter> parseFilterList(SmartFilter.Compound parent, String str) throws FilterException {
        List<SmartFilter> res = new ArrayList<>();

        str = str.trim();
        while (!str.isEmpty()) {
            int start = str.indexOf('(');
            if (start <= 0) {
                throw new FilterException("missing open parenthesis: " + str);
            }

            int matchingParen = findMatchingParenIdx(str, start);

            // string is of form "TYPE(ARG)" (where ARG could itself contain multiple nested parens!)
            // e.g. "and(item(A) or (item(B) item(C)) item(D))"
            String type = str.substring(0, start).trim();
            String arg = unescape(str.substring(start + 1, matchingParen).trim());

            if (!ResourceLocation.isValidResourceLocation(type)) {
                throw new FilterException("invalid filter ID: " + type);
            }

            res.add(FilterRegistry.INSTANCE.getDetails(FTBFilterSystemAPI.modDefaultedRL(type))
                    .map(FilterRegistry.FilterDetails::factory)
                    .orElseThrow(() -> new FilterException("unknown filter ID: " + type))
                    .create(parent, arg));

            str = str.substring(matchingParen + 1);
        }

        return res;
    }

    private static String unescape(String str) {
        StringBuilder res = new StringBuilder();
        for (int i = 0; i < str.length(); i++) {
            if (str.charAt(i) == '\\') {
                if (++i >= str.length()) break;
            }
            res.append(str.charAt(i));
        }
        return res.toString();
    }

    private static int findMatchingParenIdx(String str, int checkPos) throws FilterException {
        int depth = 0;
        while (checkPos < str.length()) {
            if (str.charAt(checkPos) == '\\') {  // allows escaped parens
                checkPos++;
            } else if (str.charAt(checkPos) == '(') {
                depth++;
            } else if (str.charAt(checkPos) == ')') {
                if (--depth == 0) {
                    return checkPos;
                }
            }
            checkPos++;
        }
        throw new FilterException("missing close parenthesis: " + str);
    }
}
