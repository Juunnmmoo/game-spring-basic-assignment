package com.gamebasic.Ranking.dto;

import java.util.List;

public record RankingResponse(
        String season,
        int totalRecords,
        int excludedCount,
        List<RankingEntry> entries
) {}