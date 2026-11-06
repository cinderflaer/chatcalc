package ca.rttv.chatcalc.mixin;

import ca.rttv.chatcalc.ChatCalc;
import ca.rttv.chatcalc.ChatHelper;
import ca.rttv.chatcalc.Config;
import ca.rttv.chatcalc.FunctionParameter;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.contents.TranslatableContents;
import com.mojang.datafixers.util.Pair;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.OptionalDouble;

@Mixin(EditBox.class)
abstract class EditBoxMixin extends AbstractWidget {
    public EditBoxMixin(int x, int y, int width, int height, Component message) {
        super(x, y, width, height, message);
    }

    @Shadow private Font font;
    @Shadow private int displayPos;
    @Shadow private int textX;
    @Shadow private int textY;

    @Shadow public native int getCursorPosition();

    @Shadow public native String getValue();

    @Unique
    @Nullable
    private Pair<String, OptionalDouble> evaluationCache;

    @Inject(method = "extractWidgetRenderState", at = @At("RETURN"))
    private void chatcalc$extractWidgetRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        String value = getValue();
        int start = Math.min(displayPos, value.length());
        int end = Math.max(start, Math.min(getCursorPosition(), value.length()));
        int cursorX = textX + font.width(value.substring(start, end));
        displayAbove(graphics, cursorX, textY);
    }

    @Unique
    private void displayAbove(GuiGraphicsExtractor context, int x, int y) {
        if (!(getMessage().getContents() instanceof TranslatableContents translatable && translatable.getKey().equals("chat.editBox"))) {
            return;
        }

        if (!Config.displayAbove()) {
            evaluationCache = null;
            return;
        }

        String word = ChatHelper.getSection(getValue(), getCursorPosition());

        if (ChatCalc.NUMBER.matcher(word).matches()) {
            evaluationCache = null;
            return;
        }

        try {
            double result;
            if (evaluationCache != null && evaluationCache.getFirst().equals(word)) {
                if (evaluationCache.getSecond().isEmpty()) {
                    return;
                }

                result = evaluationCache.getSecond().getAsDouble();
            } else {
                ChatCalc.CONSTANT_TABLE.clear();
                ChatCalc.FUNCTION_TABLE.clear();
                result = Config.makeEngine().eval(word, new FunctionParameter[0]);
                evaluationCache = new Pair<>(word, OptionalDouble.of(result));
            }
            Component text = Component.literal("=" + Config.getDecimalFormat().format(result));
            context.setTooltipForNextFrame(font, text, x - 8, y - 4);
        } catch (Throwable ignored) {
            evaluationCache = new Pair<>(word, OptionalDouble.empty());
        }
    }
}
