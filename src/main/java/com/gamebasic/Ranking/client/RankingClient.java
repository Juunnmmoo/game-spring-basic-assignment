package com.gamebasic.Ranking.client;

import com.gamebasic.Ranking.dto.RankingSource;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
@RequiredArgsConstructor
public class RankingClient {

    private static final String SOURCE_URL = "https://f-api.github.io/game-spring-api-docs/basic/rankings.json";

    private final RestClient restClient;

    public RankingSource fetch()
    {
        return restClient.get()
                .uri(SOURCE_URL)
                .retrieve()
                .body(RankingSource.class);
    }
}
