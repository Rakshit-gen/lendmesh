# LendMesh

A peer-to-peer micro-lending marketplace, simulated end to end, no real money moves anywhere in this system.

Borrowers list loan requests. A transparent, explainable scorecard grades each one (A through G) from a simulated financial profile. Lenders browse the marketplace and fund loans in fractional notes, the way real P2P platforms like LendingClub or Prosper split a single loan across many investors. Once a listing is fully funded, an amortization schedule kicks in and a simulation clock advances time, generating repayments and, for the riskier grades, defaults, so lenders can watch a portfolio behave the way a real one would, compressed into minutes instead of years.

## Why this exists

Most portfolio/lending demos either move fake numbers around with no underlying model, or bury the credit decision in a black box. LendMesh does neither: the risk score is a weighted scorecard you can inspect line by line, and the default simulation is driven by the same grade the scorecard produced, so the numbers a lender sees are the direct consequence of a visible model, not a random number generator dressed up as one.

## Stack

- **Backend**: Java 21, Spring Boot 3, Spring Data JPA, Spring Security (JWT), Flyway, WebSocket/STOMP for live funding updates, H2 (dev) / PostgreSQL (prod)
- **Frontend**: Next.js (App Router), Material UI, TanStack Query, framer-motion, a hand-rolled canvas background (no particle library pulled in just for that)

## Project layout

```
lendmesh/
  backend/     Spring Boot API, simulation engine, WebSocket feed
  frontend/    Next.js + MUI client
```

See `backend/README.md` and `frontend/README.md` for how to run each half.
