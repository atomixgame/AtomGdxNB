# AtomGdx Studio - Comprehensive User Guide & Manual

> Complete guide for building 2D and 3D cross-platform games using AtomGdx Studio on the NetBeans Platform with native LibGDX runtime integration.

---

## 📑 Table of Contents

1. [IDE Overview & Architecture](#1-ide-overview--architecture)
2. [Project Management & Liftoff](#2-project-management--liftoff)
3. [The Unified Visual Game Development Pipeline](#3-the-unified-visual-game-development-pipeline)
4. [2D Game Development & Level Construction](#4-2d-game-development--level-construction)
   - [TileMap Studio (Unity & Tiled Parity)](#tilemap-studio-unity--tiled-parity)
   - [Multi-Device Resolution & Screen Preview](#multi-device-resolution--screen-preview)
   - [HyperLap2D Scene Designer & Hierarchy](#hyperlap2d-scene-designer--hierarchy)
   - [Unity-Style 2D Inspector](#unity-style-2d-inspector)
   - [SpriteSheet Editor & Animation Player](#spritesheet-editor--animation-player)
   - [2D Particle Designer & Presets](#2d-particle-designer--presets)
   - [Bitmap & MSDF Font Studio](#bitmap--msdf-font-studio)
   - [VisUI Skin Composer & 9-Patch Slicer](#visui-skin-composer--9-patch-slicer)
5. [3D Game Development & Graphics Studio](#5-3d-game-development--graphics-studio)
   - [OpenGL 3D Viewport & Interactive Transform Gizmos](#opengl-3d-viewport--interactive-transform-gizmos)
   - [PBR Material & Shader Studio](#pbr-material--shader-studio)
   - [Environment Lighting & HDRI Skybox Studio](#environment-lighting--hdri-skybox-studio)
   - [3D SceneGraph Hierarchy & Asset Palette](#3d-scenegraph-hierarchy--asset-palette)
   - [3D Particle Flame Studio](#3d-particle-flame-studio)
6. [Visual Node Editor Suite (Visual Library API)](#6-visual-node-editor-suite-visual-library-api)
   - [Visual ShaderGraph Studio (PBR Master & GLSL Generator)](#visual-shadergraph-studio)
   - [Visual Scripting & Finite State Machine (FSM) Engine](#visual-scripting--finite-state-machine-fsm-engine)
   - [Animator State Machine & 2D Blend Trees](#animator-state-machine--2d-blend-trees)
   - [Procedural Geometry & Mesh Generation Nodes](#procedural-geometry--mesh-generation-nodes)
7. [Timeline & Animation Player Studio (4 Animation Architectures)](#7-timeline--animation-player-studio-4-animation-architectures)
8. [Ashley ECS Component Registry & Prefabs](#8-ashley-ecs-component-registry--prefabs)
9. [Audio & Media Studio](#9-audio--media-studio)
10. [Multi-Platform Build Configuration Matrix](#10-multi-platform-build-configuration-matrix)
11. [AI Copilot & Model Context Protocol (MCP)](#11-ai-copilot--model-context-protocol-mcp)
12. [Interactive Demo Project: NeonCosmos](#12-interactive-demo-project-neoncosmos)

---

## 1. IDE Overview & Architecture

**AtomGdx Studio** is an all-in-one Integrated Game Development Environment (IDE) built on the NetBeans Rich Client Platform (RCP) for the **LibGDX** framework. It unifies all fragmented desktop utilities into a cohesive dark-themed environment powered by real hardware OpenGL rendering.

```
+---------------------------------------------------------------------------------------------------+
| AtomGdx Studio [LibGDX 1.13.1 | Java 21 LTS]                                                      |
+-------------------+-------------------------------------------------------+-----------------------+
| Project Explorer  | Viewport (2D Scene / 3D OpenGL / TileMap Studio /     | Inspector             |
| Scene Structure / | Timeline / ShaderGraph / FSM / Animator / Geometry)   | (Unity-Style          |
| 3D SceneGraph /   |                                                       | Collapsible           |
| Asset Palette     |                                                       | Components & PBR)     |
+-------------------+-------------------------------------------------------+-----------------------+
| Bottom Output (Gradle build matrix, IOProvider console, Timeline Scrubber, AI Copilot)            |
+---------------------------------------------------------------------------------------------------+
```

---

## 2. Project Management & Liftoff

- **Project Explorer Tree**: Visualizes source packages, `assets/` directories, deployment targets, and Gradle build configurations.
- **Context Actions**:
  - Double-click `.dt` / `.scene` &rarr; Opens 2D Scene Editor.
  - Double-click `.tmx` / `.tilemap.json` &rarr; Opens TileMap Studio.
  - Double-click `.shadergraph.json` &rarr; Opens Visual ShaderGraph Studio.
  - Double-click `.fsm.json` &rarr; Opens Visual Scripting & FSM Graph.
  - Double-click `.animator.json` &rarr; Opens Animator State Machine Studio.
  - Double-click `.geonodes.json` &rarr; Opens Procedural Geometry Nodes.
  - Double-click `.gltf` / `.glb` / `.obj` / `.g3db` &rarr; Opens 3D Model Viewer.
  - Double-click `.ogg` / `.mp3` / `.wav` &rarr; Opens Audio & Media Studio.
  - Right-click Project &rarr; **Run Desktop (LWJGL3)** (triggers `gradlew lwjgl3:run` streamed to Output window).

---

## 3. The Unified Visual Game Development Pipeline

All subsystems in AtomGdx Studio are designed to interconnect seamlessly:

```
+---------------------+     +----------------------+     +---------------------+
| Procedural Geometry | --> | Visual ShaderGraph   | --> | 3D PBR Viewport     |
| & Mesh Nodes        |     | (GLSL Shader Export) |     | & Transform Gizmos  |
+---------------------+     +----------------------+     +---------------------+
                                                                   |
+---------------------+     +----------------------+               v
| Animator State      | --> | Animation Timeline   | --> | Ashley ECS Prefabs  |
| Machine & Blending  |     | (4 Anim Types)       |     | & Scene Graph       |
+---------------------+     +----------------------+     +---------------------+
                                                                   |
+---------------------+     +----------------------+               v
| TileMap Studio      | --> | Visual Scripting     | --> | Multi-Platform      |
| (5 Grid Layouts)    |     | & AI FSM Logic       |     | Build Matrix (Run)  |
+---------------------+     +----------------------+     +---------------------+
```

---

## 4. 2D Game Development & Level Construction

### TileMap Studio (Unity & Tiled Parity)
- **Menu Access**: `Window > LibGDX Tools > TileMap Studio` or `LibGDX > TileMap Studio`.
- **5 Supported Grid Layouts**:
  - `Orthogonal`: Standard rectangular grid.
  - `Isometric Diamond (2:1)` & `Isometric Staggered`: True 2:1 isometric projection.
  - `Hexagonal Pointy-Top` & `Hexagonal Flat-Top`: Staggered hexagon tiles.
- **Multi-Layer System**:
  - `Tile Layer`: 2D tile cell grid with global IDs (GIDs).
  - `Object Group Layer`: Vector shapes (Rectangles, Ellipses, Polygons, Points, Spawns, Triggers, Colliders, Waypoints).
  - `Image Layer`: Parallax background/foreground scenery with repeat and parallax factor X/Y.
- **Unity-Style Tile Palette**:
  - Paint / Stamp: Paint individual tiles or multi-cell stamps.
  - Bucket Fill: 4-way flood fill bounded regions.
  - Eraser: Clear cells on the active layer.
  - Rectangle Fill: Drag bounding box to fill rectangular tile region.
  - Line Tool: Bresenham line algorithm for tile placement.
  - Eyedropper: Sample clicked tile directly into active brush.
  - Marquee Select: Select, Cut, Copy, Paste tile blocks.
  - Random Weighted Brush: Weighted random selection among a group of tiles.
  - Auto-Tile (Rule Tile): Instant 8-neighbor bitmask calculation (255 combinations) resolving corner, edge, and center tiles.
- **Physics Collision Generation (CompositeCollider2D)**:
  - Scans solid tiles and merges contiguous rectangular boundaries into minimal outer polygon contours, eliminating internal ghost seam snags in Box2D.
- **Native LibGDX TMX Export**:
  - Export standard `.tmx` XML files loadable directly in LibGDX via `new TmxMapLoader().load("tilemaps/space_station_level.tmx")`.

### Multi-Device Resolution & Screen Preview
- **Device Presets**:
  - Desktop 1080p FHD (1920x1080, 16:9) & 4K UHD (3840x2160)
  - Steam Deck (1280x800, 16:10)
  - Nintendo Switch (1280x720)
  - iPhone 15 / Pro (2556x1179, 19.5:9) with Safe Area Notch / Dynamic Island guide
  - Google Pixel 8 (2400x1080, 20:9) with camera punch-hole guide
  - iPad Pro 12.9" (2732x2048, 4:3)
- **Orientation & Zoom**:
  - Instant toggle between **Landscape** and **Portrait**.
  - Zoom scaling (25%, 50%, 75%, 100%, 150%, 200%, Fit to Canvas).

---

## 5. 3D Game Development & Graphics Studio

### OpenGL 3D Viewport & Interactive Transform Gizmos
- **Hardware Pipeline**: Real LibGDX 3D engine with `PerspectiveCamera`, `ModelBatch`, `ModelInstance`, `Environment`, `DirectionalLight`, and `AmbientLight`.
- **Interactive Transform Gizmos**:
  - Translate Gizmo: Red (X), Green (Y), Blue (Z) axis arrows.
  - Rotate Gizmo: Red, Green, Blue gimbal circles for Euler rotation.
  - Scale Gizmo: Uniform and per-axis box scaling handles.

### PBR Material & Shader Studio
- **Menu Access**: `Window > LibGDX Tools > PBR Material Studio` or `LibGDX > PBR Material Studio`.
- **Parameters**: Albedo / Base Color, Metallic (0.0 to 1.0), Roughness (0.0 to 1.0), Normal Map Scale, AO, Emissive Rim Glow, Clearcoat coating.
- **Live Preview Sphere**: Real-time sphere preview with specular highlights and Fresnel reflections.

### Environment Lighting & HDRI Skybox Studio
- **Menu Access**: `Window > LibGDX Tools > Environment & Lighting` or `LibGDX > Environment & Lighting`.
- **HDRI Skybox**: Presets (*Space Nebula, Sunset Horizon, SciFi Studio, Overcast Sky, Industrial Garage*).
- **Directional Sun Orbit Compass**: Interactive 2D pitch/yaw compass controlling sun elevation and azimuth.

---

## 6. Visual Node Editor Suite (Visual Library API)

Powered by the **NetBeans Visual Library API** (`org.netbeans.api.visual`), AtomGdx Studio provides visual node graphs with pan, zoom, smooth connection routing, pin type compatibility validation, and code compilation.

### Visual ShaderGraph Studio
- **Menu Access**: `Window > LibGDX Tools > Visual ShaderGraph Studio` or `LibGDX > Visual ShaderGraph Studio`.
- **Nodes**:
  - **PBR Master Stack**: Base Color, Metallic, Roughness, Normal, Emission, Alpha inputs.
  - **Texture Nodes**: Sample Texture 2D, UV Tiling & Offset, Normal Unpack.
  - **Math Nodes**: Add, Subtract, Multiply, Divide, Lerp, Step, Clamp, Power, Sine, Cosine, Dot/Cross product.
  - **Procedural Nodes**: Voronoi Noise, Perlin Noise, Gradient Noise.
  - **Parameters**: Color Parameter, Float Parameter, Time uniform, Camera View Direction, World Normal.
- **GLSL Generation**: 1-click compilation to LibGDX Vertex (`.vert`) and Fragment (`.frag`) GLSL shaders with live code output preview.

### Visual Scripting & Finite State Machine (FSM) Engine
- **Menu Access**: `Window > LibGDX Tools > Visual Scripting & FSM Graph` or `LibGDX > Visual Scripting & FSM Graph`.
- **Features**:
  - State nodes (`State: Radar_Scan`, `State: Target_Lock`, `State: Rapid_Fire_Burst`, `State: Vent_Cooldown`).
  - Flow transitions with event triggers and conditional guards.
  - Action / Blueprint nodes: Play Audio SFX, Spawn Entity, Apply Box2D Force, Set Blackboard Variable.

### Animator State Machine & 2D Blend Trees
- **Menu Access**: `Window > LibGDX Tools > Animator State Machine & Blend Tree` or `LibGDX > Animator State Machine & Blend Tree`.
- **Features**:
  - Animation state nodes (`Idle`, `Walk`, `Run`, `Jump`, `Attack`).
  - Transition arrows with exit time, crossfade blend duration ms, and trigger parameters (`Speed > 0.1`, `IsGrounded == true`, `AttackTrigger`).
  - **2D Directional Blend Trees**: Blends 4-way walk/run clips based on Velocity X/Y joystick axes.

### Procedural Geometry & Mesh Generation Nodes
- **Menu Access**: `Window > LibGDX Tools > Procedural Geometry Nodes` or `LibGDX > Procedural Geometry Nodes`.
- **Features**:
  - Generator Nodes: Grid, Cube, Sphere, Cylinder, Torus primitives.
  - Modifier Nodes: Displace by Perlin Noise, Extrude Faces, Subdivide Mesh, Bevel Edges.
  - Output Node: LibGDX 3D Model / Mesh compiler.

---

## 7. Timeline & Animation Player Studio (4 Animation Architectures)

- **Menu Access**: `Window > LibGDX Tools > Animation Timeline` or `LibGDX > Animation Timeline`.
- **4 Animation Types**:
  1. **Node Properties**: Position X/Y/Z, Rotation, Scale X/Y, Opacity, Color Tint, and Shader Uniforms.
  2. **Skeletal 2D (Spine / DragonBones)**: Hierarchical bone trees, rotations, and skin slot attachment swapping.
  3. **Skeleton 3D (glTF Rig)**: 3D joint transforms, morph target blend shapes (`Smile`, `Blink`), and root motion.
  4. **SpriteFrames Ex**: Multi-part paperdoll hierarchies (Torso &rarr; Head &rarr; Armor &rarr; Weapon).
- **Keyframe Easing Curves**: `Linear`, `Step`, `Ease In`, `Ease Out`, `Ease In-Out (Cubic Bezier)`, `Bounce`, `Elastic`.
- **Events Track**: Dedicated event markers (`▲`) with parameter payload inspector (Audio cues, string/int/float arguments).
- **Transport Controls**: 60 FPS playback, speeds (0.25x to 4.0x), loop toggle, onion skinning, and frame snapping.

---

## 8. Ashley ECS Component Registry & Prefabs

- **Menu Access**: `Window > LibGDX Tools > Ashley ECS Components` or `LibGDX > Ashley ECS Components`.
- **Component Scanning**: Introspects built-in engine components and generates custom Ashley ECS Java component classes.
- **Prefab System**: Create and instantiate reusable 2D (`PrefabVO`) and 3D (`Prefab3DVO`) entity prefabs.

---

## 9. Audio & Media Studio

- **Menu Access**: `Window > LibGDX Tools > Audio Media Studio` or `LibGDX > Audio Media Studio`.
- **Features**: Real-time stereo waveform visualizer, playhead scrubbing, volume (0–100%), and stereo panning (-1.0 to +1.0).

---

## 10. Multi-Platform Build Configuration Matrix

- **Menu Access**: `Window > LibGDX Tools > Build Configurations` or `LibGDX > Build Configurations`.
- **Deployment Targets**: Desktop (LWJGL3), Android (APK/AAB), Web (HTML5 TeaVM/GWT), iOS (RoboVM).
- **Profiles**: Custom JVM arguments, environment flags, and live streaming to NetBeans Output console.

---

## 11. AI Copilot & Model Context Protocol (MCP)

- **Menu Access**: `Window > LibGDX Tools > AI Copilot` or `LibGDX > AI Copilot`.
- **LLM Support**: Ollama Local, OpenAI, Anthropic Claude, Google Gemini.
- **MCP Integration**: Exposes scene graphs, material definitions, and project structure directly to AI coding agents.

---

## 12. Interactive Demo Project: NeonCosmos

The bundled example project in [`Workspace/NeonCosmos/`](../Workspace/NeonCosmos/) contains rich demo files showcasing all studios:

| Studio / Feature | Demo File Path in `NeonCosmos/assets/` |
| :--- | :--- |
| **TileMap Studio** | `assets/tilemaps/space_station_level.tmx` |
| **Visual ShaderGraph** | `assets/graphs/hologram_shield.shadergraph.json` |
| **Visual Scripting & FSM** | `assets/graphs/enemy_turret.fsm.json` |
| **Animator Controller** | `assets/graphs/spacecraft_animator.animator.json` |
| **Procedural Geometry** | `assets/graphs/procedural_asteroid.geonodes.json` |
| **3D GLTF Models** | `assets/models/spaceship.gltf`, `cyber_hovercraft.gltf`, `khronos/*.glb` |
| **2D HyperLap2D Scene** | `assets/scenes/MainScene.dt`, `level1.scene2d` |
| **Particle 2D FX** | `assets/particles/plasma_burst.p` |
| **VisUI Skin** | `assets/skins/scifi.skin` |
| **Custom Shaders** | `assets/shaders/neon.vert`, `assets/shaders/neon.frag` |
