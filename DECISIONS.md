# Open Fashion — Architecture Decision Records (ADR)

## Purpose of Recording Decisions
This log records significant architectural and engineering decisions made during the lifecycle of the **Open Fashion** ecosystem. 

### ⚠️ Mandatory Protocol Rule:
Before introducing any major architectural shift, new core dependency, alternative state management library, or protocol change, an ADR entry must be drafted, reviewed, and finalized in this document.

---

## ADR Template

```markdown
### ADR-[000]: [Decision Title]
- **Date**: YYYY-MM-DD
- **Status**: [ PROPOSED | ACCEPTED | REJECTED | DEPRECATED | SUPERSEDED ]
- **Deciders**: Principal Mentor & Lead Developer

#### 1. Context & Problem Statement
[What problem are we trying to solve? What are the constraints and requirements?]

#### 2. Decision
[What is the change/pattern/library we are committing to adopt?]

#### 3. Alternatives Considered
- **Option A**: [Pros and Cons]
- **Option B**: [Pros and Cons]

#### 4. Reasons & Trade-offs
[Why did we select this decision over the alternatives? What trade-offs are we accepting?]

#### 5. Consequences
- **Positive Impacts**: [Benefits gained]
- **Negative / Neutral Impacts**: [Maintenance overhead, learning curves]

#### 6. Learning Notes
[What core computer science or software engineering principles does this decision teach us?]
```

---

## Confirmed Decision Records

### ADR-001: 3-Tier Multi-Client Repository Structure
- **Date**: 2026-09-18
- **Status**: ACCEPTED
- **Deciders**: Principal Mentor & Lead Developer

#### 1. Context & Problem Statement
We need an enterprise e-commerce portfolio application that demonstrates mastery across modern backend systems, native mobile platforms, and cross-platform administration dashboards without coupling them into an unmaintainable monolith.

#### 2. Decision
Organize the root repository into 3 distinct workspaces:
1. `backend/`: Node.js (ESM), Express 5, PostgreSQL (Prisma 7), Redis, Socket.io, Zod, Docker.
2. `mobile/`: Native Android with Kotlin, Jetpack Compose Material 3, Clean Architecture, and MVI.
3. `admin/`: Flutter cross-platform (Responsive Web & Mobile) for admin dashboard operations.

#### 3. Alternatives Considered
- **Single Monolithic Web App (e.g. Next.js full-stack)**: Faster to build initially, but misses the opportunity to master deep native Android development and dedicated backend service architecture.
- **Microservices Architecture**: Excessive operational overhead and premature complexity for a single-developer learning curriculum.

#### 4. Reasons & Trade-offs
- Provides clean isolation of concerns and independent dependency trees.
- Mirrors how high-scale tech companies separate backend platforms from specialized consumer client apps and internal operations tools.

#### 5. Consequences
- **Positive Impacts**: Clear separation of concerns, independent build tools (Gradle, npm, Flutter), direct alignment with modern full-stack mobile developer job roles.
- **Negative / Neutral Impacts**: Requires context switching between Kotlin, JavaScript/Node.js, and Dart.

#### 6. Learning Notes
Teaches client-server contract synchronization, independent subsystem lifecycles, and cross-platform architectural design patterns.

---

### ADR-002: Native Android Clean Architecture & MVI Pattern
- **Date**: 2026-09-18
- **Status**: ACCEPTED
- **Deciders**: Principal Mentor & Lead Developer

#### 1. Context & Problem Statement
The customer-facing mobile app requires high visual polish, predictable state transitions, offline resilience, and enterprise-grade testability.

#### 2. Decision
Adopt **Clean Architecture** (Presentation, Domain, Data) coupled with **MVI (Model-View-Intent)** using Jetpack Compose and Kotlin Coroutines/Flow.

#### 3. Alternatives Considered
- **Standard MVVM with multiple LiveData streams**: Can lead to state divergence and race conditions in complex UI screens.
- **MVC/God Activity**: Anti-pattern with zero separation of concerns and poor testability.

