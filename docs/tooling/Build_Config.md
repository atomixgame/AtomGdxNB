# Build & Tooling Configuration: AtomGdx Studio

Technical specification for Gradle build scripts, NetBeans Platform harness configuration, and dependencies.

---

## 1. Gradle Build Ecosystem

AtomGdx Studio utilizes standard Gradle with the Apache NetBeans utilities plugin (`org.apache.netbeans.utilities:nbm-gradle-plugin`) and Java 21 LTS toolchain.

### Root `settings.gradle.kts` Structure
```kotlin
rootProject.name = "atomgdx-studio"

include(
    "atomgdx-branding",
    "atomgdx-core",
    "atomgdx-ui-theme",
    "atomgdx-project-liftoff",
    "atomgdx-gradle-integration",
    "atomgdx-editor-particle2d",
    "atomgdx-editor-particle3d",
    "atomgdx-editor-ui-skin",
    "atomgdx-editor-scene2d",
    "atomgdx-editor-texturepacker",
    "atomgdx-editor-font",
    "atomgdx-viewer-3d",
    "atomgdx-viewer-media",
    "atomgdx-languages",
    "atomgdx-ai-assistant",
    "atomgdx-platform-android",
    "atomgdx-platform-web"
)
```

---

## 2. Key Dependencies & Version Matrix

| Library / Tool | Version | Purpose |
|---|---|---|
| **NetBeans Platform Harness** | 21.0 / 22.0 | Core RCP framework, windowing, lookups |
| **LibGDX Core & Tools** | 1.13.1 | Core game engine, TexturePacker, Particle system |
| **gdx-gltf** | 2.2.1 | PBR shader & GLTF/GLB 3D model loading |
| **VisUI** | 1.5.5 | Scene2D modern UI widget library |
| **FlatLaf** | 3.5.4 | Dark modern Look & Feel base |
| **Model Context Protocol SDK** | 0.6.0+ | MCP Server/Client JSON-RPC implementation |
| **Gradle Tooling API** | 8.5+ | In-IDE Gradle execution and project model query |
