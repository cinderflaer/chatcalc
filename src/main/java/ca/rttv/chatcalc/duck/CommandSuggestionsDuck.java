package ca.rttv.chatcalc.duck;

import com.mojang.brigadier.suggestion.Suggestions;

import java.util.concurrent.CompletableFuture;

public interface CommandSuggestionsDuck {
    CompletableFuture<Suggestions> chatcalc$pendingSuggestions();
}