#### 4. Reasons & Trade-offs
- MVI guarantees a single source of truth for the UI through an immutable `UiState`.
- Clean Architecture ensures the domain layer is 100% pure Kotlin with zero Android SDK dependencies, making business rules fully testable with fast JVM unit tests.

#### 5. Consequences
- **Positive Impacts**: Highly predictable reactive UI, simple state debugging, seamless offline caching integration.
- **Negative / Neutral Impacts**: Slightly more boilerplate (Intent, State, Effect classes per screen).

#### 6. Learning Notes
Teaches unidirectional data flow (UDF), immutable state reduction, and enterprise Android design patterns.

---

### ADR-003: Flutter Responsive Web & Mobile for Admin Dashboard
- **Date**: 2026-09-18
- **Status**: ACCEPTED
- **Deciders**: Principal Mentor & Lead Developer

#### 1. Context & Problem Statement
The admin dashboard needs to be accessible from both desktop web browsers in office settings and mobile screens on the go.

#### 2. Decision
Build the `admin/` application in Flutter using adaptive and responsive layouts (`LayoutBuilder`, breakpoints, `NavigationRail` for Web and `NavigationBar` for Mobile).

#### 3. Alternatives Considered
- **React/Vue Web Admin**: Good for web, but requires a separate app for mobile admin usage.
- **Native Android Admin App**: Limits desktop browser access for store managers.

#### 4. Reasons & Trade-offs
- A single Flutter codebase compiles to both high-performance Web and Mobile binaries.
- Offers rich data visualization (`fl_chart`), rapid UI prototyping, and robust component architecture.

#### 5. Consequences
- **Positive Impacts**: 100% code reuse between web and mobile admin platforms.
- **Negative / Neutral Impacts**: Web bundle size and initial load time must be monitored.

#### 6. Learning Notes
Teaches responsive UI engineering, cross-platform compilation targets, and Flutter widget lifecycles.

---

### ADR-004: State Management for Admin Dashboard (Flutter Riverpod)
- **Date**: 2026-09-18
- **Status**: ACCEPTED
- **Deciders**: Principal Mentor & Lead Developer

#### 1. Context & Problem Statement
The admin dashboard requires robust asynchronous data fetching, automatic caching, filter parameterization, and state synchronization across responsive web and mobile components without tightly coupling to `BuildContext`.

#### 2. Decision
Adopt **`flutter_riverpod`** (v2.x) with `Notifier` / `AsyncNotifier` patterns for state management and dependency injection across the `admin/` application.

#### 3. Alternatives Considered
- **`flutter_bloc` / Cubit**: Excellent MVI pattern, but already familiar to the developer; Riverpod expands full-stack mastery and offers superior declarative data caching (`AsyncValue`) for web data tables.
- **`Provider`**: Predecessor to Riverpod with runtime exceptions and `BuildContext` limitations.

#### 4. Reasons & Trade-offs
- Compile-time safety with zero `ProviderNotFoundException` risks.
- Built-in `AsyncValue` eliminates manual loading/error boolean boilerplate.
- Effortless cache invalidation via `ref.invalidate()` on data writes.

#### 5. Consequences
- **Positive Impacts**: Rapid async state handling, cleaner table querying, no `BuildContext` requirement for business logic.
- **Negative / Neutral Impacts**: Requires mastering Riverpod-specific concepts (`ref.watch`, `ref.listen`, `autoDispose`, `family`).

#### 6. Learning Notes
Teaches declarative reactive caching, dependency injection without service locators, and compile-safe state lifecycles.

---

### ADR-005: Workspace Governance & Local Antigravity Skills Integration
- **Date**: 2026-09-18
- **Status**: ACCEPTED
- **Deciders**: Principal Mentor & Lead Developer

#### 1. Context & Problem Statement
To ensure pedagogical clarity and prevent uncontrolled multi-file code dumps, the development environment requires strict governance rules, security boundaries, and local skill configurations.

