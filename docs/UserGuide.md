# AtomGdx Studio - User Guide

> Comprehensive guide for building 2D and 3D cross-platform games using AtomGdx Studio on the NetBeans Platform.

---

## Table of Contents

1. [Introduction & Overview](#1-introduction--overview)
2. [Project Management & Liftoff](#2-project-management--liftoff)
3. [2D Game Development](#3-2d-game-development)
   - [HyperLap2D Scene Designer](#hyperlap2d-scene-designer)
   - [Scene Structure & Hierarchy](#scene-structure--hierarchy)
   - [Unity-Style 2D Inspector](#unity-style-2d-inspector)
   - [SpriteSheet Editor & Animation Player](#spritesheet-editor--animation-player)
   - [2D Particle Designer & Presets](#2d-particle-designer--presets)
   - [Skin Composer & 9-Patch Editor](#skin-composer--9-patch-editor)
4. [3D Game Development](#4-3d-game-development)
   - [OpenGL 3D Viewport & Orbit Controls](#opengl-3d-viewport--orbit-controls)
   - [3D SceneGraph Hierarchy](#3d-scenegraph-hierarchy)
   - [3D GameObject & PBR Material Inspector](#3d-gameobject--pbr-material-inspector)
   - [3D Asset Palette (Primitives, Prefabs, Materials)](#3d-asset-palette-primitives-prefabs-materials)
5. [Audio & Media Studio](#5-audio--media-studio)
6. [AI Assistant & Model Context Protocol (MCP)](#6-ai-assistant--model-context-protocol-mcp)
7. [Building, Running & Multi-Platform Deployment](#7-building-running--multi-platform-deployment)

---

## 1. Introduction & Overview

**AtomGdx Studio** is an all-in-one Integrated Game Development Environment (IDE) built on the NetBeans Rich Client Platform (RCP) for the **LibGDX** framework. It unifies all fragmented desktop utilities into a cohesive dark-themed environment powered by real hardware OpenGL rendering.

```
+-----------------------------------------------------------------------------------+
| AtomGdx Studio [LibGDX 1.13.1 | Java 21 LTS]                                      |
+-------------------+---------------------------------------+-----------------------+
| Project Explorer  | Viewport (2D Scene / 3D OpenGL /      | Inspector             |
| Scene Structure / | Particle / SpriteSheet / Audio)       | (Unity-Style          |
| 3D SceneGraph /   |                                       | Collapsible           |
| Asset Palette     |                                       | Components & PBR)     |
+-------------------+---------------------------------------+-----------------------+
| Bottom Output (Gradle build logs, IOProvider, AI Assistant & MCP Copilot)         |
+-----------------------------------------------------------------------------------+
```

---

## 2. Project Management & Liftoff

- **Project Explorer Tree**: Visualizes source packages, `assets/` directories, deployment targets, and Gradle build configurations.
- **Context Actions**:
  - Double-click `.dt` / `.scene` &rarr; Opens 2D Scene Editor.
  - Double-click `.png` / `.jpg` &rarr; Opens SpriteSheet & Image Viewer.
  - Double-click `.gltf` / `.glb` / `.obj` / `.g3db` &rarr; Opens 3D Model Viewer.
  - Double-click `.ogg` / `.mp3` / `.wav` &rarr; Opens Audio & Media Studio.
  - Right-click Project &rarr; **Run Desktop (LWJGL3)** (triggers `gradlew lwjgl3:run` streamed to Output window).

---

## 3. 2D Game Development

### HyperLap2D Scene Designer
- **Fullscreen Canvas**: Real hardware OpenGL viewport using `LwjglAWTCanvas` without scrollbar dead space.
- **Navigation**: Left-click drag to pan the infinite virtual canvas; mouse wheel to zoom in and out.
- **Lighting & Physics**: Native rendering for Dynamic Point Lights with soft penumbra shadows and Box2D physics collider overlays.

### Scene Structure & Hierarchy
- **Layers & Entity Tree**: Organizes items into layers (*Background, Gameplay, HUD*).
- **Node Editing**:
  - `+ Create` dropdown (Sprite, Particle Emitter, Dynamic Light, Text Label, 9-Patch, Layer).
  - Right-click menu: Rename (F2), Duplicate (Ctrl+D), Hide/Show, Lock/Unlock, Delete (Del).

### Unity-Style 2D Inspector
- **Collapsible Component Foldouts** (`▼` / `▶`):
  - **Transform**: Position (X/Y), Scale (X/Y), Rotation (°), Origin (X/Y), Z-Index, Visibility, Lock state.
  - **Box2D Rigidbody**: Body Type (*Static, Kinematic, Dynamic*), Density, Friction, Restitution, Sensor, Bullet (CCD).
  - **Dynamic Light 2D**: Light Type (*POINT, CONE, DIRECTIONAL*), Rays, Distance, Soft Shadows, X-Ray.
- **`+ Add Component` Button**: Dynamically attach Physics, Point Lights, or Particle Emitters to any selected entity.

### SpriteSheet Editor & Animation Player
- **Grid Slicing**: Configurable Frame Width, Frame Height, Rows, Columns, and 1:1 Pixel Zoom.
- **Live Preview Player**: Interactive Play/Pause, FPS spinner (1–60 FPS), and timeline scrubber with frame index badges (`F1`, `F2`, ...).

### 2D Particle Designer & Presets
- **Particle Curves**: Full editing for Life, Scale, Velocity, Emission, Angle, and Color interpolation.
- **Preset Library**: One-click application of *Fireball Blast, Nebula Swarm, Toxic Spores, Cosmic Portal*.

### Skin Composer & 9-Patch Editor
- **Skin Composer**: Scene2D / VisUI button styles, text fields, checkboxes, scrollbars, and dialog windows.
- **9-Patch Editor**: Interactive visual drag handles to configure Left, Right, Top, Bottom stretchable borders.

---

## 4. 3D Game Development

### OpenGL 3D Viewport & Orbit Controls
- **Pipeline**: Real LibGDX 3D engine with `PerspectiveCamera`, `ModelBatch`, `ModelInstance`, `Environment`, `DirectionalLight`, and `AmbientLight`.
- **Orbit Camera**:
  - **Left-drag**: Orbit camera around target (Yaw / Pitch).
  - **Right-drag / Middle-drag**: Pan camera laterally.
  - **Scroll Wheel**: Smooth camera zoom.
  - **Reset Camera**: Returns camera to default isometric view.
- **Grid & Axes**: Dynamic ground grid and color-coded XYZ axes (*Red X, Green Y, Blue Z*).

### 3D SceneGraph Hierarchy
- Visualizes the 3D entity tree: `Scene Root` &rarr; `Environment & Lights` &rarr; `Main Camera` &rarr; `Spacecraft Fighter` &rarr; `Thruster Glow`.
- Top `+ Create` dropdown menu allows adding 3D Primitives, Lights, Cameras, and Prefabs directly.

### 3D GameObject & PBR Material Inspector
- **Transform 3D**: Position (X, Y, Z), Rotation Euler angles (X, Y, Z in degrees), Scale (X, Y, Z).
- **3D Mesh & Stats**: Shape selector, live Vertex count, Triangle count.
- **PBR Material**: Presets (*Metallic Gold, Brushed Steel, Neon Glow Cyan, SciFi Hull Paint, Matte Plastic, Transparent Glass*), Metallic slider, Roughness slider, Opacity slider, Two-Sided checkbox, Wireframe mode.
- **Bullet Physics 3D**: Body Type (*Static, Kinematic, Dynamic*), Mass (kg), Friction, Restitution, Collider Shape (*Box, Sphere, Capsule, Convex Hull*).

### 3D Asset Palette (Primitives, Prefabs, Materials)
- **Primitives Tab**: Cube/Box, Sphere, Cylinder, Cone, Plane/Floor, Capsule.
- **Prefabs Tab** (`.prefab.json`): Spacecraft Fighter, Asteroid Rock, SciFi Turret, Energy Shield.
- **Materials Tab** (`.mat.json`): Metallic Gold, Brushed Steel, Neon Cyan, Hull Paint, Glass.
- **Instantiation**: Click the `+` button or double-click any card to insert it directly into the active 3D scene.

---

## 5. Audio & Media Studio

- **Real-Time Waveform Visualizer**: Stereo audio amplitude representation with live blue active region.
- **Playback Controls**: Play, Pause, Stop, Looping toggle.
- **DSP Controls**: Volume slider (0–100%), Stereo Pan slider (-100% Left to +100% Right).
- **Metadata**: Format (*Vorbis, MP3, WAV*), Sample Rate (44.1kHz), Channels (*Stereo*), Bitrate.

---

## 6. AI Assistant & Model Context Protocol (MCP)

- **Multi-Provider LLM Integration**: Connects to Google Gemini, Anthropic Claude, OpenAI, and Local Ollama instances.
- **Model Context Protocol (MCP)**: Exposes live game scene graphs, material definitions, and project structure directly to AI coding agents.
- **In-IDE Prompting**: Generate game mechanics, particle curves, shader code, and level layouts directly from the AI Copilot window.

---

## 7. Building, Running & Multi-Platform Deployment

### Desktop Execution
Run directly from the IDE toolbar or by right-clicking a project in the Project Explorer:
```bash
./gradlew lwjgl3:run
```

### Android Deployment
Select the Android deployment target to launch the app on connected devices or Android Virtual Devices (AVD):
```bash
./gradlew android:installDebug android:run
```

### Web (TeaVM / HTML5)
Launch the local web dev server to preview HTML5 games in your browser:
```bash
./gradlew html:superDev
```
