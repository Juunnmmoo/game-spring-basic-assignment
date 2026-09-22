# Crimson Citadel — Spring 게임 서버 과제

**Crimson Citadel**의 백엔드 API 서버입니다.

Spring Boot + JPA + MySQL로 게임 진행 상황을 저장하고, 외부 랭킹 API를 가공해 시즌 랭킹을 제공합니다.

- 📄 API 명세: [game-spring-api-docs](https://f-api.github.io/game-spring-api-docs/basic/api-docs.html)
- 📘 과제 가이드 전문: [`ASSIGNMENT.md`](ASSIGNMENT.md)

## API 엔드포인트

| Method | Path | 설명 |
| --- | --- | --- |
| `POST` | `/games` | 새 게임 생성 (시작 덱 포함) |
| `GET` | `/games` | 저장된 게임 목록 조회 (id 내림차순, 카드 수 포함) |
| `GET` | `/games/{gameId}` | 게임 상세 + 전체 덱 조회 |
| `PUT` | `/games/{gameId}/progress` | 진행 상황(HP/층/phase/status/덱) 저장 |
| `PATCH` | `/games/{gameId}` | 플레이어 이름 변경 |
| `DELETE` | `/games/{gameId}` | 게임 및 소속 카드 삭제 |
| `GET` | `/rankings` | 외부 시즌 기록을 필터링·정렬한 랭킹 조회 |

> 요청/응답 필드와 상태 코드는 위 API 명세 문서를 기준으로 합니다.
> 검증 실패(`400`), 게임 없음(`404`), 이미 종료된 게임에 진행 저장 시도(`409`)는 `GlobalExceptionHandler`가 명세 형식(`status`, `error`, `message`, `path`)의 에러 응답으로 처리합니다.
