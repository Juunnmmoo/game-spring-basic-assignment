package com.gamebasic.runcard.dto;

import lombok.Getter;

@Getter
public class DeckCount {
    private final Long gameId;
    private final Long cardCount;

    public DeckCount(Long gameId, Long cardCount)
    {
        this.gameId = gameId;
        this.cardCount = cardCount;
    }
}
