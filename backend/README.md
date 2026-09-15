# LendMesh API

Spring Boot 3 / Java 21 backend for the LendMesh simulation.

## Run it

```bash
./mvnw spring-boot:run
```

Boots against an in-memory H2 database by default — nothing to install. Point `DB_URL`, `DB_USER`, `DB_PASSWORD` at a real Postgres instance (see `../docker-compose.yml`) to run against a persistent database instead.

The API listens on `:8080`. `GET /actuator/health` is unauthenticated for a quick check that it's up.

## Test it

```bash
./mvnw test
```

## How the simulation clock works

Every `lendmesh.simulation.tick-interval-ms` (15s by default, override with `SIM_TICK_MS`), every `ACTIVE` loan advances by one repayment period: the borrower's risk grade decides the odds of a default that period, and the outcome either pays every lender their pro-rata share or charges the whole loan off. See `SimulationClockService` for the mechanics.

## Key packages

- `domain` — JPA entities
- `service` — business logic (scoring, amortization, marketplace, wallet, portfolio)
- `service.simulation` — the repayment clock and default-probability engine
- `web` — REST controllers
- `security` — JWT issuing/parsing and the auth filter
- `config` — Spring Security and WebSocket wiring
