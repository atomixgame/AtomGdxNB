# AtomGdx Studio

> **Next-Gen LibGDX Integrated Game Development Environment (IDE) built on NetBeans Platform**

[![Version](https://img.shields.io/badge/version-0.1.0-cyan.svg)](docs/plan/v0.1.0_Milestone_Plan.md)
[![Platform](https://img.shields.io/badge/platform-NetBeans%20RCP%20%7C%20Java%2021-blue.svg)](docs/howto/Dev_Setup.md)
[![Ecosystem](https://img.shields.io/badge/i2c-ecosystem-purple.svg)](https://github.com/i2ccom/i2c-docs)

---

## Overview

**AtomGdx Studio** is a unified, futuristic game development IDE for the **LibGDX** multi-platform framework. It replaces the fragmented ecosystem of standalone desktop utilities by consolidating **Liftoff Project Generator, 2D/3D Particle Editors, Skin Composer, Texture Packer, 2D Scene Editor (HyperLap2D), Hiero Font Generator, 3D Model Viewer, and Shader Editors** into a cohesive NetBeans Platform application equipped with an embedded **AI Assistant & Model Context Protocol (MCP)** copilot.

---

## Core Capabilities

- **Unified LibGDX Tool Suite**:
  - **Liftoff Generator**: Full-featured project wizard supporting Desktop (LWJGL3), Android, Web (TeaVM/GWT), iOS (RoboVM) with Ashley, Box2D, FreeType, gdx-ai, and gdx-gltf.
  - **2D/3D Particle Designers**: Real-time particle curve and emitter editors with instant live preview.
  - **Scene2D & VisUI Skin Composer**: Visual skin creator and widget styler with live UI rendering.
  - **2D Scene & Level Designer**: Multi-layer tilemap, composite entities, Box2D collider outlines, and Box2DLight placement.
  - **Texture Packer & 9-Patch Editor**: In-IDE spritesheet generation and interactive 9-patch slice configuration.
  - **Hiero & FreeType Font Generator**: SDF / MSDF distance-field font generation and bitmap export.
  - **3D Model & Shader Viewer**: PBR model viewer (`.g3db`, `.g3dj`, `.gltf`, `.glb`) with interactive GLSL editor.
- **Polyglot Language Intelligence**: Syntax highlighting, code folding, and templates for Java, Kotlin, Groovy, JavaScript, HTML, JSON, YAML, XML, and GLSL (`.vert`, `.frag`, `.glsl`).
- **AI Assistant & MCP Studio**:
  - Multi-provider AI assistant (Gemini, Anthropic Claude, OpenAI, Ollama/Local LLM) for game logic, math helper, and procedural asset generation.
  - **Model Context Protocol (MCP) Server**: Exposes scene graphs, texture atlases, asset pipelines, and project AST to AI agents.
- **Multi-Platform Deployment**:
  - One-click build & launch to Desktop (LWJGL3), Web DevServer (TeaVM/GWT), and Android Emulator with ADB logcat stream.

---

## Documentation Layout

Following the [i2c Platform Development Guideline](file:///G:/i2c/PROJECTS/i2c_Docs/guideline/i2c_dev-guideline.md):

```
docs/
├── plan/       # Milestones and feature roadmap (e.g. v0.1.0_Milestone_Plan.md)
├── todo/       # Living task checklists and roadmap items (TODO.md)
├── howto/      # Developer setup and build instructions (Dev_Setup.md, Build_And_Run.md)
├── design/     # Architectural specs, LibGDX tool integration, AI/MCP design
├── progress/   # Development logs and completion tracking (v0.1.md)
├── reports/    # Verification reports and framework comparison analysis
└── tooling/    # Build scripts, Gradle plugins, and IDE configuration
```

---

## Quick Start

### Prerequisites
- **JDK**: Java 21 LTS (or higher)
- **Gradle**: 8.x+ (or Gradle Wrapper)
- **Android SDK** (optional for Android target)

### Building the IDE
```bash
# Build the entire platform suite
./gradlew build

# Launch AtomGdx Studio
./gradlew run
```

---

## License & i2c Ecosystem Linkage

Part of the **i2c platform ecosystem**. Refer to [i2c-docs](https://github.com/i2ccom/i2c-docs) for cross-platform integration guidelines.
