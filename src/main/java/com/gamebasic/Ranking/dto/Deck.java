package com.gamebasic.Ranking.dto;

import java.util.List;

public record Deck(
        Integer size,
        List<DeckCardSnapshot> cards
) {}