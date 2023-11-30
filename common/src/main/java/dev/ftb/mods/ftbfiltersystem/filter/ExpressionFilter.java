package dev.ftb.mods.ftbfiltersystem.filter;

import dev.ftb.mods.ftbfiltersystem.api.FTBFilterSystemAPI;
import dev.ftb.mods.ftbfiltersystem.api.filter.AbstractSmartFilter;
import dev.ftb.mods.ftbfiltersystem.api.filter.SmartFilter;
import dev.ftb.mods.ftbfiltersystem.util.FilterParser;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public class ExpressionFilter extends AbstractSmartFilter {
    public static final ResourceLocation ID = FTBFilterSystemAPI.rl("expression");

    private final String expression;
    private final String customName;
    private final SmartFilter parsedExpression;

    public ExpressionFilter(SmartFilter.Compound parent) {
        this(parent, "");
    }

    public ExpressionFilter(SmartFilter.Compound parent, String expression) {
        super(parent);

        String[] parts = expression.split("/", 2);
        this.expression = parts[0];
        this.customName = parts.length > 1 ? parts[1] : "";

        this.parsedExpression = FilterParser.parse(this.expression);
    }

    public String getExpression() {
        return expression;
    }

    public String getCustomName() {
        return customName;
    }

    @Override
    public boolean test(ItemStack stack) {
        return parsedExpression.test(stack);
    }

    @Override
    public String getStringArg() {
        return customName.isEmpty() ? expression : expression + "/" + customName;
    }

    @Override
    public Component getDisplayArg() {
        return customName.isEmpty() ? super.getDisplayArg() : Component.literal(customName);
    }

    public static ExpressionFilter fromString(SmartFilter.Compound parent, String str) {
        return new ExpressionFilter(parent, str);
    }

    @Override
    public ResourceLocation getId() {
        return ID;
    }
}
