package dev.ftb.mods.ftbfiltersystem.filter;

import dev.ftb.mods.ftbfiltersystem.api.FTBFilterSystemAPI;
import dev.ftb.mods.ftbfiltersystem.api.filter.AbstractSmartFilter;
import dev.ftb.mods.ftbfiltersystem.api.filter.SmartFilter;
import net.minecraft.core.HolderLookup;
import java.util.regex.Pattern;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public class ModFilter extends AbstractSmartFilter {
    public static final ResourceLocation ID = FTBFilterSystemAPI.rl("mod");

    private final String modId;
    private final String patternArg;
    private final Pattern patternRegex;

    public ModFilter(SmartFilter.Compound parent) {
        this(parent, "minecraft");
    }

    public ModFilter(SmartFilter.Compound parent, String modId) {
        super(parent);
        this.modId = modId;
        this.patternArg = null;
        this.patternRegex = null;
    }

    private ModFilter(SmartFilter.Compound parent, String patternArg, Pattern patternRegex) {
        super(parent);

        this.modId = "";
        this.patternArg = patternArg;
        this.patternRegex = patternRegex;
    }

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    @Override
    public boolean test(ItemStack stack) {
        String ns = stack.getItem().arch$registryName().getNamespace();
        if (patternRegex != null) {
            return patternRegex.matcher(ns).matches();
        }
        return ns.equals(modId);
    }

    @Override
    public String getStringArg(HolderLookup.Provider registryAccess) {
        return patternArg == null ? modId : patternArg;
    }

    public static ModFilter fromString(SmartFilter.Compound parent, String str, HolderLookup.Provider ignored2) {
        if (str.indexOf('*') >= 0) {
            StringBuilder sb = new StringBuilder();
            sb.append('^');
            for (int i = 0; i < str.length(); i++) {
                char c = str.charAt(i);
                if (c == '*') {
                    sb.append(".*");
                } else if (".\\+?^${}()|[]".indexOf(c) >= 0) {
                    sb.append('\\').append(c);
                } else {
                    sb.append(c);
                }
            }
            sb.append('$');
            Pattern p = Pattern.compile(sb.toString());
            return new ModFilter(parent, str, p);
        }

        return new ModFilter(parent, str);
    }
}
