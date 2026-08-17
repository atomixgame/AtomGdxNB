# AI Assistant and Model Context Protocol (MCP) Design

Design specification for AI-driven game development and Model Context Protocol (MCP) server/client integration within AtomGdx Studio.

---

## 1. Overview

AtomGdx Studio integrates an intelligent, multi-provider AI copilot and standard **Model Context Protocol (MCP)** support. This enables:
1. **Interactive AI Assistant**: Dockable chat panel for code generation, mathematical derivations, shader explanations, and bug resolution.
2. **MCP Server**: AtomGdx Studio exposes the active LibGDX project AST, scene hierarchy, texture atlas metadata, particle configs, and shader code as MCP Resources and Tools to AI models and agentic workflows.
3. **MCP Client**: AtomGdx Studio connects to external MCP tools (such as asset generators, documentation servers, or database sidecars).
4. **Generative Art Tools**: Direct prompt-to-texture and prompt-to-sprite generation integrated into the game's asset pipeline.

---

## 2. Architecture & Data Flow

```
+-------------------------------------------------------------------------+
|                           AtomGdx Studio IDE                            |
|                                                                         |
|  +--------------------+   +-----------------------+   +---------------+ |
|  | AI Chat & Copilot  |   | Generative Art Studio |   | Scene/Shader  | |
|  | (TopComponent)     |   | (Texture/Sprite Gen)  |   | Live Viewers  | |
|  +---------+----------+   +-----------+-----------+   +-------+-------+ |
|            |                          |                       |         |
|  +---------v--------------------------v-----------------------v-------+ |
|  |                    AtomGdx AI Core Service                         | |
|  |  - Provider Adapters: Gemini, Claude, OpenAI, Ollama (Local LLM)  | |
|  |  - Context Builder: Open files, cursor token, compilation errors   | |
|  +------------------------------------+-------------------------------+ |
|                                       |                                 |
|  +------------------------------------v-------------------------------+ |
|  |                Embedded MCP Server & Client Layer                  | |
|  |  - MCP Server (JSON-RPC 2.0 / SSE / Stdio transport)              | |
|  |  - MCP Resources: libgdx://project/tree, libgdx://assets/atlas    | |
|  |  - MCP Tools: create_particle, compile_shader, generate_ui_skin    | |
|  +--------------------------------------------------------------------+ |
+-------------------------------------------------------------------------+
```

---

## 3. MCP Server Protocol Resources & Tools

### 3.1 Exposed MCP Resources
| Resource URI | Description | MIME Type |
|---|---|---|
| `libgdx://project/manifest` | Project metadata, build targets, dependencies | `application/json` |
| `libgdx://assets/atlas/list` | List of packed texture atlases and sprites | `application/json` |
| `libgdx://scenes/active/hierarchy` | Scene2D / HyperLap2D active scene node graph | `application/json` |
| `libgdx://shaders/list` | List of `.vert` and `.frag` shader source files | `application/json` |
| `libgdx://particles/list` | List of 2D/3D particle system configurations | `application/json` |

### 3.2 Exposed MCP Tools
| Tool Name | Parameters | Description |
|---|---|---|
| `analyze_game_performance` | `{ "logcat_output": string }` | Analyzes frame-rate drops or memory allocations |
| `create_particle_effect` | `{ "name": string, "type": "2d"|"3d", "preset": string }` | Creates new particle file in assets |
| `generate_scene2d_screen` | `{ "screen_name": string, "layout": string }` | Generates a complete Java/Kotlin Screen class with VisUI widgets |
| `compile_glsl_shader` | `{ "vert_code": string, "frag_code": string }` | Validates GLSL syntax in the embedded shader engine |
| `repack_texture_atlas` | `{ "directory": string }` | Triggers TexturePacker rebuild and updates atlas references |

---

## 4. Multi-Provider AI Engine Configuration

Users can configure AI models via **Tools -> Options -> AI Assistant**:
- **Google Gemini**: Gemini 2.0 Flash / Pro via API key.
- **Anthropic Claude**: Claude 3.5 Sonnet / Haiku via API key.
- **OpenAI**: GPT-4o / GPT-4o-mini via API key.
- **Local Ollama**: `http://localhost:11434` supporting `deepseek-coder`, `qwen2.5-coder`, `llama3`.
