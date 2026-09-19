# Open Fashion — Flutter Admin Dashboard Guidelines & Learning Rules

## 1. Domain & Responsibilities
The `admin/` directory contains the cross-platform administrative portal for the Open Fashion ecosystem. It is engineered with Flutter and Dart to run responsively across Desktop Web browsers and Mobile devices from a single unified codebase.

**Strict Boundary Rule**: Never place Node.js backend code or Android Kotlin code inside this directory.

---

## 2. Technology Stack & Multi-Platform Target
- **Framework**: Flutter (Latest Stable)
- **Language**: Dart
- **Compilation Targets**: Web (Primary Dashboard) and Android/iOS Mobile (On-the-go Administration)
- **State Management**: `flutter_riverpod` (v2.x) with `Notifier` and `AsyncNotifier` patterns (`[Confirmed]`)
- **Networking**: `dio` / `http` with JWT Bearer authentication (`[Planned]`)
- **Data Visualization**: `fl_chart` for revenue and inventory metrics (`[Planned]`)

---

## 3. Responsive Web & Mobile Architecture

To support both large desktop monitors and narrow mobile viewports, all screens must adhere to an adaptive layout strategy:

```
                          ┌──────────────────────────┐
                          │   Screen Viewport Size   │
                          └─────────────┬────────────┘
                                        │
                    ┌───────────────────┴───────────────────┐
                    │                                       │
            [Width >= 1024px]                        [Width < 1024px]
             (Desktop / Web)                             (Mobile)
                    │                                       │
                    ▼                                       ▼
       • Collapsible NavigationRail           • Bottom NavigationBar / Drawer
       • Multi-column Data Tables             • Card-based List Tiles
       • Side-by-side Detail Panes            • Slide-over Modal BottomSheets
       • Fixed Action Toolbars                • Floating Action Buttons
```

### Breakpoint Standards:
- **Mobile**: `< 600px`
- **Tablet**: `600px - 1023px`
- **Desktop Web**: `>= 1024px`

---

## 4. Flutter & Dart Learning & Engineering Rules

### 4.1. Dart Language Standards
- Strong typing: Always annotate return types, parameters, and generic collections.
- Null Safety: Use non-nullable types by default, using `?` only when data is genuinely optional.
- Immutable Models: Use `@immutable` data models or code generation with `freezed` for predictable state updates.

### 4.2. Widget Construction & Tree Lifecycle
- Prefer `StatelessWidget` with external reactive state controllers over complex internal `StatefulWidget` mutation.
- Use `const` constructors wherever possible to avoid redundant widget rebuilds during state changes.
- Modularize large build methods into small, focused sub-widgets instead of single monolithic `build()` trees.

### 4.3. UI States (The 4 Fundamental States)
Every admin view must explicitly handle all 4 states:
1. **Initial / Idle**: Unloaded state before user interaction.
2. **Loading**: Centered spinner or skeleton loader.
3. **Success / Content**: Rendered responsive data table or form.
4. **Error**: User-friendly error message with a "Retry" button.
5. **Empty**: Specialized empty-state illustration when data lists are empty.

### 4.4. Forms & Authoritative Admin Input
- Always use `Form` widgets with `GlobalKey<FormState>` and dedicated `TextFormField` validators.
- Ensure form submission is debounced and displays inline field validation errors received from backend Zod responses.

---

## 5. Standard Flutter Commands
*(To be activated upon Flutter project creation in `admin/`)*
- Create Flutter project: `flutter create --platforms=web,android,ios .`
- Run Web development: `flutter run -d chrome`
- Run Mobile development: `flutter run -d <device_id>`
- Run Unit & Widget Tests: `flutter test`
- Build Web Release: `flutter build web`
