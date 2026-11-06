package ca.rttv.chatcalc.mixin;

import ca.rttv.chatcalc.duck.CommandSuggestionsDuck;
import com.mojang.brigadier.suggestion.Suggestions;
import net.minecraft.client.gui.components.CommandSuggestions;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.concurrent.CompletableFuture;

@Mixin(CommandSuggestions.class)
abstract class CommandSuggestionsMixin implements CommandSuggestionsDuck {
    @Shadow @Nullable private CompletableFuture<Suggestions> pendingSuggestions;

    @Override
    public CompletableFuture<Suggestions> chatcalc$pendingSuggestions() {
        return this.pendingSuggestions;
    }
}
