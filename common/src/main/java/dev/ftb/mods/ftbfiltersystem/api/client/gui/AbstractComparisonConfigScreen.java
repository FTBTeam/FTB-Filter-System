package dev.ftb.mods.ftbfiltersystem.api.client.gui;

import com.mojang.blaze3d.platform.InputConstants;
import dev.ftb.mods.ftbfiltersystem.api.NumericComparison;
import dev.ftb.mods.ftbfiltersystem.api.filter.AbstractComparisonFilter;
import dev.ftb.mods.ftbfiltersystem.api.filter.SmartFilter;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Checkbox;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.client.gui.layouts.LayoutSettings;
import net.minecraft.client.gui.layouts.SpacerElement;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import org.apache.commons.lang3.math.NumberUtils;

import java.util.function.BiFunction;

/**
 * Base screen class for all comparison filters - see {@link AbstractComparisonFilter}. You can extend this class;
 * typical implementations are extremely simple, needing only a constructor which satisfies the
 * {@link dev.ftb.mods.ftbfiltersystem.api.client.FilterScreenFactory} interface contract.
 *
 * @param <T> the filter implementation type
 */
public abstract class AbstractComparisonConfigScreen<T extends AbstractComparisonFilter> extends AbstractFilterConfigScreen<T> {
    private final BiFunction<SmartFilter.Compound, NumericComparison, T> comparisonFactory;
    protected CycleButton<NumericComparison.ComparisonOp> opBtn;
    protected EditBox numBox;
    protected Checkbox pctCheckBox;

    public AbstractComparisonConfigScreen(T filter, AbstractFilterScreen parentScreen,
                                          BiFunction<SmartFilter.Compound, NumericComparison, T> comparisonFactory)
    {
        super(filter, parentScreen, 176, 55);
        this.comparisonFactory = comparisonFactory;
    }

    @Override
    protected void init() {
        super.init();

        GridLayout layout = new GridLayout(leftPos + 8, topPos + 20);
        GridLayout.RowHelper rowHelper = layout.createRowHelper(5);

        opBtn = rowHelper.addChild(CycleButton.builder(NumericComparison.ComparisonOp::getDisplayName, filter.getComparison().op())
                .withValues(NumericComparison.ComparisonOp.values())
                .displayOnlyValue()
                .create(0, 0, 20, font.lineHeight + 8,
                        Component.empty(), (btn, val) -> {}));

        rowHelper.addChild(SpacerElement.width(10));
        rowHelper.addChild(Button.builder(Component.literal("-"), b -> adjustVal(-1)).size(12, 12).build(),
                LayoutSettings.defaults().alignVerticallyMiddle().paddingRight(2));
        numBox = rowHelper.addChild(new EditBox(font, 0, 0, 30, font.lineHeight + 8, Component.empty()));
        rowHelper.addChild(Button.builder(Component.literal("+"), b -> adjustVal(1)).size(12, 12).build(),
                LayoutSettings.defaults().alignVerticallyMiddle().paddingLeft(2));

        numBox.setValue(Integer.toString(filter.getComparison().value()));
        numBox.setResponder(str -> adjustVal(0));
        numBox.setFilter(str -> isValidNumber(str) || str.isEmpty());

        if (filter.allowsPercentage()) {
            rowHelper.addChild(SpacerElement.height(5), 5);
            MutableComponent txt = Component.translatable("ftbfiltersystem.gui.percentage").withoutShadow();
            pctCheckBox = rowHelper.addChild(Checkbox.builder(txt, font).maxWidth(font.width(txt)).selected(filter.getComparison().percentage()).build(), 5);
        }

        layout.arrangeElements();
        layout.visitWidgets(this::addRenderableWidget);
    }

    @Override
    public boolean keyPressed(KeyEvent keyEvent) {
        if ((keyEvent.key() == InputConstants.KEY_RETURN || keyEvent.key() == InputConstants.KEY_NUMPADENTER) && numBox.canConsumeInput()) {
            applyChanges();
        }
        return super.keyPressed(keyEvent);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double dirX, double dirY) {
        adjustVal(dirY < 0 ? -1 : 1);

        return true;
    }

    private boolean isValidNumber(String str) {
        if (NumberUtils.isDigits(str)) {
            if (isPercent()) {
                int v = Integer.parseInt(str);
                return v >= 0 && v <= 100;
            } else {
                return true;
            }
        }
        return false;
    }

    private void adjustVal(int amount) {
        try {
            String s = numBox.getValue();
            int value = (s.isEmpty() ? 0 : Integer.parseInt(s)) + amount;
            numBox.setResponder(str -> {});
            numBox.setValue(String.valueOf(value));
            numBox.setResponder(str -> adjustVal(0));
        } catch (NumberFormatException ignored) {
        }
    }

    @Override
    protected T makeNewFilter() {
        try {
            int value = Integer.parseInt(numBox.getValue());
            NumericComparison comparison = new NumericComparison(opBtn.getValue(), value, isPercent());
            return comparisonFactory.apply(filter.getParent(), comparison);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    protected boolean isPercent() {
        return pctCheckBox != null && pctCheckBox.selected();
    }
}
