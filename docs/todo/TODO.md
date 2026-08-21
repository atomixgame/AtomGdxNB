# AtomGdx Studio - Task Tracker & Living TODO

## Active Milestone: v0.2.0 (In Progress)

### Completed v0.2.0 Deliverables (v0.2.1 -> v0.2.10)
- [x] **Feature/Concept: Device, Resolution & ScreenMode Preview (v0.2.1)**:
  - [x] Add Preview Canvas dropdown to select device presets (iPhone 15, Pixel 8, iPad Pro, 1080p FHD, Steam Deck, Custom).
  - [x] Adapt layout immediately on resolution change with aspect ratio letterboxing and safe-area notch overlay.
  - [x] Orientation switcher (Landscape / Portrait).
- [x] **Build & Build Configurations (v0.2.2)**:
  - [x] Connect targets (Desktop LWJGL3, Android APK/AAB, Web TeaVM/GWT, iOS RoboVM) to Gradle build parameters and flavor profiles.
- [x] **Timeline & Animations Studio (v0.2.3)**:
  - [x] Create `TimelineTopComponent` with dope-sheet track view.
  - [x] Playback control toolbar (Play, Pause, Step, Loop, Scrubber) for 2D spritesheet frames and 3D glTF skeletal animations.
- [x] **PBR Material & Environment Lighting Studio (v0.2.4 & v0.2.5)**:
  - [x] Create `MaterialEditorTopComponent` with live shader linkage.
  - [x] Create `LightingEnvironmentTopComponent` with HDRI skybox IBL reflections, ambient light, and sun shadow controls.
- [x] **Ashley ECS Component Registry & Prefabs (v0.2.6 & v0.2.7)**:
  - [x] Create `ComponentRegistryTopComponent` to inspect available components via code introspection.
  - [x] Prefab asset workflow and scene instantiation.
- [x] **3D Interactive Gizmos (v0.2.8)**:
  - [x] Interactive 3D Translate (arrows), Rotate (rings), and Scale (boxes) gizmos directly on the OpenGL canvas.
- [x] **2D Isometric & Hexagonal Tilemap Grids (v0.2.9)**:
  - [x] Orthogonal, Isometric Diamond (2:1), Isometric Staggered, Hexagonal Pointy/Flat grid rendering.
- [x] **Unified Suite Verification (v0.2.10)**:
  - [x] JUnit unit tests for DeviceResolution, FontGenerator, and BuildConfigModel; Ant build verified.
- [x] **TileMap Studio & Level Construction Engine (v0.2.11 - Unity & Tiled Parity)**:
  - [x] 5 Grid layouts (Orthogonal, Isometric Diamond 2:1, Isometric Staggered, Hexagonal Pointy/Flat).
  - [x] Multi-layer hierarchy (Tile Layer, Object Group Layer, Parallax Image Layer, Group Folders).
  - [x] Unity-style Tile Palette (Zoom slider, tileset picker, Paint, Bucket, Eraser, Rect Fill, Line, Eyedropper, Marquee).
  - [x] Smart Auto-Tiling (3x3 neighbor bitmask / Wang terrain sets) & Multi-frame Animated Tiles.
  - [x] Box2D Collision generator with boundary contour merger (CompositeCollider2D).
  - [x] Native LibGDX TMX XML Exporter/Importer compatible with `TmxMapLoader`.

---

## Upcoming Roadmap: v0.3.0

- [ ] **Visual Shader Graph Editor**: Node-based shader graph editor producing LibGDX GLSL shaders.
- [ ] **State Machine & Visual Scripting Graph**: Node-based state machine for AI entity behaviors and dialogue flow.
- [ ] **Asset Bundle Manager**: Asset bundle packing, compression, and OTA dynamic content download system.

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
