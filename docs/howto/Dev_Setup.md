# Developer Setup: AtomGdx Studio

Guide for developers setting up their environment to build, run, and contribute to AtomGdx Studio.

---

## 1. Prerequisites

### Java Development Kit (JDK)
- **Required**: OpenJDK 21 LTS or Oracle JDK 21+.
- Verify installation:
  ```powershell
  java -version
  ```
- Ensure `JAVA_HOME` environment variable points to your JDK 21 installation path.

### Gradle
- **Recommended**: Gradle 8.5+ or use the included `gradlew` wrapper.
- Verify installation:
  ```powershell
  gradle -version
  ```

### Android SDK (Optional, for Android deployment)
- Ensure `ANDROID_HOME` or `ANDROID_SDK_ROOT` is set if developing or testing Android target deployment.
- Install Android command-line tools and platform tools (`adb`).

### Apache NetBeans Platform Harness
- The build pulls required NetBeans Platform bits (version 21 or 22) via Maven Central / Gradle repositories. No separate manual NetBeans installation is required for headless builds.

---

## 2. Cloning & Directory Layout

```powershell
git clone <repo-url> AtomGdxNB
cd AtomGdxNB/AtomGdxNB
```

---

## 3. IDE Configuration (Developing in NetBeans or IntelliJ)

### In Apache NetBeans
1. Open Apache NetBeans.
2. Select **File -> Open Project...**
3. Navigate to `AtomGdxNB` and open the Suite project.

### In IntelliJ IDEA
1. Select **File -> Open...**
2. Choose `build.gradle.kts` / `settings.gradle.kts`.
3. Enable Gradle project sync.

---

## 4. Environment Variables for AI Assistant

To enable cloud AI providers inside AtomGdx Studio during development, configure any of the following optional environment variables:
- `GEMINI_API_KEY`: Google Gemini API key.
- `ANTHROPIC_API_KEY`: Anthropic Claude API key.
- `OPENAI_API_KEY`: OpenAI API key.
- `OLLAMA_HOST`: Local Ollama URL (defaults to `http://localhost:11434`).
