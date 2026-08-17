# Architecture Design: AtomGdx Studio

Architectural blueprint for **AtomGdx Studio**, a modular NetBeans Rich Client Platform (RCP) application dedicated to LibGDX multi-platform game development.

---

## 1. System Overview

AtomGdx Studio is designed as an extensible, modular architecture consisting of interconnected NetBeans Platform OSGi modules and standard Java/Kotlin libraries.

```
+-----------------------------------------------------------------------+
|                         AtomGdx Studio UI Layer                       |
|  [Sci-Fi Branding] [FlatLaf Dark Theme] [Docking TopComponent System] |
+------------------------------------+----------------------------------+
|      Game Design Tool Suite        |     Code Intelligence & AI       |
|  - 2D/3D Particle Editors          |  - Polyglot Editors (Java/Kt/GLSL)|
|  - Scene2D / VisUI Skin Composer   |  - AI Copilot Dockable Panel     |
|  - 2D Level / Scene Designer       |  - Model Context Protocol (MCP)  |
|  - Texture Packer & 9-Patch Tool   |  - Asset & Code Synthesis Engine |
|  - Hiero & FreeType Font Studio    |                                  |
|  - 3D Model & PBR Shader Viewer    |                                  |
|  - Media / Audio Visualizer        |                                  |
+------------------------------------+----------------------------------+
|                    Project Engine & Target Backends                   |
|  - Liftoff Project Generation Wizard                                  |
|  - Gradle Tooling API Build & Task Runner                             |
|  - Android Target (ADB / Emulator / Logcat)                           |
|  - Web Target (TeaVM / GWT DevServer)                                 |
|  - Desktop Target (LWJGL3 / Profiler)                                 |
+-----------------------------------------------------------------------+
|                    NetBeans Platform 21+ Core Layer                   |
|  [Lookup API] [Nodes API] [Explorer] [Filesystems] [Window System]    |
+-----------------------------------------------------------------------+
```

---

## 2. Module Decomposition

### 2.1 Core Infrastructure
- `atomgdx-branding`: Application branding token (`atomgdx`), splash screen, window titles, About box, sci-fi welcome screen.
- `atomgdx-core`: Game project ontology, asset registry, project lifecycle events, workspace settings.
- `atomgdx-ui-theme`: FlatLaf Dark custom palette, cyber obsidian background (`#0D1117`), cyan neon highlights (`#00F0FF`), custom tab headers and status bar.

### 2.2 Project Management & Build
- `atomgdx-project-liftoff`: Embedded Liftoff template generation engine, project archetypes, dependency catalog.
- `atomgdx-gradle-integration`: Gradle Tooling API bridge, background task scheduling, build log highlighters.
- `atomgdx-platform-android`: Android SDK discovery, AVD emulator controller, ADB device selection, package installer, Logcat console.
- `atomgdx-platform-web`: TeaVM and GWT build targets with embedded Jetty/Undertow HTTP static server for in-browser live testing.

### 2.3 Integrated LibGDX Tool Suite
- `atomgdx-editor-particle2d`: NetBeans `TopComponent` integrating LibGDX 2D Particle Editor, curve editor, emitter management.
- `atomgdx-editor-particle3d`: NetBeans `TopComponent` integrating LibGDX 3D Flame particle editor.
- `atomgdx-editor-ui-skin`: Visual Skin Composer for LibGDX Scene2D and VisUI widgets with live skin JSON preview.
- `atomgdx-editor-scene2d`: 2D Level & Scene designer with layer hierarchy, tilemaps, Box2D physics polygon tracing, and Box2DLight placement.
- `atomgdx-editor-texturepacker`: Graphical interface for `TexturePacker` with batch packing, 9-patch slicing, and atlas inspector.
- `atomgdx-editor-font`: Hiero & FreeType font generator with SDF / MSDF distance-field generation.
- `atomgdx-viewer-3d`: 3D model viewer (`.g3db`, `.g3dj`, `.gltf`, `.glb`) with PBR lighting, animation track scrubber, and orbit camera.
- `atomgdx-viewer-media`: Audio visualizer for `.wav`, `.mp3`, `.ogg` with waveform display, loop settings, pitch tests, and sprite inspector.

### 2.4 Language Intelligence
- `atomgdx-languages`: Multi-language editor support for Java, Kotlin, Groovy, JavaScript, HTML, JSON, YAML, XML.
- `atomgdx-glsl-shader`: GLSL syntax highlighter, error parser, and live compile checker for vertex (`.vert`) and fragment (`.frag`) shaders.

### 2.5 AI Assistant & MCP
- `atomgdx-ai-assistant`: AI Chat panel supporting Gemini, Anthropic Claude, OpenAI, and Ollama local models.
- `atomgdx-mcp-server`: Model Context Protocol server exposing project AST, scene hierarchy, texture atlas metadata, and shader source code to AI agents.
- `atomgdx-genart-tools`: In-IDE prompt-to-texture generation and automatic atlas integration.

---

## 3. Lookups and Extension Mechanisms

All modules interact loosely coupled via the NetBeans `Lookup` API:
```java
// Example: Querying active LibGDX project from NetBeans GlobalContext Lookup
Lookup.Result<LibGdxProject> result = Utilities.actionsGlobalContext().lookupResult(LibGdxProject.class);
```
Editors and viewers register their file associations via standard NetBeans `@MIMEResolver` and `@DataObject.Registration` annotations.
