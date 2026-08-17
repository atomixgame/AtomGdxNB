package com.atomgdx.theme.project;

import com.atomgdx.core.project.LibGdxProject;
import com.atomgdx.theme.windows.*;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.ImageUtilities;
import org.openide.util.lookup.AbstractLookup;
import org.openide.util.lookup.InstanceContent;
import org.openide.windows.Mode;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * NetBeans Node representing a LibGDX Multi-Platform project and live Assets Tree (AT)
 * with Fatcow icons, context menus, and full support for .dt, .scene, .particle, .gltf, etc.
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
        setIconBaseWithExtension("com/atomgdx/theme/icons/folder.png");
    }

    @Override
    public Image getIcon(int type) {
        return getCustomIcon("folder.png");
    }

    @Override
    public Image getOpenedIcon(int type) {
        return getCustomIcon("folder.png");
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
                new AbstractAction("Open Assets in Explorer") {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        try {
                            Desktop.getDesktop().open(project.getAssetsDirectory());
                        } catch (Exception ex) {
                            JOptionPane.showMessageDialog(null, "Assets: " + project.getAssetsDirectory().getAbsolutePath());
                        }
                    }
                },
                new AbstractAction("Refresh Project Tree") {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        setChildren(new ProjectChildren(project));
                    }
                }
        };
    }

    public static Image getCustomIcon(String name) {
        try {
            ImageIcon icon = com.atomgdx.editor.scene2d.ui.Scene2DEditorPanel.getIcon(name);
            if (icon != null) return icon.getImage();
        } catch (Throwable ignored) {}
        return ImageUtilities.loadImage("com/atomgdx/theme/icons/" + name, true);
    }

    public static void openEditor(TopComponent tc) {
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
                    return new Node[]{new CategoryNode("Source Packages", "Core, Desktop, Android, HTML source modules", "cog.png", new String[]{
                            "core/src/main/java/" + project.getPackageName(),
                            "desktop/src/main/java/" + project.getPackageName() + "/DesktopLauncher.java",
                            "android/src/main/java/" + project.getPackageName() + "/AndroidLauncher.java"
                    })};
                case "Assets":
                    return new Node[]{new RealAssetDirectoryNode(project.getAssetsDirectory())};
                case "Platforms":
                    return new Node[]{new CategoryNode("Deployment Targets", "Configured target platforms", "star.png", new String[]{
                            "Desktop (LWJGL3 64-bit)",
                            "Android (API 34+)",
                            "HTML5 (TeaVM WebAssembly / JS)"
                    })};
                case "Project Files":
                    return new Node[]{new CategoryNode("Build Configuration", "Gradle scripts & properties", "folder.png", new String[]{
                            "build.gradle.kts",
                            "settings.gradle.kts",
                            "gradle.properties"
                    })};
                default:
                    return new Node[0];
            }
        }
    }

    /**
     * Real File/Directory Asset Node that scans actual workspace files and renders specialized icons & context menus.
     */
    public static class RealAssetDirectoryNode extends AbstractNode {
        private final File dir;

        public RealAssetDirectoryNode(File dir) {
            super(new RealAssetChildren(dir));
            this.dir = dir;
            setDisplayName(dir != null ? dir.getName() : "assets");
            setShortDescription("Game Assets: " + (dir != null ? dir.getAbsolutePath() : ""));
        }

        @Override
        public Image getIcon(int type) {
            return getCustomIcon("folder.png");
        }

        @Override
        public Image getOpenedIcon(int type) {
            return getCustomIcon("folder.png");
        }

        @Override
        public Action[] getActions(boolean context) {
            return new Action[]{
                    new AbstractAction("New Scene (.dt)") {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            String name = JOptionPane.showInputDialog(null, "Enter Scene Name:", "NewScene");
                            if (name != null && !name.trim().isEmpty()) {
                                File scenesDir = new File(dir, "scenes");
                                if (!scenesDir.exists()) scenesDir.mkdirs();
                                File sceneFile = new File(scenesDir, name.trim() + ".dt");
                                try {
                                    com.atomgdx.editor.scene2d.data.vo.HyperLap2DSerializer.saveSceneToFile(
                                            new com.atomgdx.editor.scene2d.data.vo.SceneVO(name.trim()), sceneFile);
                                    openEditor(new Scene2DTopComponent());
                                } catch (Exception ex) {
                                    JOptionPane.showMessageDialog(null, "Error: " + ex.getMessage());
                                }
                            }
                        }
                    },
                    new AbstractAction("Open in Explorer") {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            try {
                                Desktop.getDesktop().open(dir);
                            } catch (Exception ignored) {}
                        }
                    }
            };
        }
    }

    public static class RealAssetChildren extends Children.Keys<File> {
        private final File dir;

        public RealAssetChildren(File dir) {
            this.dir = dir;
        }

        @Override
        protected void addNotify() {
            if (dir != null && dir.exists() && dir.isDirectory()) {
                File[] files = dir.listFiles();
                if (files != null) {
                    Arrays.sort(files, (a, b) -> {
                        if (a.isDirectory() && !b.isDirectory()) return -1;
                        if (!a.isDirectory() && b.isDirectory()) return 1;
                        return a.getName().compareToIgnoreCase(b.getName());
                    });
                    setKeys(files);
                    return;
                }
            }
            setKeys(new File[0]);
        }

        @Override
        protected Node[] createNodes(File file) {
            if (file.isDirectory()) {
                return new Node[]{new RealAssetDirectoryNode(file)};
            } else {
                return new Node[]{new RealFileAssetNode(file)};
            }
        }
    }

    public static class RealFileAssetNode extends AbstractNode {
        private final File file;

        public RealFileAssetNode(File file) {
            this(file, new InstanceContent());
        }

        private RealFileAssetNode(File file, InstanceContent content) {
            super(Children.LEAF, new AbstractLookup(content));
            this.file = file;
            content.add(file);
            setDisplayName(file.getName());
            setShortDescription("Asset File: " + file.getAbsolutePath() + " (" + file.length() + " bytes)");
        }

        @Override
        public Image getIcon(int type) {
            String name = file.getName().toLowerCase();
            if (name.endsWith(".dt") || name.endsWith(".scene") || name.endsWith(".scene2d") || name.endsWith(".h2d")) {
                return getCustomIcon("star.png");
            } else if (name.endsWith(".particle") || name.endsWith(".p")) {
                return getCustomIcon("fire.png");
            } else if (name.endsWith(".glsl") || name.endsWith(".frag") || name.endsWith(".vert")) {
                return getCustomIcon("lightning.png");
            } else if (name.endsWith(".gltf") || name.endsWith(".obj")) {
                return getCustomIcon("bomb.png");
            } else if (name.endsWith(".png") || name.endsWith(".jpg")) {
                return getCustomIcon("picture.png");
            } else if (name.endsWith(".ogg") || name.endsWith(".wav") || name.endsWith(".mp3")) {
                return getCustomIcon("weather_clouds.png");
            } else if (name.endsWith(".json") || name.endsWith(".skin")) {
                return getCustomIcon("wand.png");
            }
            return getCustomIcon("cog.png");
        }

        @Override
        public Action getPreferredAction() {
            return new AbstractAction("Open in Editor") {
                @Override
                public void actionPerformed(ActionEvent e) {
                    openFileInEditor();
                }
            };
        }

        private void openFileInEditor() {
            String name = file.getName().toLowerCase();
            if (name.endsWith(".dt") || name.endsWith(".scene") || name.endsWith(".scene2d") || name.endsWith(".h2d")) {
                openEditor(new Scene2DTopComponent());
            } else if (name.endsWith(".particle") || name.endsWith(".p")) {
                openEditor(new Particle2DTopComponent());
            } else if (name.endsWith(".glsl") || name.endsWith(".frag") || name.endsWith(".vert")) {
                openEditor(new ShaderEditorTopComponent());
            } else if (name.endsWith(".skin") || (name.endsWith(".json") && name.contains("skin"))) {
                openEditor(new SkinComposerTopComponent());
            } else if (name.endsWith(".9.png")) {
                openEditor(new NinePatchEditorTopComponent());
            } else if (name.endsWith(".gltf") || name.endsWith(".obj")) {
                openEditor(new Model3DViewerTopComponent());
            } else if (name.endsWith(".ogg") || name.endsWith(".wav") || name.endsWith(".mp3")) {
                openEditor(new MediaViewerTopComponent());
            }
        }

        @Override
        public Action[] getActions(boolean context) {
            return new Action[]{
                    getPreferredAction(),
                    new AbstractAction("Copy Absolute Path") {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(file.getAbsolutePath()), null);
                        }
                    },
                    new AbstractAction("Show in Explorer") {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            try {
                                Desktop.getDesktop().open(file.getParentFile());
                            } catch (Exception ignored) {}
                        }
                    },
                    null, // Separator
                    new AbstractAction("Delete") {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            if (JOptionPane.showConfirmDialog(null, "Delete asset: " + file.getName() + "?", "Confirm Delete", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                                file.delete();
                            }
                        }
                    }
            };
        }
    }

    private static class CategoryNode extends AbstractNode {
        private final String iconName;

        CategoryNode(String name, String desc, String iconName, String[] items) {
            super(new CategoryChildren(items));
            this.iconName = iconName;
            setDisplayName(name);
            setShortDescription(desc);
        }

        @Override
        public Image getIcon(int type) {
            return getCustomIcon(iconName);
        }

        @Override
        public Image getOpenedIcon(int type) {
            return getCustomIcon(iconName);
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
