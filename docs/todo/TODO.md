# AtomGdx Studio - Task Tracker & Living TODO

## Active Milestone: v0.2.0 (In Progress)

### High Priority v0.2.0 Tasks
- [ ] **Feature/Concept: Device, Resolution & ScreenMode Preview**:
  - [ ] Add Preview Canvas dropdown to select device presets (iPhone 15, Pixel 8, iPad Pro, 1080p FHD, Steam Deck, Custom).
  - [ ] Adapt layout immediately on resolution change with aspect ratio letterboxing and safe-area notch overlay.
  - [ ] Orientation switcher (Landscape / Portrait).
- [ ] **Build & Build Configurations**:
  - [ ] Connect targets (Desktop LWJGL3, Android APK/AAB, Web TeaVM/GWT, iOS RoboVM) to Gradle build parameters and flavor profiles.
- [ ] **Timeline & Animations Studio**:
  - [ ] Create `TimelineTopComponent` with dope-sheet track view.
  - [ ] Playback control toolbar (Play, Pause, Step, Loop, Scrubber) for 2D spritesheet frames and 3D glTF skeletal animations.
- [ ] **PBR Material & Environment Lighting Studio**:
  - [ ] Create `MaterialEditorTopComponent` with live shader linkage.
  - [ ] Create `LightingEnvironmentTopComponent` with HDRI skybox IBL reflections, ambient light, and sun shadow controls.
- [ ] **Ashley ECS Component Registry & Prefabs**:
  - [ ] Create `ComponentRegistryTopComponent` to inspect available components via code introspection.
  - [ ] Prefab asset workflow and scene instantiation.
- [ ] **3D Interactive Gizmos**:
  - [ ] Interactive 3D Translate (arrows), Rotate (rings), and Scale (boxes) gizmos directly on the OpenGL canvas.

---

## Completed Milestone: v0.1.90 (Core Complete)

- [x] **Branding & Assets**: SVG Logo, window icons (16–512px), dark theme tokens, splash screen.
- [x] **2D Scene & Level Designer**: HyperLap2D integration, fullscreen `LwjglAWTCanvas`, layers, composite items, point lights, soft shadows.
- [x] **Scene Structure (2D/3D)**: Dual-mode hierarchy supporting 2D HyperLap layers and 3D SceneGraph trees with context menus, lock toggle, and eye visibility toggle.
- [x] **Unity-Style Inspectors**:
  - [x] 2D Item Inspector: Transform, Box2D Rigidbody, Dynamic Light 2D, `+ Add Component`.
  - [x] 3D GameObject Inspector: 3D Transform, Mesh geometry stats, PBR Material, Bullet Physics 3D.
- [x] **SpriteSheet Editor & Image Viewer**: 1:1 Pixel Zoom, frame slicing, live Animation Player with FPS spinner and frame scrubber.
- [x] **Particle 2D Designer**: Curve editors, emitter parameters, hardware OpenGL renderer, preset library (*Fireball Blast, Nebula Swarm, Toxic Spores, Cosmic Portal*).
- [x] **3D Model & GLTF 2.0 Viewer**: Native LibGDX 3D viewport, Orbit camera controls (Yaw/Pitch/Pan/Zoom), 3D grid floor, coordinate axes, `gdx-gltf` 2.0 PBR material rendering with 35 Khronos sample models.
- [x] **3D Asset Palette**: Primitives (*Cube, Sphere, Cylinder, Cone, Plane, Capsule*), Prefabs (*Spacecraft Fighter, Asteroid Rock, SciFi Turret, Energy Shield*), Materials (*Metallic Gold, Brushed Steel, Neon Cyan, Hull Paint, Glass*).
- [x] **Audio & Media Studio**: Real-time waveform visualizer, playback timeline, Volume/Pan sliders, Looping toggle.
- [x] **TexturePacker Studio**: Multi-atlas batch packer, whitespace trimming, padding spinners, and background thread execution.
- [x] **Bitmap & MSDF Font Studio**: Full rasterizer (.png + .fnt), signed distance field (SDF/MSDF) transforms, character presets, live sample testing, and GLSL distance-field shader exports.
- [x] **3D Particle Flame Studio**: 3D flame particle system editor with emitter lifecycle manager, physics gravity influencers, and live 60 FPS simulation canvas.
- [x] **Skin Composer & 9-Patch Editor**: VisUI styles, 9-patch interactive slicing, Widget Styles manager bound to `SkinModel`.
- [x] **AI Assistant & MCP Studio**: Multi-LLM provider integration, Model Context Protocol server.
- [x] **Build & Runtime Engine**: Desktop (LWJGL3) runner with live output streaming to NetBeans `IOProvider`, Android & Web targets.
- [x] **Documentation & User Guide**: `docs/UserGuide.md`, `docs/plan/v0.1.0_Milestone_Plan.md`, `docs/plan/v0.2.0_Milestone_Plan.md`, `docs/progress/v0.1.md`, `docs/progress/v0.2.md`, `README.md`.
