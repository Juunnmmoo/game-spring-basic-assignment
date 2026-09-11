package com.gamebasic.Ranking.dto;

import java.time.Instant;

public record Season(
        String id, String name, Instant startsAt, Instant endsAt
) {}