#### 2. Decision
Adopt a dedicated workspace rule file at `.agents/rules/project-workflow.md` enforcing:
1. Micro-step pacing (one file at a time).
2. Deep line-by-line learning explanations.
3. Zero external telemetry transmission.
4. Activation of 12 verified local Antigravity skills.

#### 3. Alternatives Considered
- **Unstructured Prompting**: Leads to context loss and inconsistent coding pacing across chat sessions.
- **Autonomous Coding Agents**: Modifies multiple files without review, which breaks the learning process and introduces untested changes.

#### 4. Reasons & Trade-offs
- Guarantees consistent senior-level mentoring throughout the entire project lifecycle.
- Keeps all code, logs, and development tools completely local and private.

#### 5. Consequences
- **Positive Impacts**: High code quality, complete understanding of every line written, version-controlled governance.
- **Negative / Neutral Impacts**: Requires explicit confirmation after each micro-task (intentional pacing).

#### 6. Learning Notes
Teaches team engineering governance, pair-programming workflows, and documentation-driven development.

---

### ADR-006: Centralized API Endpoints, UI App Strings, and Strongly-Typed DTOs
- **Date**: 2026-10-03
- **Status**: ACCEPTED
- **Deciders**: Principal Mentor & Lead Developer

#### 1. Context & Problem Statement
Hardcoded endpoint URI strings (e.g. `/api/v1/auth/login`) and UI text strings scattered across repository implementations, service classes, and composables lead to typos, difficult refactoring, lack of single-source-of-truth, and high friction when supporting internationalization (i18n) or environment URL switching. Additionally, parsing API responses as unstructured raw maps (e.g., `Map<String, dynamic>`) prevents compile-time safety and IDE autocompletion.

#### 2. Decision
1. **Centralized Endpoints**:
   - Flutter Admin: [`admin/lib/core/constants/api_endpoints.dart`](file:///c:/Vicky/open_fashion/admin/lib/core/constants/api_endpoints.dart)
   - Android Mobile: [`mobile/app/src/main/java/com/example/open_fashion/core/constants/ApiEndpoints.kt`](file:///c:/Vicky/open_fashion/mobile/app/src/main/java/com/example/open_fashion/core/constants/ApiEndpoints.kt)
2. **Centralized UI Strings**:
   - Flutter Admin: [`admin/lib/core/constants/app_strings.dart`](file:///c:/Vicky/open_fashion/admin/lib/core/constants/app_strings.dart)
   - Android Mobile: [`mobile/app/src/main/java/com/example/open_fashion/core/constants/AppStrings.kt`](file:///c:/Vicky/open_fashion/mobile/app/src/main/java/com/example/open_fashion/core/constants/AppStrings.kt)
3. **Strongly-Typed Data Models**:
   - Replaced raw JSON map return types with structured models (e.g., `AuthActionModel` in Flutter and `ActionResponseDto` in Android) across all remote data sources and repositories.

#### 3. Alternatives Considered
- **In-file string literals**: Faster for initial prototyping but creates technical debt and breaks when backend paths evolve.
- **Raw dynamic maps (`Map<String, dynamic>` / `JsonObject`)**: High risk of runtime key misspelling (`json['dev_otp']` vs `json['devOtp']`).

#### 4. Reasons & Trade-offs
- Guarantees compile-time validation for every network call and response field.
- Provides immediate auto-completion across all IDE editors.
- Paves a direct upgrade path for future localization (l10n / i18n).

#### 5. Consequences
- **Positive Impacts**: Refactoring an API path or UI copy takes a single-line edit; compile-time safety across Flutter and Kotlin.
- **Negative / Neutral Impacts**: Requires creating constant definitions before building new features.

#### 6. Learning Notes
Teaches strict Clean Architecture separation of constants, compile-time type safety over dynamic typing, and scalable internationalization readiness.
