# uni-link

[![GitHub](https://img.shields.io/badge/github-ninggiangboy/uni-link-181717?style=flat-square&logo=github)](https://github.com/ninggiangboy/uni-link)
[![Stars](https://img.shields.io/github/stars/ninggiangboy/uni-link?style=flat-square)](https://github.com/ninggiangboy/uni-link/stargazers)
[![Issues](https://img.shields.io/github/issues/ninggiangboy/uni-link?style=flat-square)](https://github.com/ninggiangboy/uni-link/issues)
[![Last commit](https://img.shields.io/github/last-commit/ninggiangboy/uni-link?style=flat-square)](https://github.com/ninggiangboy/uni-link/commits/main)
[![Commits (last month)](https://img.shields.io/github/commit-activity/m/ninggiangboy/uni-link?style=flat-square)](https://github.com/ninggiangboy/uni-link/graphs/commit-activity)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow?style=flat-square)](LICENSE)

A social/thread-style platform backend built with **Java 25** and **Spring Boot 4**. The system follows domain-driven design and is organized as a Gradle multi-module project with bounded contexts for identity, profiles, threads, chat, hashtags, and notifications.

## Tech Stack

| Layer | Technologies |
|---|---|
| Language | Java 25 (preview features) |
| Framework | Spring Boot 4.0.0, Spring Cloud 2025.1.1 |
| Database | PostgreSQL, Spring Data JDBC, Liquibase |
| Cache | Redis, Caffeine |
| Messaging | Kafka |
| Security | Spring Security, OAuth2, JWT (RSA) |
| API | REST, GraphQL, SpringDoc OpenAPI |
| Observability | Actuator, OpenTelemetry, Sentry |
| Quality | Checkstyle (Google style), JUnit 5, Mockito, AssertJ |

## Project Structure

```
backend/
├── apps/
│   └── app                  # Main Spring Boot application
├── core/
│   ├── domain               # Domain models (framework-independent)
│   └── database             # JDBC persistence layer
├── libs/
│   ├── common               # Shared base classes and interfaces
│   ├── utils                # Utility helpers
│   ├── constant             # Shared constants
│   └── infrastructure       # Web, security, caching, messaging infra
├── platform/
│   ├── config               # Spring Cloud Config Server
│   ├── gateway              # API Gateway (planned)
│   └── discovery            # Service Discovery (planned)
└── services/
    ├── notification          # Notification service (planned)
    ├── realtime              # Real-time/WebSocket service (planned)
    ├── worker                # Background workers (planned)
    ├── scheduler             # Scheduled jobs (planned)
    └── media                 # Media/upload service (planned)
```

### Domain Contexts

- **Identity** — accounts, credentials, sessions, devices, OTP, login history
- **Profile** — user profiles, follows, blocks, mutes
- **Thread** — threads, likes, reposts, bookmarks, polls, moderation
- **Chat** — conversations, messages, participants
- **Hashtag** — hashtags, relations, usage analytics
- **Notification** — notifications, settings, counters

## Getting Started

### Prerequisites

- Java 25+
- PostgreSQL
- Redis

### Configuration

The application uses Spring Cloud Config. Key environment variables:

| Variable | Description | Default |
|---|---|---|
| `DB_URL` | PostgreSQL JDBC URL | — |
| `DB_USERNAME` | Database username | — |
| `DB_PASSWORD` | Database password | — |
| `REDIS_HOST` | Redis host | `localhost` |
| `REDIS_PORT` | Redis port | `6379` |
| `CONFIG_SERVER_URI` | Config Server URL | — |
| `APP_PORT` | Application port | `8080` |

### Build & Run

```bash
cd backend

# Build all modules
./gradlew build

# Run the Config Server
./gradlew :platform:config:bootRun

# Run the main application
./gradlew :apps:app:bootRun

# Run tests
./gradlew check
```

## License

Licensed under the [MIT License](LICENSE).

Copyright (c) 2026 Ha Duy Khanh
