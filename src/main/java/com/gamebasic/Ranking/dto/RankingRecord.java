package com.gamebasic.Ranking.dto;

import java.time.Instant;

public record RankingRecord(
        Long id,
        Instant submittedAt,
        ClientInfo client,
        PlayerInfo player,
        RunInfo run,
        BossFight bossFight,   // 10층 미클리어면 null
        Deck deck
) {}
