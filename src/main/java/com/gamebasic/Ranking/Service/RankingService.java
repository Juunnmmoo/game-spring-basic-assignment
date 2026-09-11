package com.gamebasic.Ranking.Service;

import com.gamebasic.Ranking.client.RankingClient;
import com.gamebasic.Ranking.dto.*;
import com.gamebasic.game.entity.CardType; // 실제 CardType 위치에 맞게 조정
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RankingService {

    private static final Set<String> VALID_CARD_TYPES = Arrays.stream(CardType.values())
            .map(Enum::name)
            .collect(Collectors.toSet());

    private static final List<String> EXPECTED_PHASES = List.of("THRONE", "UNBOUND", "ECLIPSE");

    private final RankingClient rankingClient;

    public RankingResponse getRankings() {
        RankingSource source = rankingClient.fetch();

        List<RankingRecord> eligible = source.records().stream()
                .filter(this::isEligible)
                .toList();

        List<RankingRecord> valid = eligible.stream()
                .filter(this::isValid)
                .toList();

        int excludedCount = eligible.size() - valid.size();

        List<RankingRecord> sorted = valid.stream()
                .sorted(Comparator
                        .comparing((RankingRecord r) -> r.run().durationSeconds())
                        .thenComparing((RankingRecord r) -> r.run().finalHp(), Comparator.reverseOrder())
                        .thenComparing(RankingRecord::id))
                .toList();

        List<RankingRecord> distinctByPlayer = new ArrayList<>();
        Set<String> seenPlayerIds = new HashSet<>();
        for (RankingRecord record : sorted) {
            if (seenPlayerIds.add(record.player().id())) {
                distinctByPlayer.add(record);
            }
        }

        List<RankingEntry> rankings = new ArrayList<>();
        for (int i = 0; i < distinctByPlayer.size(); i++) {
            RankingRecord record = distinctByPlayer.get(i);
            rankings.add(toEntry(record, i + 1));
        }

        return new RankingResponse(
                source.meta().season().id(),
                source.records().size(),   // 원본 records 전체 개수
                excludedCount,
                rankings
        );
    }

    private boolean isEligible(RankingRecord record) {
        RunInfo run = record.run();
        return run != null
                && "CLEARED".equals(run.status())
                && run.clearedFloor() != null
                && run.clearedFloor() == 10;
    }

    private boolean isValid(RankingRecord record) {
        RunInfo run = record.run();
        Deck deck = record.deck();
        BossFight bossFight = record.bossFight();

        if (run == null || deck == null || bossFight == null || record.player() == null) {
            return false;
        }

        // 클리어 시간
        if (run.durationSeconds() == null || run.durationSeconds() < run.clearedFloor() * 30L) {
            return false;
        }

        // 남은 HP
        if (run.finalHp() == null || run.finalHp() < 1 || run.finalHp() > 99) {
            return false;
        }

        // 덱 크기
        List<DeckCardSnapshot> cards = deck.cards();
        if (cards == null) return false;
        int cardCount = cards.size();
        if (cardCount < 9 || cardCount > 20) return false;
        if (deck.size() == null || deck.size() != cardCount) return false;

        // 카드 타입 / 획득 층
        for (DeckCardSnapshot card : cards) {
            if (card.cardType() == null || !VALID_CARD_TYPES.contains(card.cardType())) return false;
            if (card.acquiredFloor() == null || card.acquiredFloor() < 0 || card.acquiredFloor() > 9) return false;
        }

        // 보스 페이즈
        List<BossPhase> phases = bossFight.phases();
        if (phases == null || phases.size() != 3) return false;

        int sumTurns = 0;
        for (int i = 0; i < 3; i++) {
            BossPhase phase = phases.get(i);
            if (phase.phase() == null || !phase.phase().equals(EXPECTED_PHASES.get(i))) return false;
            if (phase.turns() == null || phase.turns() < 1) return false;
            sumTurns += phase.turns();
        }
        if (bossFight.totalTurns() == null || bossFight.totalTurns() != sumTurns) return false;

        // 마무리 카드
        if (bossFight.finishingCard() == null) return false;
        boolean finishingCardInDeck = cards.stream()
                .anyMatch(c -> bossFight.finishingCard().equals(c.cardType()));
        return finishingCardInDeck;
    }

    private RankingEntry toEntry(RankingRecord record, int rank) {
        return new RankingEntry(
                rank,
                record.player().name(),
                record.run().durationSeconds().intValue(),
                record.run().finalHp(),
                record.bossFight().totalTurns(),
                record.deck().cards().size()
        );
    }
}