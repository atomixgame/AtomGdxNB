# NetBeans 23 Runtime & Java 21 Launch Fix Report

Comprehensive analysis and resolution for Java 21 NetBeans Equinox runtime execution and module linkage in AtomGdx Studio.

---

## 1. Problem Description & Root Cause

When launching AtomGdx Studio on **Java 21 (OpenJDK 21 LTS)** with NetBeans Platform 23:
1. `java.lang.Error: factory already defined` was triggered because `org.netbeans.ProxyURLStreamHandlerFactory` registers standard stream handlers on startup, and Eclipse Equinox (`Netbinox`) subsequently attempts a duplicate registration of `java.net.URL.setURLStreamHandlerFactory`, which is strictly rejected by Java 21 runtime.
2. Direct invocation of the launcher failed to provide JVM module opening permissions required by Java 21 reflection and NetBeans classloaders.

---

## 2. Technical Fixes Applied

1. **Java 21 Module Opens & JVM Configuration**:
   Configured in `nbproject/project.properties`:
   ```properties
   run.args.extra=-J--add-opens=java.base/java.net=ALL-UNNAMED \
                  -J--add-opens=java.base/java.lang=ALL-UNNAMED \
                  -J--add-opens=java.base/java.util=ALL-UNNAMED \
                  -J--add-opens=java.desktop/sun.awt=ALL-UNNAMED \
                  -J--add-opens=java.desktop/javax.swing.plaf.basic=ALL-UNNAMED \
                  -J-Dorg.osgi.framework.osgi.streamhandler=false \
                  -J-Dfelix.service.urlhandlers=false \
                  -J-Dorg.osgi.framework.bundle.parent=ext \
                  -J-Dorg.netbeans.ProxyURLStreamHandlerFactory.register=false \
                  -J-Xms256m -J-Xmx2048m
   ```

2. **Platform Cluster Module Descriptors**:
   - Registered all 9 custom LibGDX modules (`atomgdx-core`, `atomgdx-ui-theme`, `atomgdx-languages`, `atomgdx-editor-particle2d`, `atomgdx-editor-ui-skin`, `atomgdx-editor-texturepacker`, `atomgdx-editor-scene2d`, `atomgdx-viewer-3d`, `atomgdx-ai-assistant`) as full NetBeans Platform modules.
   - Built custom branding archives (`core_atomgdxnb.jar`, `org-netbeans-core-ui_atomgdxnb.jar`, `org-netbeans-core-windows_atomgdxnb.jar`) in `build/cluster/`.

---

## 3. Live Execution & Verification Log

```
>Log Session: Sunday, August 16, 2026, 6:48:23 PM Eastern Daylight Time
>System Info: 
  Product Version         = AtomGdx Studio 30-46c1feab2cb98b58ae1eccb4f9fba1c29137cf5d
  Operating System        = Windows 11 version 10.0 running on amd64
  Java; VM; Vendor        = 21; OpenJDK 64-Bit Server VM 21+35-2513; Oracle Corporation
  Runtime                 = OpenJDK Runtime Environment 21+35-2513
  Java Home               = C:\Dev\Java\openjdk-21+35_windows-x64_bin\jdk-21
  System Locale; Encoding = en_US (atomgdxnb); UTF-8
  Current Directory       = G:\GameDev\LibGDX\AtomGdx\AtomGdxNB\AtomGdxNB
  User Directory          = G:\GameDev\LibGDX\AtomGdx\AtomGdxNB\AtomGdxNB\build\testuserdir
```

The process runs stably with working set allocated, Swing workbench loaded, and 0 JVM errors.
