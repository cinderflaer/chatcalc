package ca.rttv.chatcalc.mixin;

import ca.rttv.chatcalc.ChatCalc;
import ca.rttv.chatcalc.duck.CommandSuggestionsDuck;
import com.mojang.brigadier.suggestion.Suggestions;
import net.minecraft.client.gui.components.CommandSuggestions;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.client.input.KeyEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.concurrent.CompletableFuture;

@Mixin(ChatScreen.class)
abstract class ChatScreenMixin {
    @Shadow
    protected EditBox input;

    @Shadow
    private CommandSuggestions commandSuggestions;

    @Inject(at = @At("HEAD"), method = "keyPressed", cancellable = true)
    private void keyPressed(KeyEvent event, CallbackInfoReturnable<Boolean> cir) {
        CompletableFuture<Suggestions> suggestions = ((CommandSuggestionsDuck) this.commandSuggestions).chatcalc$pendingSuggestions();
        if ((suggestions != null && suggestions.isDone() && !suggestions.isCompletedExceptionally() && suggestions.getNow(null).isEmpty())) {
            if (event.key() == 258 && ChatCalc.tryParse(input)) {
                cir.setReturnValue(true);
            }
        }
    }
}
