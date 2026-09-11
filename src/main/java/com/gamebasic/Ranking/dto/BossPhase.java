package com.gamebasic.Ranking.dto;

import java.util.List;

public record BossPhase(
        String phase,
        Integer turns,
        Integer damageTaken
) {}