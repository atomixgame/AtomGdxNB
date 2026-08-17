# TODO: AtomGdx Studio

Living roadmap and task checklist for AtomGdx Studio development.

---

## High Priority (v0.1.0 Release)

### Branding & Theme
- [x] Sci-Fi branding concept and splash screen generation (`AtomGdx Studio v0.1.0`).
- [x] High-resolution application icons (16x16 up to 512x512).
- [ ] Implement custom FlatLaf Sci-Fi Dark theme with neon cyan accent highlights.
- [ ] Configure NetBeans Platform branding tokens (`atomgdxnb`, window title, About dialog).

### Architecture & Build System
- [x] Define i2c documentation layout and guidelines compliance.
- [ ] Configure multi-module Gradle build suite (`build.gradle.kts` / `settings.gradle.kts`).
- [ ] Configure NetBeans Platform Harness and dependency clusters.

### LibGDX Tool Integration Suite
- [ ] **Liftoff Module**: Port Liftoff template engine into a NetBeans `WizardDescriptor` project wizard.
- [ ] **Particle2D Module**: Port LibGDX 2D Particle Editor into a dockable NetBeans `TopComponent`.
- [ ] **Particle3D Module**: Port LibGDX 3D Flame Particle Editor into a `TopComponent`.
- [ ] **Skin Composer Module**: Visual Scene2D / VisUI Skin Designer with JSON AST synchronization.
- [ ] **Scene2D / Level Designer**: Implement 2D scene editor with layers, Box2D physics polygon tool, and Box2DLight visualizer.
- [ ] **Texture Packer Module**: Integrated `TexturePacker` GUI with 9-Patch editor.
- [ ] **Font Generator Module**: Hiero & FreeType distance-field (SDF/MSDF) font converter.
- [ ] **3D Viewer Module**: GLTF/GLB/G3D model viewer with PBR shader preview and animation timeline.
- [ ] **Media Viewer Module**: Audio visualizer (WAV, MP3, OGG) and texture inspector.

### Polyglot Languages & Shaders
- [ ] Register MIME resolvers and syntax highlighters for GLSL (`.vert`, `.frag`, `.glsl`, `.geom`).
- [ ] Enable language support for Java, Kotlin, Groovy, JS, HTML, JSON, YAML, XML.

### AI Assistant & MCP
- [ ] Implement AI Chat TopComponent with API key configuration (Gemini, Claude, OpenAI, Ollama).
- [ ] Implement MCP Server endpoint exposing project AST, scene hierarchy, and assets to MCP clients.
- [ ] Implement generative art texture creation pipeline.

### Deployment & Emulation
- [ ] Implement Android AVD emulator runner and ADB logcat streaming panel.
- [ ] Implement Web TeaVM/GWT local dev server launcher.

---

## Medium Priority (v0.2.0 Roadmap)
- [ ] Visual Shader Graph Editor (Node-based GLSL generator).
- [ ] Visual Behavior Tree & State Machine Editor for `gdx-ai`.
- [ ] Spine & DragonBones skeletal 2D animation playback component.
- [ ] Multiplayer networking inspector and packet visualizer.
- [ ] Profiling overlay with GPU frame-time graph and memory leak detector.

---

## Low Priority / Backlog
- [ ] Steamworks & Google Play Game Services configuration wizard.
- [ ] WebAssembly / WebGPU backend target support.
