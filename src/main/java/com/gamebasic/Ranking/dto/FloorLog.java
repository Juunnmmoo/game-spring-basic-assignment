package com.gamebasic.Ranking.dto;

import java.util.List;

public record FloorLog(
        Integer floor,
        String enemy,
        Integer turns,
        Integer hpAfter,
        List<Reward> rewards   // 배열로 수정, 보상 없는 층은 null
) {}