package com.gamebasic.Ranking.dto;

import java.util.List;

public record Reward(
        List<String> offered,
        String picked
) {}  // 안 골랐으면 picked=null