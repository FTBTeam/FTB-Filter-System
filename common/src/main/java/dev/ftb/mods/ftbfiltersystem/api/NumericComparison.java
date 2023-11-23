package dev.ftb.mods.ftbfiltersystem.api;

import net.minecraft.Util;
import net.minecraft.network.chat.Component;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.IntPredicate;

public class NumericComparison implements IntPredicate {
    private final ComparisonOp op;
    private final int value;
    private final boolean percentage;

    public NumericComparison(ComparisonOp op, int value, boolean percentage) {
        this.op = op;
        this.value = value;
        this.percentage = percentage;
    }

    public static NumericComparison fromString(String str, boolean allowPercentages) throws FilterException {
        boolean pct = false;
        if (str.endsWith("%")) {
            if (!allowPercentages) {
                throw new FilterException("Percentage used but not permitted in: '" + str + "'");
            }
            pct = true;
            str = str.substring(0, str.length() - 1);
        }

        int numStart = -1;
        for (int i = 0; i < str.length(); i++) {
            if (Character.isDigit(str.charAt(i))) {
                numStart = i;
                break;
            }
        }

        if (numStart < 0) {
            throw new FilterException("Missing number in: '" + str + "'");
        }

        String numStr = str.substring(numStart);
        String opStr = str.substring(0, numStart);
        try {
            int val = Integer.parseInt(numStr);
            ComparisonOp op = ComparisonOp.byString(opStr)
                    .orElseThrow(() -> new FilterException("Invalid comparison op: '" + opStr + "'"));
            return new NumericComparison(op, val, pct);
        } catch (NumberFormatException e) {
            throw new FilterException("Invalid number: '" + numStr + "'");
        }
    }

    @Override
    public String toString() {
        return op.toString() + value + (percentage ? "%" : "");
    }

    @Override
    public boolean test(int toCheck) {
        return op.test(toCheck, value);
    }

    public boolean isPercentage() {
        return percentage;
    }

    public int getValue() {
        return value;
    }

    public ComparisonOp getOp() {
        return op;
    }

    public enum ComparisonOp {
        EQ("=", (v1, v2) -> v1 == v2),
        NE("!=", (v1, v2) -> v1 != v2),
        GT(">", (v1, v2) -> v1 > v2),
        LT("<", (v1, v2) -> v1 < v2),
        LE("<=", (v1, v2) -> v1 <= v2),
        GE(">=", (v1, v2) -> v1 >= v2);

        private final String str;
        private final ValuePredicate predicate;

        private static final Map<String,ComparisonOp> map = Util.make(new HashMap<>(),
                map -> Arrays.stream(values()).forEach(op -> map.put(op.str, op))
        );

        ComparisonOp(String str, ValuePredicate predicate) {
            this.str = str;
            this.predicate = predicate;
        }

        public static Optional<ComparisonOp> byString(String str) {
            return Optional.ofNullable(map.get(str));
        }

        public boolean test(int val1, int val2) {
            return predicate.compare(val1, val2);
        }

        @Override
        public String toString() {
            return str;
        }

        public Component getDisplayName() {
            return Component.literal(str);
        }
    }

    interface ValuePredicate {
        boolean compare(int v1, int v2);
    }
}
