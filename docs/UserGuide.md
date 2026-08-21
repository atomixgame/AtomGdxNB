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
5. [Timeline & Animation Player Studio (4 Animation Architectures)](#5-timeline--animation-player-studio-4-animation-architectures)
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

---

## 4. 3D Game Development & Graphics Studio

### OpenGL 3D Viewport & Interactive Transform Gizmos
- **Hardware Pipeline**: Real LibGDX 3D engine with `PerspectiveCamera`, `ModelBatch`, `ModelInstance`, `Environment`, `DirectionalLight`, and `AmbientLight`.
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

---

## 5. Timeline & Animation Player Studio (4 Animation Architectures)

The Timeline Studio provides a professional Dope-Sheet & Animation Scrubber supporting 4 distinct animation architectures:

### 1. 🎭 Node Properties Animations
- Animate entity transform properties: `Position X/Y/Z`, `Rotation`, `Scale X/Y`, `Opacity`, `Color Tint`, and custom shader floats.
- **Keyframe Easing Curves**:
  - `Linear`: Constant rate of change.
  - `Step`: Constant hold until next keyframe.
  - `Ease In` / `Ease Out`: Smooth quadratic acceleration / deceleration.
  - `Ease In-Out`: Smooth cubic bezier S-curve.
  - `Bounce`: Realistic gravity rebound effect.
  - `Elastic`: Damped spring oscillation.

### 2. 🦴 Skeletal 2D Animations (Spine / DragonBones)
- Full hierarchical bone trees: Root -> Torso -> Limbs -> Attachments.
- Animate bone local rotation, translation, and IK weights.
- Skin slot attachment swapping (e.g. swap sword skin for shield on specific keyframes).

### 3. 💀 Skeleton 3D Animations (glTF / g3db Rig)
- 3D joint quaternions, local translations, and bone hierarchy trees.
- Morph Target / Blend Shape tracks (e.g. facial blend shapes `Smile`, `Blink`, `JawOpen`).
- Root Motion extraction and clip blending.

### 4. 🎞️ SpriteFrames & SpriteFrames Ex (Paperdoll Hierarchies)
- Nested multi-part sprite hierarchies (Torso -> Head -> Armor -> Weapon Overlay) each with independent frame sequences and z-orders.
- Playback modes: `Once`, `Loop`, `Ping-Pong`, `Reverse`.

### ⚡ Timeline Events Management
- Dedicated **Events Track** with pink flag markers (`▲`).
- Add / edit custom named triggers:
  - `String Parameter` (e.g. collider name or hit type)
  - `Int / Float Parameter` (damage values or state codes)
  - `Audio Cue SFX` (e.g. `audio/sfx/footstep_metal.ogg`)
- Modal event inspector triggered via double-click on any event marker.

### 🎮 Transport Controls & Playback
- Speeds: `0.25x`, `0.5x`, `1.0x`, `1.5x`, `2.0x`, `4.0x`.
- `🧅 Onion Skinning`: View ghost silhouettes of previous and upcoming animation frames.
- `🧲 Snap to Grid`: Precision snapping to frame ticks based on target FPS (30, 60, 120 FPS).

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
