package com.gamebasic.Ranking.dto;

import java.time.Instant;

public record Meta(
        Season season,
        Instant generatedAt,
        String schemaVersion,
        Integer totalRecords
){}
