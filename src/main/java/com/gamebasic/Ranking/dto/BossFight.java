package com.gamebasic.Ranking.dto;

import java.time.Instant;

import java.util.List;

public record BossFight(
        List<BossPhase> phases,
        String finishingCard,
        Integer totalTurns
) {}
