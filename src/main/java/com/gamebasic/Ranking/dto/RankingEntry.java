package com.gamebasic.Ranking.dto;

import java.util.List;

public record RankingEntry(
        int rank,
        String playerName,
        int clearTimeSeconds,
        int remainingHp,
        int bossTurns,
        int deckSize
) {}