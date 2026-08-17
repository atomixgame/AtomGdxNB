# Build and Run Guide: AtomGdx Studio

Instructions for building, running, and testing AtomGdx Studio from the command line.

---

## 1. Building the Suite

### Full Build & Module Packaging
Execute the Gradle build task:
```powershell
./gradlew build
```

To run a clean build:
```powershell
./gradlew clean build
```

---

## 2. Launching AtomGdx Studio

### Running in Development Mode
To launch the NetBeans Platform application suite with all modules enabled:
```powershell
./gradlew run
```

Or using Ant harness:
```powershell
ant run
```

### Passing Custom VM Arguments
To run with custom memory limits or debug flags:
```powershell
./gradlew run --args="-J-Xmx4096m -J-Datomgdx.dev=true"
```

---

## 3. Creating Installers & Distribution Packages

### Creating Cross-Platform Zip / Tar.gz Bundle
```powershell
./gradlew buildApp
```
The output zip archive will be generated in `build/distributions/AtomGdxStudio-0.1.0.zip`.

### Creating Native OS Installers
```powershell
./gradlew buildInstallers
```

---

## 4. Running Unit & Integration Tests

Execute the automated test suite across all modules:
```powershell
./gradlew test
```

Generate test coverage reports:
```powershell
./gradlew jacocoTestReport
```
Reports are available at `build/reports/tests/test/index.html`.
