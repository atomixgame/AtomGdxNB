package com.atomgdx.viewer3d;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Descriptor and metadata inspector for 3D Models (.g3db, .g3dj, .gltf, .glb, .obj).
 */
public class Model3DDescriptor implements Serializable {
    private static final long serialVersionUID = 1L;

    public enum Format {
        G3DB,
        G3DJ,
        GLTF,
        GLB,
        OBJ,
        UNKNOWN
    }

    private final File modelFile;
    private final Format format;
    private int meshCount = 0;
    private int nodeCount = 0;
    private int materialCount = 0;
    private int animationCount = 0;
    private final List<String> animationNames = new ArrayList<>();
    private final List<String> materialNames = new ArrayList<>();

    // Viewport Display Options
    private boolean wireframe = false;
    private boolean showNormals = false;
    private boolean showGrid = true;
    private boolean showBones = false;
    private boolean enablePbr = true;
    private float cameraDistance = 5.0f;
    private float animationSpeed = 1.0f;

    public Model3DDescriptor(File modelFile) {
        this.modelFile = modelFile;
        this.format = detectFormat(modelFile);
    }

    private Format detectFormat(File file) {
        if (file == null) return Format.UNKNOWN;
        String name = file.getName().toLowerCase();
        if (name.endsWith(".g3db")) return Format.G3DB;
        if (name.endsWith(".g3dj")) return Format.G3DJ;
        if (name.endsWith(".gltf")) return Format.GLTF;
        if (name.endsWith(".glb")) return Format.GLB;
        if (name.endsWith(".obj")) return Format.OBJ;
        return Format.UNKNOWN;
    }

    public File getModelFile() { return modelFile; }
    public Format getFormat() { return format; }

    public int getMeshCount() { return meshCount; }
    public void setMeshCount(int meshCount) { this.meshCount = meshCount; }

    public int getNodeCount() { return nodeCount; }
    public void setNodeCount(int nodeCount) { this.nodeCount = nodeCount; }

    public int getMaterialCount() { return materialCount; }
    public void setMaterialCount(int materialCount) { this.materialCount = materialCount; }

    public int getAnimationCount() { return animationCount; }
    public void setAnimationCount(int animationCount) { this.animationCount = animationCount; }

    public List<String> getAnimationNames() { return Collections.unmodifiableList(animationNames); }
    public void addAnimationName(String name) { animationNames.add(name); }

    public List<String> getMaterialNames() { return Collections.unmodifiableList(materialNames); }
    public void addMaterialName(String name) { materialNames.add(name); }

    public boolean isWireframe() { return wireframe; }
    public void setWireframe(boolean wireframe) { this.wireframe = wireframe; }

    public boolean isShowNormals() { return showNormals; }
    public void setShowNormals(boolean showNormals) { this.showNormals = showNormals; }

    public boolean isShowGrid() { return showGrid; }
    public void setShowGrid(boolean showGrid) { this.showGrid = showGrid; }

    public boolean isShowBones() { return showBones; }
    public void setShowBones(boolean showBones) { this.showBones = showBones; }

    public boolean isEnablePbr() { return enablePbr; }
    public void setEnablePbr(boolean enablePbr) { this.enablePbr = enablePbr; }

    public float getCameraDistance() { return cameraDistance; }
    public void setCameraDistance(float cameraDistance) { this.cameraDistance = cameraDistance; }

    public float getAnimationSpeed() { return animationSpeed; }
    public void setAnimationSpeed(float animationSpeed) { this.animationSpeed = animationSpeed; }
}
