# LibGDX Tools Integration Design: AtomGdx Studio

Technical specification for merging and embedding standalone LibGDX tools into the AtomGdx Studio NetBeans Platform environment.

---

## 1. Motivation

The LibGDX ecosystem provides multiple standalone Java/Swing/LWJGL desktop tools:
1. **Liftoff / gdx-setup**: Project generator.
2. **Particle Editor (2D / 3D)**: Visual effect authoring.
3. **Skin Composer / VisUI Designer**: Scene2D UI styling.
4. **HyperLap2D / Overlap2D**: 2D scene, composite actor, and lighting editor.
5. **TexturePacker / 9-Patch Editor**: Atlas generation.
6. **Hiero**: Bitmap font generator.
7. **G3D / GLTF Viewer**: 3D model inspection.

AtomGdx Studio integrates these tools as first-class NetBeans `TopComponent` and `MultiViewElement` editors, allowing bidirectional synchronization with code, asset hot-reloading, and project filesystem changes.

---

## 2. Integrated Tool Architectures

### 2.1 Liftoff Project Wizard (`atomgdx-project-liftoff`)
- **Integration**: Implements NetBeans `WizardDescriptor.InstantiatingIterator`.
- **UI Panels**:
  1. *Project Metadata*: Application Name, Package Name, Main Class, Destination Directory.
  2. *Platforms Selection*: Desktop (LWJGL3), Android, Web (TeaVM & GWT), iOS (RoboVM / MobiVM).
  3. *Extensions Selection*: Box2D, Box2DLight, Ashley, FreeType, gdx-ai, gdx-controllers, gdx-gltf, VisUI.
  4. *Languages & Tooling*: Java 21, Kotlin 1.9+, Groovy.
- **Engine**: Generates multi-module Gradle project structure and immediately opens the resulting project in the NetBeans project explorer.

### 2.2 2D Particle Editor (`atomgdx-editor-particle2d`)
- **File Association**: `.p`, `.party`, `.particle2d`
- **Embedding**: NetBeans `TopComponent` containing:
  - An embedded `Lwjgl3Canvas` / `JOGL` / `AWTGLCanvas` rendering viewport.
  - Emitter list table with add/remove/duplicate/reorder controls.
  - Particle property graph editor with bezier curve manipulation (Life, Duration, Count, Velocity, Angle, Scale, Color Gradient, Transparency, Spin).
  - Background image picker, additive blending toggle, and playback controls (Play, Pause, Restart, Step).

### 2.3 Scene2D / VisUI Skin Composer (`atomgdx-editor-ui-skin`)
- **File Association**: `.json` (with `skin.json` schema) and `.skin`
- **Embedding**: NetBeans `MultiViewElement` offering:
  - *Design View*: Interactive UI preview canvas rendering live Scene2D / VisUI widgets using the selected skin styles.
  - *Source View*: Synchronized text editor for the underlying skin JSON.
  - *Style Hierarchy*: Tree of widget styles (TextButton, Label, CheckBox, Slider, ProgressBar, Window, Dialog, ScrollPane, SelectBox, SplitPane).
  - *Asset Linker*: Direct integration with `.atlas` files and Color palette selectors.

### 2.4 2D Scene & Level Designer (`atomgdx-editor-scene2d`)
- **File Association**: `.scene2d`, `.h2d`, `.dt` (HyperLap2D format)
- **Features**:
  - Multi-layer canvas with parallax depth.
  - Box2D physics polygon tracing and collision mask configuration.
  - Box2DLight placement (PointLight, ConeLight, DirectionalLight) with color and shadow previews.
  - Camera bounds and viewport aspect ratio testing (16:9, 4:3, 9:16 portrait mobile).

### 2.5 Texture Packer & 9-Patch Studio (`atomgdx-editor-texturepacker`)
- **File Association**: `pack.json`, `.atlas`, `.9.png`
- **Embedding**:
  - GUI front-end for LibGDX `com.badlogic.gdx.tools.texturepacker.TexturePacker`.
  - Configurable settings: Max width/height, padding, edge padding, duplicate padding, strip whitespace, rotation, texture format (RGBA8888, RGB888, RGBA4444).
  - Interactive 9-Patch editor with 4-way stretchable handles and live scaling preview.

### 2.6 Hiero & FreeType Font Generator (`atomgdx-editor-font`)
- **File Association**: `.hiero`, `.fnt`, `.ttf`, `.otf`
- **Embedding**:
  - TTF/OTF character rasterizer with character set presets (ASCII, Latin-1, Cyrillic, CJK, Custom).
  - Multi-channel Distance Field (MSDF) and Signed Distance Field (SDF) generator for scalable vector-quality font rendering in LibGDX.
  - Drop shadow, stroke, gradient, and glow effect renderers.

### 2.7 3D Model & Shader Viewer (`atomgdx-viewer-3d`)
- **File Association**: `.g3db`, `.g3dj`, `.gltf`, `.glb`
- **Embedding**:
  - LibGDX 3D and `gdx-gltf` viewport with orbit/pan/zoom camera.
  - PBR environment lighting with skybox and HDR map support.
  - Skeletal animation timeline player with bone hierarchy inspector and speed controls.
  - Integrated GLSL shader live editor with real-time compilation onto the 3D model.
