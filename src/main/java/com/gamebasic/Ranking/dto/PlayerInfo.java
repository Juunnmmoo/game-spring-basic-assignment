package com.gamebasic.Ranking.dto;

import java.util.List;

public record PlayerInfo(
        String id,
        String name,
        String region,
        List<String> tags
) {}
