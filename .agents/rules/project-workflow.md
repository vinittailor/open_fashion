# Project Development Workflow

Follow the root `AGENTS.md` file and the nearest folder-specific `AGENTS.md` file.

This is both a learning project and a production-quality e-commerce project.

The user is learning:

- Node.js and Express.
- PostgreSQL and Prisma.
- Redis, Socket.io, Zod, and Docker.
- Kotlin and native Android development.
- Jetpack Compose Material 3.
- Clean Architecture and MVI.
- Retrofit, Room, Kotlin Coroutines, and Flow.
- Flutter and Dart.
- Full-stack application architecture.

## Learning-first explanations

Explain important code line by line or logical block by logical block.

For each implementation, explain as relevant:

- Syntax and keywords.
- Types and nullability.
- Parameters and return values.
- Interfaces, classes, and functions.
- Framework behavior.
- Data flow.
- State flow.
- Error propagation.
- Validation.
- Security concerns.
- Edge cases.
- Testing strategy.
- Architectural reasoning.
- Alternatives and trade-offs.
- Differences between a simple beginner implementation and the production-quality approach.

Do not skip explanations merely to reduce response length. Concise code is encouraged, but meaningful learning explanations are required.

## Development workflow

Always:

- Inspect before editing.
- Work on one file or one small, tightly related micro-task at a time.
- Show the exact target path before presenting code.
- Explain why the target file exists.
- Let the user write the code unless the user explicitly requests automatic editing.
- Do not modify unrelated files.
- Do not expose secrets or read secret values from `.env` files.
- Do not invent repository facts.
- Separate confirmed facts from proposed architecture.
- Prefer the smallest correct implementation.
- Avoid premature optimization and over-engineering.
- Reuse existing project patterns.
- Explain dependencies before introducing them.
- Never introduce duplicate libraries for the same responsibility without explaining the reason.
- Run the narrowest relevant verification command.
- Review changes through a diff.
- Report changed files and verification results.
- Update `ROADMAP_CHECKLIST.md` only after progress is confirmed.
- Record major architectural decisions in `DECISIONS.md`.
- Stop and wait for user confirmation after each micro-task.

## Standard response format

For every future implementation task, use this order:

1. Current objective.
2. Exact file path.
3. Why this file exists.
4. Prerequisites.
5. Implementation or code.
6. Detailed line-by-line or logical-block explanation.
7. Data flow and architectural explanation.
8. Edge cases and security concerns.
9. Verification steps.
10. Common mistakes.
11. What the user should report after completion.
12. Stop and wait for confirmation.

## Command safety

Before executing a command:

- Explain what it does.
- Explain which directory it runs in.
- Explain whether it changes files, databases, containers, or generated code.
- Request approval when the command has side effects.

Never execute destructive commands without explicit approval.

## Project boundaries

- `backend/` contains backend code and backend infrastructure.
- `mobile/` contains native Android and Kotlin code.
- `admin/` contains Flutter and Dart code.
- Do not place code from one application inside another application folder.
- Do not modify application source code during documentation-only tasks.

## Progress tracking

Use `ROADMAP_CHECKLIST.md` to track:

- Completed work.
- Current work.
- Remaining work.
- Blocked work.
- Learning progress.
- The next recommended micro-task.

Only mark work as completed when repository evidence or successful verification confirms it.
