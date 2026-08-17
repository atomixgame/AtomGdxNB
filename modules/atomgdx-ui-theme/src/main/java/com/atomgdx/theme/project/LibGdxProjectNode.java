package com.atomgdx.theme.project;

import com.atomgdx.core.project.LibGdxProject;
import com.atomgdx.theme.windows.*;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.lookup.AbstractLookup;
import org.openide.util.lookup.InstanceContent;
import org.openide.windows.Mode;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.io.File;

/**
 * NetBeans Node representing a LibGDX Multi-Platform project in the Project Explorer.
 */
public class LibGdxProjectNode extends AbstractNode {

    private final LibGdxProject project;

    public LibGdxProjectNode(LibGdxProject project) {
        this(project, new InstanceContent());
    }

    private LibGdxProjectNode(LibGdxProject project, InstanceContent content) {
        super(new ProjectChildren(project), new AbstractLookup(content));
        this.project = project;
        content.add(project);
        setDisplayName(project.getName() + " [LibGDX " + project.getGdxVersion() + "]");
        setShortDescription("LibGDX Multi-Platform Project at " + project.getRootDirectory().getAbsolutePath());
    }

    @Override
    public Action[] getActions(boolean context) {
        return new Action[]{
                new AbstractAction("Run Desktop (LWJGL3)") {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        JOptionPane.showMessageDialog(null, "Launching LibGDX Desktop LWJGL3: " + project.getName(), "LibGDX Launcher", JOptionPane.INFORMATION_MESSAGE);
                    }
                },
                new AbstractAction("Run Web (TeaVM / HTML5)") {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        JOptionPane.showMessageDialog(null, "Starting TeaVM Local Server for: " + project.getName() + " at http://localhost:8080", "LibGDX Web Launcher", JOptionPane.INFORMATION_MESSAGE);
                    }
                },
                new AbstractAction("Build Android APK") {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        JOptionPane.showMessageDialog(null, "Building Android APK via Gradle: ./gradlew android:assembleDebug", "LibGDX Android Build", JOptionPane.INFORMATION_MESSAGE);
                    }
                },
                null, // Separator
                new AbstractAction("Open Assets Directory") {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        File assets = project.getAssetsDirectory();
                        JOptionPane.showMessageDialog(null, "Game Assets Directory:\n" + assets.getAbsolutePath(), "Assets Explorer", JOptionPane.INFORMATION_MESSAGE);
                    }
                }
        };
    }

    private static void openEditor(TopComponent tc) {
        try {
            Mode mode = WindowManager.getDefault().findMode("editor");
            if (mode != null) {
                mode.dockInto(tc);
            }
            tc.open();
            tc.requestActive();
        } catch (Exception ex) {
            tc.open();
        }
    }

    private static class ProjectChildren extends Children.Keys<String> {
        private final LibGdxProject project;

        ProjectChildren(LibGdxProject project) {
            this.project = project;
        }

        @Override
        protected void addNotify() {
            setKeys(new String[]{"Sources", "Assets", "Platforms", "Project Files"});
        }

        @Override
        protected Node[] createNodes(String key) {
            switch (key) {
                case "Sources":
                    return new Node[]{new CategoryNode("Source Packages", "Core, Desktop, Android, HTML source modules", new String[]{
                            "core/src/main/java/" + project.getPackageName(),
                            "desktop/src/main/java/" + project.getPackageName() + "/DesktopLauncher.java",
                            "android/src/main/java/" + project.getPackageName() + "/AndroidLauncher.java"
                    })};
                case "Assets":
                    return new Node[]{new AssetCategoryNode("Game Assets", "Textures, Particles, Skins, Audio, Shaders, 3D Models")};
                case "Platforms":
                    return new Node[]{new CategoryNode("Deployment Targets", "Configured target platforms", new String[]{
                            "Desktop (LWJGL3 64-bit)",
                            "Android (API 34+)",
                            "HTML5 (TeaVM WebAssembly / JS)"
                    })};
                case "Project Files":
                    return new Node[]{new CategoryNode("Build Configuration", "Gradle scripts & properties", new String[]{
                            "build.gradle.kts",
                            "settings.gradle.kts",
                            "gradle.properties"
                    })};
                default:
                    return new Node[0];
            }
        }
    }

    private static class AssetCategoryNode extends AbstractNode {
        AssetCategoryNode(String name, String desc) {
            super(new AssetChildren());
            setDisplayName(name);
            setShortDescription(desc);
        }
    }

    private static class AssetChildren extends Children.Keys<String> {
        @Override
        protected void addNotify() {
            setKeys(new String[]{
                    "particles/flame.particle",
                    "shaders/space_bg.glsl",
                    "ui/uiskin.json",
                    "ui/button.9.png",
                    "scenes/level1.json",
                    "models/spacefighter.gltf",
                    "audio/theme.ogg"
            });
        }

        @Override
        protected Node[] createNodes(String key) {
            return new Node[]{new FileAssetNode(key)};
        }
    }

    private static class FileAssetNode extends AbstractNode {
        private final String assetPath;

        FileAssetNode(String assetPath) {
            super(Children.LEAF);
            this.assetPath = assetPath;
            setDisplayName(assetPath);
            setShortDescription("Double click to edit in AtomGdx Studio: " + assetPath);
        }

        @Override
        public Action getPreferredAction() {
            return new AbstractAction("Open in Editor") {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (assetPath.endsWith(".particle")) {
                        openEditor(new Particle2DTopComponent());
                    } else if (assetPath.endsWith(".glsl")) {
                        openEditor(new ShaderEditorTopComponent());
                    } else if (assetPath.endsWith(".json") && assetPath.contains("skin")) {
                        openEditor(new SkinComposerTopComponent());
                    } else if (assetPath.endsWith(".9.png")) {
                        openEditor(new NinePatchEditorTopComponent());
                    } else if (assetPath.endsWith("level1.json")) {
                        openEditor(new Scene2DTopComponent());
                    } else if (assetPath.endsWith(".gltf")) {
                        openEditor(new Model3DViewerTopComponent());
                    } else if (assetPath.endsWith(".ogg") || assetPath.endsWith(".wav")) {
                        openEditor(new MediaViewerTopComponent());
                    }
                }
            };
        }

        @Override
        public Action[] getActions(boolean context) {
            return new Action[]{getPreferredAction()};
        }
    }

    private static class CategoryNode extends AbstractNode {
        CategoryNode(String name, String desc, String[] items) {
            super(new CategoryChildren(items));
            setDisplayName(name);
            setShortDescription(desc);
        }
    }

    private static class CategoryChildren extends Children.Keys<String> {
        private final String[] items;

        CategoryChildren(String[] items) {
            this.items = items;
        }

        @Override
        protected void addNotify() {
            setKeys(items);
        }

        @Override
        protected Node[] createNodes(String key) {
            AbstractNode leaf = new AbstractNode(Children.LEAF);
            leaf.setDisplayName(key);
            return new Node[]{leaf};
        }
    }
}
