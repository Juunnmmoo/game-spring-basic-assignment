package com.gamebasic.Ranking.dto;

import java.time.Instant;

public record ClientInfo(
        String version,
        String platform,
        String locale
) {}
