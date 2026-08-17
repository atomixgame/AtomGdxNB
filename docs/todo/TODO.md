# AtomGdx Studio - Task Tracker & Living TODO

## Active Milestone: v0.1.72 (Core Complete)

### Completed Tasks
- [x] **Branding & Assets**: SVG Logo, window icons (16–512px), dark theme tokens, splash screen.
- [x] **2D Scene & Level Designer**: HyperLap2D integration, fullscreen `LwjglAWTCanvas`, layers, composite items, point lights, soft shadows.
- [x] **Scene Structure (2D/3D)**: Dual-mode hierarchy supporting 2D HyperLap layers and 3D SceneGraph trees with context menus (Create, Duplicate, Rename, Delete).
- [x] **Unity-Style Inspectors**:
  - [x] 2D Item Inspector: Transform, Box2D Rigidbody, Dynamic Light 2D, `+ Add Component`.
  - [x] 3D GameObject Inspector: 3D Transform, Mesh geometry stats, PBR Material, Bullet Physics 3D.
- [x] **SpriteSheet Editor & Image Viewer**: 1:1 Pixel Zoom, frame slicing, live Animation Player with FPS spinner and frame scrubber.
- [x] **Particle 2D Designer**: Curve editors, emitter parameters, hardware OpenGL renderer, preset library (*Fireball Blast, Nebula Swarm, Toxic Spores, Cosmic Portal*).
- [x] **3D Model & GLTF Viewer**: Native LibGDX 3D viewport, Orbit camera controls (Yaw/Pitch/Pan/Zoom), 3D grid floor, coordinate axes.
- [x] **3D Asset Palette**: Primitives (*Cube, Sphere, Cylinder, Cone, Plane, Capsule*), Prefabs (*Spacecraft Fighter, Asteroid Rock, SciFi Turret, Energy Shield*), Materials (*Metallic Gold, Brushed Steel, Neon Cyan, Hull Paint, Glass*).
- [x] **Audio & Media Studio**: Real-time waveform visualizer, playback timeline, Volume/Pan sliders, Looping toggle.
- [x] **Skin Composer & 9-Patch Editor**: VisUI styles, 9-patch interactive slicing, SDF font generator.
- [x] **AI Assistant & MCP Studio**: Multi-LLM provider integration, Model Context Protocol server.
- [x] **Build & Runtime Engine**: Desktop (LWJGL3) runner with live output streaming to NetBeans `IOProvider`, Android & Web targets.
- [x] **Documentation & User Guide**: `docs/UserGuide.md`, `docs/plan/v0.1.0_Milestone_Plan.md`, `docs/progress/v0.1.md`, `README.md`.

---

## Next Roadmap Items (v0.2.0)

### 1. Advanced 3D Engine Features
- [ ] GLTF skeletal animation playback timeline (play/pause/scrub specific animation tracks).
- [ ] 3D Gizmos (Translation, Rotation, Scale handles directly inside the OpenGL canvas).
- [ ] Procedural Terrain / Heightmap Editor with brush tools.

### 2. Enhanced 2D Tilemap & Level Tools
- [ ] Isometric / Hexagonal tilemap grid support.
- [ ] Auto-tiling rules and terrain brush presets.
- [ ] Smart Box2D pathfinding navmesh generator.

### 3. Visual Scripting & Behavior Trees
- [ ] Node-based visual logic graph for entity behaviors.
- [ ] State Machine Editor for LibGDX Ashley ECS components.

### 4. Feature/Concept of Build, BuildConfig, Preview Canvas, Device , Resolution, ScreenMode: 
- Add a Preview Canvas with Dropdown to select different Canvas size, represent different Devices with Resolution, ScreenMode [ Inspired by Unity]
- The Game Layout should adapt to each Resoluton immediately after change
- Build and Build Config translate and connect closely with the underlying Build of Gradle and ecosystem, platform: eg: Desktop, Android, iOS...

### 5. Improve Materials & Shader
- Create 2 more TopComponent: Material Editor, can review the linked Shader
- Node base editor for Shader, Material [like Unity, Blender]