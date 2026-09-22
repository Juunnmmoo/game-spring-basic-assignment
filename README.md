# Crimson Citadel — Spring 게임 서버 과제

로그라이크 덱 배틀 게임 **Crimson Citadel**의 백엔드 API 서버입니다. Spring Boot + JPA + MySQL로 게임 진행을 저장하고, 외부 랭킹 API를 가공해 시즌 랭킹을 제공합니다.

- 완성된 정적 게임 화면: https://nhahan.github.io/crimson-citadel/
- API 명세: https://f-api.github.io/game-spring-api-docs/basic/api-docs.html
- 과제 가이드 전문: [`ASSIGNMENT.md`](ASSIGNMENT.md)

## 기술 스택

| 구분 | 내용 |
| --- | --- |
| 언어 / 빌드 | Java 21 (toolchain), Gradle |
| 프레임워크 | Spring Boot 4.1.0 (`spring-boot-starter-webmvc`, `spring-boot-starter-data-jpa`, `spring-boot-starter-validation`) |
| DB | MySQL (`com.mysql:mysql-connector-j`), 테스트는 H2 인메모리 |
| 기타 | Lombok, JUnit 5 (`spring-boot-starter-webmvc-test`) |

`tasks.named('jar') { enabled = false }` 설정으로 실행 가능한 jar는 만들지 않고 `bootJar`만 사용합니다.

## 실행 방법

1. Docker 등으로 MySQL을 띄웁니다.
2. `src/main/resources/application.properties`에서 datasource 정보를 확인/수정합니다. (`spring.datasource.url`, `username`, `password`)
3. 프로젝트 루트에서 서버를 실행합니다.

   ```bash
   ./gradlew bootRun
   ```

4. 브라우저에서 http://localhost:8080/ 접속 — 완성된 프론트엔드(`src/main/resources/static`)가 함께 서빙되어 바로 플레이할 수 있습니다.

테스트는 `./gradlew test`로 실행하며, 테스트용 DB 설정은 `src/test/resources/application-test.properties`(H2)를 사용합니다.

## 프로젝트 구조

```
src/main/java/com/gamebasic
├── GameBasicApplication.java     # @SpringBootApplication, @EnableJpaAuditing
├── common
│   ├── dto/ErrorResponse.java            # 공통 에러 응답 형식
│   └── exception/                        # GameNotFoundException, GameFinishedException, GlobalExceptionHandler
├── game
│   ├── controller/GameController.java    # /games, /games/{id}, /rankings 엔드포인트
│   ├── dto/                              # 요청/응답 DTO (Create, Progress, Rename, Summary, Detail)
│   ├── entity/                           # Game, GamePhase, GameStatus, CardType, BaseEntity(생성/수정 시각)
│   ├── repository/GameRepository.java
│   └── service/GameService.java          # 게임 생성·진행 저장·조회·이름변경·삭제
├── runcard
│   ├── dto/                              # CardResponse, RunCardRequest, DeckCount(집계 프로젝션)
│   ├── entity/RunCard.java               # Game에 단방향 다대일로 연결되는 카드 한 장
│   └── repository/RunCardRepository.java
└── Ranking
    ├── client/RankingClient.java, RestClientConfig.java   # 외부 랭킹 API 호출(RestClient)
    ├── dto/                              # 외부 API 응답 매핑용 DTO 전체
    └── Service/RankingService.java       # 랭킹 필터링·정렬·집계 로직
```

프론트엔드 정적 자원은 `src/main/resources/static`에 함께 포함되어 있습니다(빌드된 SPA + 이미지/오디오 에셋).

## 도메인 모델

- **Game**: 플레이어 이름, 현재 HP/층, `phase`(`BATTLE`/`REWARD`/`FINISHED`), `status`(`PLAYING`/`CLEARED`/`FAILED`)를 갖는 한 판의 여정. `BaseEntity`를 상속해 생성/수정 시각을 자동 기록합니다.
- **RunCard**: 한 게임에 속한 카드 한 장(`cardType`, `acquiredFloor`). `Game`과 단방향 다대일 관계이며 자식 엔티티의 조회·삭제는 `RunCardRepository`를 통해 명시적으로 처리합니다(양방향/cascade 미사용).
- **CardType**: 카드 38종을 정의하는 enum.

## API 엔드포인트

| Method | Path | 설명 |
| --- | --- | --- |
| POST | `/games` | 새 게임 생성 (시작 덱 포함) |
| GET | `/games` | 저장된 게임 목록 조회 (id 내림차순, 카드 수 포함) |
| GET | `/games/{gameId}` | 게임 상세 + 전체 덱 조회 |
| PUT | `/games/{gameId}/progress` | 진행 상황(HP/층/phase/status/덱) 저장 |
| PATCH | `/games/{gameId}` | 플레이어 이름 변경 |
| DELETE | `/games/{gameId}` | 게임 및 소속 카드 삭제 |
| GET | `/rankings` | 외부 시즌 기록을 필터링·정렬한 랭킹 조회 |

요청/응답 필드와 상태 코드는 위 API 명세 문서를 기준으로 합니다. 검증 실패(400)·게임 없음(404)·이미 끝난 게임에 진행 저장 시도(409)는 `GlobalExceptionHandler`가 명세 형식(`status`, `error`, `message`, `path`)의 에러 응답으로 처리합니다.

## 랭킹 로직 (`RankingService`)

1. `https://f-api.github.io/game-spring-api-docs/basic/rankings.json`에서 시즌 기록 전체를 가져옵니다.
2. `run.status == CLEARED`이고 `clearedFloor == 10`인 기록만 순위 대상으로 삼습니다.
3. 클리어 시간, 남은 HP, 덱 크기, 카드 타입, 획득 층, 보스 페이즈 구성, 마무리 카드 조건을 모두 만족하는 기록만 정상 기록으로 인정하고, 제외된 수를 `excludedCount`로 응답합니다.
4. 클리어 시간 오름차순 → 남은 HP 내림차순 → id 오름차순으로 정렬한 뒤, 플레이어(`player.id`)당 최고 기록 하나만 남겨 순위를 매깁니다.

## 참고

- `src/main/resources/application.properties`에는 로컬 개발용 MySQL 접속 정보가 포함되어 있습니다. 다른 환경에 배포하거나 공유 저장소에 올릴 때는 실제 비밀번호를 환경 변수 등으로 분리하는 것을 권장합니다.
- `docs/images`에는 과제 단계별(Lv2~Lv12) 진행 전/후 화면 캡처가 있어 `ASSIGNMENT.md`와 함께 보면 각 단계의 기대 동작을 확인할 수 있습니다.
- 테스트 스켈레톤은 `src/test/java/com/gamebasic/game/GameApiTests.java`에 있습니다.
