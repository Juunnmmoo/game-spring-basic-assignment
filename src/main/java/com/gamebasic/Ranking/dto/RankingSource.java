package com.gamebasic.Ranking.dto;

import java.util.List;

public record RankingSource(
        Meta meta,
        List<RankingRecord> records
){}