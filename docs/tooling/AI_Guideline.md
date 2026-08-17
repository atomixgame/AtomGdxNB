# AI Developer Closed-Loop Verification Guideline

**Standard Operating Procedure for AI-Assisted Development, Automated Testing, and Visual Proof Capture**

This document establishes the mandatory closed-loop engineering process across the **AtomGdx Studio** and **i2c platform ecosystem**. Every AI agent or automated developer must execute work in strict iterative verification cycles.

---

## 1. The Closed-Loop Verification Lifecycle

```
+-------------------------------------------------------------------------+
|                       1. Specification & Design                        |
|  - Define requirements, data models, file associations, and UI layout   |
+------------------------------------+------------------------------------+
                                     |
+------------------------------------v------------------------------------+
|                         2. Modular Development                         |
|  - Write clean Java 21 / NetBeans Platform / Gradle code                |
|  - Maintain separation of concerns and loose coupling via Lookups      |
+------------------------------------+------------------------------------+
                                     |
+------------------------------------v------------------------------------+
|                       3. Automated Unit Testing                         |
|  - Run JUnit 5 & AssertJ suites for data models, parsers, and engines   |
|  - Verify exit codes and capture execution logs                         |
+------------------------------------+------------------------------------+
                                     |
+------------------------------------v------------------------------------+
|                   4. Visual Verification & Proof Capture                |
|  - Render GUI components, dialogs, and editors to image buffers         |
|  - Save verified screenshots into docs/assets/ and artifacts directory  |
|  - Embed screenshots directly in reports and walkthrough documentation  |
+------------------------------------+------------------------------------+
                                     |
+------------------------------------v------------------------------------+
|                   5. Milestone Completion & Progress Log                |
|  - Record deliverables in docs/progress/v<major>.<minor>.md             |
|  - Create milestone report in docs/reports/report-v<major>.<minor>.md   |
+-------------------------------------------------------------------------+
```

---

## 2. Visual Proof Requirements

Code or UI features are not considered verified without visual proof:
1. **Component Rendering**:
   - Swing TopComponents, Dialogs, and Viewers must be rendered headless or in memory to a `BufferedImage` or captured via screenshot utilities.
2. **Artifact Persistence**:
   - Screenshots must be saved with descriptive names in `docs/assets/` and the conversation artifact directory (`<appDataDir>/brain/<conversation-id>/`).
3. **Walkthrough Integration**:
   - Every completed milestone must embed visual proof using the `![Caption](path)` syntax in `walkthrough.md` and `docs/reports/`.

---

## 3. Quality Gate Rules for AI Agents

1. **Never Report Done Without Testing**: Every new parser, model, or engine must have a corresponding test.
2. **Verify File Formats**: File associations (MIME types, icons, multi-view editors) must be verified against actual sample files.
3. **Demo Project Integrity**: Generated LibGDX sample projects must contain all required asset directories (`assets/`, `core/`, `lwjgl3/`) and compile-ready code.
4. **Follow i2c Naming & Layout**: All documentation in `docs/` must use Title Case naming and proper cross-linking.
