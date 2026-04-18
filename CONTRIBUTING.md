# Contributing to uni-link

Thanks for your interest in this project. This guide explains how to work on the codebase and what CI expects.

## Before you start

- For **substantial changes** (new features, refactors, API shifts), open an **issue** first so direction and scope can be agreed.
- Read [`docs/architecture.md`](docs/architecture.md) for DDD, modular boundaries, and layering. Prefer **aggregate encapsulation**: state changes go through the aggregate root, not direct mutation of child entities from outside.

## Development setup

### Backend

- **JDK 25** (Temurin matches CI).
- **PostgreSQL** and **Redis** when running the app locally (see the root [`README.md`](README.md) for env vars and run commands).

From the repo root:

```bash
cd backend
./gradlew check    # tests + Checkstyle (Google style)
./gradlew build    # full build
```

### Frontend

- **Node.js 24** and **pnpm 9** (see [`.github/workflows/ci.yml`](.github/workflows/ci.yml)).

```bash
cd frontend
pnpm install --frozen-lockfile
pnpm run type-check
pnpm run build-only
```

## Pull requests

1. **Branch** from `main` with a short, descriptive name (e.g. `fix/auth-refresh-token`, `feat/thread-bookmarks`).
2. **Keep changes focused** — one concern per PR when possible.
3. **Match existing style**: naming, packaging, and patterns in nearby code. Backend code is checked with **Checkstyle**; run `./gradlew check` before pushing.
4. **Tests**: add or update tests when behavior changes. Integration tests may need local services or testcontainers as already used in the project.
5. **Description**: explain *what* changed and *why*; link related issues if any.

CI runs on pushes and PRs to `main`:

- Changes under `backend/**` or `.github/**` → `./gradlew check` in `backend/`.
- Changes under `frontend/**` or `.github/**` → `pnpm run type-check` and `pnpm run build-only` in `frontend/`.

If CI fails, check uploaded **test** and **checkstyle** artifacts on the workflow run for details.

## License

Contributions are accepted under the same terms as the project: the [MIT License](LICENSE). By opening a pull request, you agree your contribution will be licensed under MIT as well.
