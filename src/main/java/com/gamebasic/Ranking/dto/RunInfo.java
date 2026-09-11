package com.gamebasic.Ranking.dto;

import java.util.List;

public record RunInfo(
        String seed,
        String status,            // "CLEARED" / "FAILED" / 그 외 이상값
        Integer clearedFloor,
        Long durationSeconds,
        Integer finalHp,
        List<FloorLog> floors
) {}
