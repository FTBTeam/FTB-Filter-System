package dev.ftb.mods.ftbfiltersystem.util;

import java.util.regex.Pattern;

public class RegExParser {

    public static Pattern parseRegex (String string) {
        if (string.indexOf('*') >= 0) {
            StringBuilder builder = new StringBuilder();
            builder.append('^');
            for (int i = 0; i < string.length(); i++) {
                char c = string.charAt(i);
                if (c == '*') {
                    builder.append(".*");
                } else if ("\\.[]{}()+-^$|".indexOf(c) >= 0) {
                    builder.append('\\').append(c);
                } else {
                    builder.append(c);
                }
            }
            builder.append('$');
            return Pattern.compile(builder.toString());
        } else {
            return null;
        }
    }

}
