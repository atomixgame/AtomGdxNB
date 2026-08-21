# AtomGdx Studio - Comprehensive User Guide & Manual

> Complete guide for building 2D and 3D cross-platform games using AtomGdx Studio on the NetBeans Platform with native LibGDX runtime integration.

---

## 📑 Table of Contents

1. [IDE Overview & Architecture](#1-ide-overview--architecture)
2. [Project Management & Liftoff](#2-project-management--liftoff)
3. [2D Game Development & Level Construction](#3-2d-game-development--level-construction)
   - [TileMap Studio (Unity & Tiled Parity)](#tilemap-studio-unity--tiled-parity)
   - [Multi-Device Resolution & Screen Preview](#multi-device-resolution--screen-preview)
   - [HyperLap2D Scene Designer & Hierarchy](#hyperlap2d-scene-designer--hierarchy)
   - [Unity-Style 2D Inspector](#unity-style-2d-inspector)
   - [SpriteSheet Editor & Animation Player](#spritesheet-editor--animation-player)
   - [2D Particle Designer & Presets](#2d-particle-designer--presets)
   - [Bitmap & MSDF Font Studio](#bitmap--msdf-font-studio)
   - [VisUI Skin Composer & 9-Patch Slicer](#visui-skin-composer--9-patch-slicer)
4. [3D Game Development & Graphics Studio](#4-3d-game-development--graphics-studio)
   - [OpenGL 3D Viewport & Interactive Transform Gizmos](#opengl-3d-viewport--interactive-transform-gizmos)
   - [PBR Material & Shader Studio](#pbr-material--shader-studio)
   - [Environment Lighting & HDRI Skybox Studio](#environment-lighting--hdri-skybox-studio)
   - [3D SceneGraph Hierarchy & Asset Palette](#3d-scenegraph-hierarchy--asset-palette)
   - [3D Particle Flame Studio](#3d-particle-flame-studio)
5. [Timeline & Animation Player Studio](#5-timeline--animation-player-studio)
6. [Ashley ECS Component Registry & Prefabs](#6-ashley-ecs-component-registry--prefabs)
7. [Audio & Media Studio](#7-audio--media-studio)
8. [Multi-Platform Build Configuration Matrix](#8-multi-platform-build-configuration-matrix)
9. [AI Copilot & Model Context Protocol (MCP)](#9-ai-copilot--model-context-protocol-mcp)

---

## 1. IDE Overview & Architecture

**AtomGdx Studio** is an all-in-one Integrated Game Development Environment (IDE) built on the NetBeans Rich Client Platform (RCP) for the **LibGDX** framework. It unifies all fragmented desktop utilities into a cohesive dark-themed environment powered by real hardware OpenGL rendering.

```
+---------------------------------------------------------------------------------------------------+
| AtomGdx Studio [LibGDX 1.13.1 | Java 21 LTS]                                                      |
+-------------------+-------------------------------------------------------+-----------------------+
| Project Explorer  | Viewport (2D Scene / 3D OpenGL / TileMap Studio /     | Inspector             |
| Scene Structure / | Timeline / PBR Shader Studio / Device Preview Canvas) | (Unity-Style          |
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
  - Double-click `.dt` / `.scene` -> Opens 2D Scene Editor.
  - Double-click `.tmx` / `.tilemap.json` -> Opens TileMap Studio.
  - Double-click `.png` / `.jpg` -> Opens SpriteSheet & Image Viewer.
  - Double-click `.gltf` / `.glb` / `.obj` / `.g3db` -> Opens 3D Model Viewer.
  - Double-click `.ogg` / `.mp3` / `.wav` -> Opens Audio & Media Studio.
  - Right-click Project -> **Run Desktop (LWJGL3)** (triggers `gradlew lwjgl3:run` streamed to Output window).

---

## 3. 2D Game Development & Level Construction

### TileMap Studio (Unity & Tiled Parity)
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
  - Export standard `.tmx` XML files loadable directly in LibGDX via `new TmxMapLoader().load("levels/map.tmx")`.

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

### HyperLap2D Scene Designer & Hierarchy
- **Fullscreen Canvas**: Real hardware OpenGL viewport using `LwjglAWTCanvas` without scrollbar dead space.
- **Navigation**: Left-click drag to pan the infinite virtual canvas; mouse wheel to zoom in and out.
- **Lighting & Physics**: Native rendering for Dynamic Point Lights with soft penumbra shadows and Box2D physics collider overlays.

### Unity-Style 2D Inspector
- **Collapsible Component Foldouts**:
  - **Transform**: Position (X/Y), Scale (X/Y), Rotation, Origin (X/Y), Z-Index, Visibility, Lock state.
  - **Box2D Rigidbody**: Body Type (*Static, Kinematic, Dynamic*), Density, Friction, Restitution, Sensor, Bullet (CCD).
  - **Dynamic Light 2D**: Light Type (*POINT, CONE, DIRECTIONAL*), Rays, Distance, Soft Shadows, X-Ray.
- **`+ Add Component` Button**: Dynamically attach Physics, Point Lights, or Particle Emitters to any selected entity.

### SpriteSheet Editor & Animation Player
- **Grid Slicing**: Configurable Frame Width, Frame Height, Rows, Columns, and 1:1 Pixel Zoom.
- **Live Preview Player**: Interactive Play/Pause, FPS spinner (1–60 FPS), and timeline scrubber with frame index badges (`F1`, `F2`, ...).

### 2D Particle Designer & Presets
- **Particle Curves**: Full editing for Life, Scale, Velocity, Emission, Angle, and Color interpolation.
- **Preset Library**: One-click application of *Fireball Blast, Nebula Swarm, Toxic Spores, Cosmic Portal*.

### Bitmap & MSDF Font Studio
- **Full Rasterizer**: Converts TrueType/OpenType (.ttf/.otf) fonts to `.png` texture atlases and `.fnt` descriptors.
- **Multi-Channel Signed Distance Field (MSDF)**: Generates distance-field fonts that retain crisp edges at extreme zoom levels with GLSL distance-field fragment shader export.

### VisUI Skin Composer & 9-Patch Slicer
- **Skin Composer**: Scene2D / VisUI button styles, text fields, checkboxes, scrollbars, and dialog windows.
- **9-Patch Editor**: Interactive visual drag handles to configure Left, Right, Top, Bottom stretchable borders.

---

## 4. 3D Game Development & Graphics Studio

### OpenGL 3D Viewport & Interactive Transform Gizmos
- **Hardware Pipeline**: Real LibGDX 3D engine with `PerspectiveCamera`, `ModelBatch`, `ModelInstance`, `Environment`, `DirectionalLight`, and `AmbientLight`.
- **Orbit Camera**:
  - Left-drag: Orbit camera around target (Yaw / Pitch).
  - Right-drag / Middle-drag: Pan camera laterally.
  - Scroll wheel: Dolly zoom in / out.
- **Interactive Transform Gizmos**:
  - Translate Gizmo: Red (X), Green (Y), Blue (Z) axis arrows.
  - Rotate Gizmo: Red, Green, Blue gimbal circles for Euler rotation.
  - Scale Gizmo: Uniform and per-axis box scaling handles.

### PBR Material & Shader Studio
- **PBR Parameters**:
  - Albedo / Base Color: Tint color picker and diffuse texture map.
  - Metallic: 0.0 (Dielectric) to 1.0 (Pure Metal).
  - Roughness: 0.0 (Mirror Polish) to 1.0 (Diffuse Matte).
  - Normal Map Scale: Tangent-space normal bump intensity.
  - Ambient Occlusion (AO): Crevice shadow intensity.
  - Emissive Glow: Color and emission intensity multiplier for neon/energy effects.
  - Clearcoat: Dual-layer car paint / lacquered wood reflection coating.
- **Live Preview Sphere**: Real-time 2.5D sphere preview with specular highlights and Fresnel rim glow.
- **GLSL Linkage**: Live preview of GLSL shader uniform declarations.

### Environment Lighting & HDRI Skybox Studio
- **HDRI Skybox**: Presets (*Space Nebula, Sunset Horizon, SciFi Studio, Overcast Sky, Industrial Garage*).
- **Image-Based Lighting (IBL)**: Separate diffuse irradiance and specular reflection intensity sliders.
- **Directional Sun**: Live 2D Sun Orbit Compass controlling Sun Pitch, Sun Yaw, Color, and Intensity.
- **Shadows & Fog**: Dynamic shadow map resolutions (512 to 4096) and distance fog simulation (Density, Near, Far, Color).

### 3D SceneGraph Hierarchy & Asset Palette
- **Palette Primitives**: Cube, Sphere, Cylinder, Cone, Plane, Capsule.
- **Palette Prefabs**: Spacecraft Fighter, Asteroid Rock, SciFi Turret, Energy Shield.
- **Palette Materials**: Metallic Gold, Brushed Steel, Neon Cyan, Hull Paint, Glass.

### 3D Particle Flame Studio
- 3D particle emitter system with physics gravity influencers, color gradients, velocity spreads, and 60 FPS OpenGL simulation canvas.

---

## 5. Timeline & Animation Player Studio

- **Dope-Sheet Scrubber**: Visual keyframe lane showing keyframe diamond markers (`◆`).
- **Track Types**:
  - Transform Tracks: `Position X`, `Position Y`, `Rotation`, `Scale X`, `Scale Y`, `Opacity`.
  - Sprite Frame Tracks: Sequence of spritesheet animation frames.
  - Event Trigger Tracks: Named audio or script triggers fired at specific timestamps.
- **Transport Controls**: Jump to Start, Step Previous Keyframe, Play / Pause (60 FPS playback timer), Step Next Keyframe, Loop Playback Toggle.

---

## 6. Ashley ECS Component Registry & Prefabs

- **Component Catalog**:
  - Built-in components: `TransformComponent`, `TextureComponent`, `AnimationComponent`, `RigidBody2DComponent`, `Mesh3DComponent`, `Light2DComponent`, `ScriptComponent`.
  - Custom component scanner and interactive Java component class generator.
- **Prefab Asset Workflow**:
  - Save configured entities as reusable 2D (`PrefabVO`) or 3D (`Prefab3DVO`) asset files.
  - Drag and drop prefabs into scenes to instantiate cloned hierarchies.

---

## 7. Audio & Media Studio

- **Real-Time Waveform Visualizer**: Renders audio amplitude waveforms across the full track duration.
- **Playback Controls**: Play, Pause, Stop, Seek timeline scrubber, Volume slider (0–100%), Stereo Pan (-1.0 to +1.0), and Loop toggle.

---

## 8. Multi-Platform Build Configuration Matrix

- **Target Platforms**:
  - Desktop (LWJGL3): `lwjgl3:run`, `lwjgl3:jar`
  - Android (APK/AAB): `android:assembleDebug`, `android:bundleRelease`
  - Web (HTML5 TeaVM/GWT): `teavm:build`, `teavm:run`
  - iOS (RoboVM / MobiVM): `ios:createIPA`, `ios:launchIOSDevice`
- **Profile Customization**:
  - Custom JVM Arguments (e.g. `-Xmx2048m -Dorg.lwjgl.util.Debug=true`).
  - Active profile management (Development, Staging, Production Release).
  - One-click build and execution with real-time log output streamed to NetBeans `IOProvider`.

---

## 9. AI Copilot & Model Context Protocol (MCP)

- **AI Assistant**: Multi-LLM provider support (Ollama local, OpenAI, Anthropic, Gemini, DeepSeek).
- **Model Context Protocol (MCP)**: Native MCP client & server enabling AI-assisted asset generation, shader coding, and project refactoring.
