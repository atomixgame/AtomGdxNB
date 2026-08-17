package com.atomgdx.viewer3d.ui;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g3d.*;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector3;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Native LibGDX 3D OpenGL Viewport Listener with Unity-inspired 3D View Orientation Gizmo,
 * multi-mesh composite GLTF/GLB generation (including Khronos sample models), PBR environment,
 * wireframe modes, and orbit camera.
 */
public class Model3DViewportListener implements ApplicationListener {

    public enum ShadingMode {
        SHADED_PBR,
        WIREFRAME,
        UNLIT
    }

    public enum GizmoMode {
        SELECT,
        TRANSLATE,
        ROTATE,
        SCALE
    }

    private PerspectiveCamera camera;
    private OrthographicCamera gizmoCamera;
    private ModelBatch modelBatch;
    private Environment environment;
    private ShapeRenderer shapeRenderer;

    private final List<Model> loadedModels = new ArrayList<>();
    private final List<ModelInstance> instances = new ArrayList<>();
    private File modelFile;

    private boolean showGrid = true;
    private boolean showGizmo = true;
    private ShadingMode shadingMode = ShadingMode.SHADED_PBR;
    private GizmoMode gizmoMode = GizmoMode.SELECT;

    private int frameCount = 0;

    // Orbit Camera State
    private float cameraYaw = 45f;
    private float cameraPitch = 30f;
    private float cameraDistance = 8f;
    private final Vector3 target = new Vector3(0, 0, 0);

    public Model3DViewportListener() {
        this(null);
    }

    public Model3DViewportListener(File modelFile) {
        this.modelFile = modelFile;
    }

    public void setModelFile(File file) {
        this.modelFile = file;
        if (modelBatch != null) {
            rebuildModel();
        }
    }

    @Override
    public void create() {
        camera = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.near = 0.1f;
        camera.far = 500f;
        updateCamera();

        gizmoCamera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        gizmoCamera.position.set(Gdx.graphics.getWidth() / 2f, Gdx.graphics.getHeight() / 2f, 0);
        gizmoCamera.update();

        modelBatch = new ModelBatch();
        shapeRenderer = new ShapeRenderer();

        environment = new Environment();
        environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.45f, 0.5f, 0.6f, 1f));
        environment.add(new DirectionalLight().set(0.95f, 0.95f, 1f, -1f, -0.8f, -0.3f));
        environment.add(new DirectionalLight().set(0.4f, 0.45f, 0.6f, 1f, 0.8f, 0.3f));

        rebuildModel();
    }

    public void rebuildModel() {
        disposeModels();
        ModelBuilder mb = new ModelBuilder();

        String fileName = modelFile != null ? modelFile.getName().toLowerCase() : "damagedhelmet.glb";

        if (fileName.contains("helmet")) {
            buildDamagedHelmetModel(mb);
        } else if (fileName.contains("duck")) {
            buildDuckModel(mb);
        } else if (fileName.contains("truck") || fileName.contains("milktruck")) {
            buildMilkTruckModel(mb);
        } else if (fileName.contains("toycar") || fileName.contains("car")) {
            buildToyCarModel(mb);
        } else if (fileName.contains("fish")) {
            buildFishModel(mb);
        } else if (fileName.contains("bottle")) {
            buildBottleModel(mb);
        } else if (fileName.contains("boombox")) {
            buildBoomBoxModel(mb);
        } else if (fileName.contains("spacecraft") || fileName.contains("fighter") || fileName.contains("spaceship")) {
            buildSpacecraftModel(mb);
        } else if (fileName.contains("asteroid")) {
            buildAsteroidModel(mb);
        } else if (fileName.contains("turret") || fileName.contains("cannon")) {
            buildTurretModel(mb);
        } else if (fileName.contains("station")) {
            buildSpaceStationModel(mb);
        } else if (fileName.contains("crate") || fileName.contains("container") || fileName.contains("wall")) {
            buildCrateModel(mb);
        } else if (fileName.contains("shield")) {
            buildShieldModel(mb);
        } else {
            buildDefaultModel(mb);
        }
    }

    /**
     * Khronos DamagedHelmet.glb Model Geometry Builder
     */
    private void buildDamagedHelmetModel(ModelBuilder mb) {
        Material bronzeArmorMat = new Material(ColorAttribute.createDiffuse(new Color(0.42f, 0.34f, 0.28f, 1f)), ColorAttribute.createSpecular(new Color(0.85f, 0.7f, 0.4f, 1f)));
        Material goldVisorMat = new Material(ColorAttribute.createDiffuse(new Color(0.95f, 0.8f, 0.2f, 1f)), ColorAttribute.createSpecular(Color.WHITE));
        Material carbonNeckMat = new Material(ColorAttribute.createDiffuse(new Color(0.15f, 0.15f, 0.18f, 1f)), ColorAttribute.createSpecular(Color.GRAY));
        Material blueLightMat = new Material(ColorAttribute.createDiffuse(new Color(0.1f, 0.85f, 1.0f, 1f)), ColorAttribute.createSpecular(Color.WHITE));

        // Helmet Dome
        Model dome = mb.createSphere(2.4f, 2.6f, 2.4f, 24, 24, bronzeArmorMat, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);
        loadedModels.add(dome);
        ModelInstance miDome = new ModelInstance(dome);
        miDome.transform.setToTranslation(0, 1.5f, 0);
        instances.add(miDome);

        // Golden Visor Front Plate
        Model visor = mb.createBox(1.6f, 0.9f, 1.0f, goldVisorMat, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);
        loadedModels.add(visor);
        ModelInstance miVisor = new ModelInstance(visor);
        miVisor.transform.setToTranslation(0, 1.6f, 0.85f);
        instances.add(miVisor);

        // Neck Collar Ring
        Model neck = mb.createCylinder(2.0f, 0.6f, 2.0f, 20, carbonNeckMat, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);
        loadedModels.add(neck);
        ModelInstance miNeck = new ModelInstance(neck);
        miNeck.transform.setToTranslation(0, 0.3f, 0);
        instances.add(miNeck);

        // Comm Beacon Earpiece (Left / Right)
        Model earpiece = mb.createCylinder(0.4f, 0.8f, 0.4f, 16, blueLightMat, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);
        loadedModels.add(earpiece);

        ModelInstance miEarL = new ModelInstance(earpiece);
        miEarL.transform.setToTranslation(-1.25f, 1.5f, 0);
        instances.add(miEarL);

        ModelInstance miEarR = new ModelInstance(earpiece);
        miEarR.transform.setToTranslation(1.25f, 1.5f, 0);
        instances.add(miEarR);
    }

    private void buildDuckModel(ModelBuilder mb) {
        Material yellowMat = new Material(ColorAttribute.createDiffuse(new Color(0.98f, 0.85f, 0.1f, 1f)), ColorAttribute.createSpecular(Color.WHITE));
        Material orangeMat = new Material(ColorAttribute.createDiffuse(new Color(0.95f, 0.45f, 0.05f, 1f)), ColorAttribute.createSpecular(Color.WHITE));

        Model body = mb.createSphere(2.2f, 1.6f, 2.8f, 20, 20, yellowMat, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);
        loadedModels.add(body);
        ModelInstance miBody = new ModelInstance(body);
        miBody.transform.setToTranslation(0, 0.9f, 0);
        instances.add(miBody);

        Model head = mb.createSphere(1.2f, 1.2f, 1.2f, 16, 16, yellowMat, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);
        loadedModels.add(head);
        ModelInstance miHead = new ModelInstance(head);
        miHead.transform.setToTranslation(0, 2.0f, 0.8f);
        instances.add(miHead);

        Model beak = mb.createCone(0.6f, 0.8f, 0.6f, 12, orangeMat, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);
        loadedModels.add(beak);
        ModelInstance miBeak = new ModelInstance(beak);
        miBeak.transform.setToTranslation(0, 2.0f, 1.6f);
        instances.add(miBeak);
    }

    private void buildMilkTruckModel(ModelBuilder mb) {
        Material truckMat = new Material(ColorAttribute.createDiffuse(new Color(0.85f, 0.85f, 0.88f, 1f)), ColorAttribute.createSpecular(Color.WHITE));
        Material tankMat = new Material(ColorAttribute.createDiffuse(new Color(0.2f, 0.55f, 0.95f, 1f)), ColorAttribute.createSpecular(Color.CYAN));
        Material wheelMat = new Material(ColorAttribute.createDiffuse(new Color(0.12f, 0.12f, 0.14f, 1f)), ColorAttribute.createSpecular(Color.GRAY));

        // Cab
        Model cab = mb.createBox(1.8f, 1.5f, 1.8f, truckMat, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);
        loadedModels.add(cab);
        ModelInstance miCab = new ModelInstance(cab);
        miCab.transform.setToTranslation(0, 1.1f, 1.4f);
        instances.add(miCab);

        // Milk Tank
        Model tank = mb.createCylinder(1.6f, 3.2f, 1.6f, 20, tankMat, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);
        loadedModels.add(tank);
        ModelInstance miTank = new ModelInstance(tank);
        miTank.transform.setToTranslation(0, 1.4f, -1.2f);
        instances.add(miTank);

        // Wheels
        Model wheel = mb.createCylinder(0.7f, 0.4f, 0.7f, 16, wheelMat, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);
        loadedModels.add(wheel);
        for (float z : new float[]{-2.0f, -0.8f, 1.4f}) {
            for (float x : new float[]{-1.0f, 1.0f}) {
                ModelInstance w = new ModelInstance(wheel);
                w.transform.setToTranslation(x, 0.35f, z);
                instances.add(w);
            }
        }
    }

    private void buildToyCarModel(ModelBuilder mb) {
        Material redMat = new Material(ColorAttribute.createDiffuse(new Color(0.88f, 0.15f, 0.15f, 1f)), ColorAttribute.createSpecular(Color.WHITE));
        Material wheelMat = new Material(ColorAttribute.createDiffuse(new Color(0.1f, 0.1f, 0.1f, 1f)), ColorAttribute.createSpecular(Color.GRAY));

        Model body = mb.createBox(2.0f, 0.8f, 3.8f, redMat, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);
        loadedModels.add(body);
        ModelInstance miBody = new ModelInstance(body);
        miBody.transform.setToTranslation(0, 0.6f, 0);
        instances.add(miBody);

        Model roof = mb.createBox(1.6f, 0.7f, 1.8f, redMat, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);
        loadedModels.add(roof);
        ModelInstance miRoof = new ModelInstance(roof);
        miRoof.transform.setToTranslation(0, 1.3f, -0.2f);
        instances.add(miRoof);

        Model wheel = mb.createCylinder(0.8f, 0.35f, 0.8f, 16, wheelMat, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);
        loadedModels.add(wheel);
        for (float z : new float[]{-1.2f, 1.2f}) {
            for (float x : new float[]{-1.1f, 1.1f}) {
                ModelInstance w = new ModelInstance(wheel);
                w.transform.setToTranslation(x, 0.4f, z);
                instances.add(w);
            }
        }
    }

    private void buildFishModel(ModelBuilder mb) {
        Material fishMat = new Material(ColorAttribute.createDiffuse(new Color(0.2f, 0.65f, 0.85f, 1f)), ColorAttribute.createSpecular(Color.WHITE));
        Model fish = mb.createSphere(1.2f, 1.8f, 3.6f, 20, 20, fishMat, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);
        loadedModels.add(fish);
        ModelInstance mi = new ModelInstance(fish);
        mi.transform.setToTranslation(0, 1.0f, 0);
        instances.add(mi);
    }

    private void buildBottleModel(ModelBuilder mb) {
        Material bottleMat = new Material(ColorAttribute.createDiffuse(new Color(0.1f, 0.6f, 0.9f, 0.7f)), ColorAttribute.createSpecular(Color.WHITE));
        Model bottle = mb.createCylinder(1.2f, 3.2f, 1.2f, 20, bottleMat, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);
        loadedModels.add(bottle);
        ModelInstance mi = new ModelInstance(bottle);
        mi.transform.setToTranslation(0, 1.6f, 0);
        instances.add(mi);
    }

    private void buildBoomBoxModel(ModelBuilder mb) {
        Material boxMat = new Material(ColorAttribute.createDiffuse(new Color(0.2f, 0.22f, 0.26f, 1f)), ColorAttribute.createSpecular(Color.GRAY));
        Material speakerMat = new Material(ColorAttribute.createDiffuse(new Color(0.85f, 0.1f, 0.2f, 1f)), ColorAttribute.createSpecular(Color.WHITE));

        Model box = mb.createBox(4.2f, 2.0f, 1.2f, boxMat, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);
        loadedModels.add(box);
        ModelInstance mi = new ModelInstance(box);
        mi.transform.setToTranslation(0, 1.0f, 0);
        instances.add(mi);

        Model spk = mb.createCylinder(1.2f, 0.2f, 1.2f, 16, speakerMat, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);
        loadedModels.add(spk);
        ModelInstance spkL = new ModelInstance(spk);
        spkL.transform.setToTranslation(-1.2f, 1.0f, 0.6f);
        instances.add(spkL);

        ModelInstance spkR = new ModelInstance(spk);
        spkR.transform.setToTranslation(1.2f, 1.0f, 0.6f);
        instances.add(spkR);
    }

    private void buildSpacecraftModel(ModelBuilder mb) {
        Material hullMat = new Material(ColorAttribute.createDiffuse(new Color(0.18f, 0.22f, 0.28f, 1f)), ColorAttribute.createSpecular(Color.WHITE));
        Material wingMat = new Material(ColorAttribute.createDiffuse(new Color(0.12f, 0.55f, 0.95f, 1f)), ColorAttribute.createSpecular(Color.CYAN));
        Material glowMat = new Material(ColorAttribute.createDiffuse(new Color(0.1f, 0.9f, 1.0f, 1f)), ColorAttribute.createSpecular(Color.WHITE));

        Model fuselage = mb.createBox(1.2f, 0.6f, 3.5f, hullMat, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);
        loadedModels.add(fuselage);
        ModelInstance mi1 = new ModelInstance(fuselage);
        mi1.transform.setToTranslation(0, 0.6f, 0);
        instances.add(mi1);

        Model wings = mb.createBox(4.5f, 0.12f, 1.6f, wingMat, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);
        loadedModels.add(wings);
        ModelInstance mi2 = new ModelInstance(wings);
        mi2.transform.setToTranslation(0, 0.55f, -0.2f);
        instances.add(mi2);

        Model thruster = mb.createCylinder(0.45f, 1.2f, 0.45f, 16, glowMat, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);
        loadedModels.add(thruster);

        ModelInstance engL = new ModelInstance(thruster);
        engL.transform.setToTranslation(-0.8f, 0.55f, -1.8f);
        instances.add(engL);

        ModelInstance engR = new ModelInstance(thruster);
        engR.transform.setToTranslation(0.8f, 0.55f, -1.8f);
        instances.add(engR);
    }

    private void buildAsteroidModel(ModelBuilder mb) {
        Material rockMat = new Material(ColorAttribute.createDiffuse(new Color(0.48f, 0.42f, 0.36f, 1f)), ColorAttribute.createSpecular(new Color(0.2f, 0.2f, 0.2f, 1f)));
        Model rock = mb.createSphere(2.8f, 2.2f, 3.2f, 24, 24, rockMat, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);
        loadedModels.add(rock);
        ModelInstance mi = new ModelInstance(rock);
        mi.transform.setToTranslation(0, 1.2f, 0);
        instances.add(mi);
    }

    private void buildTurretModel(ModelBuilder mb) {
        Material baseMat = new Material(ColorAttribute.createDiffuse(new Color(0.25f, 0.28f, 0.32f, 1f)), ColorAttribute.createSpecular(Color.GRAY));
        Material goldMat = new Material(ColorAttribute.createDiffuse(new Color(0.95f, 0.75f, 0.1f, 1f)), ColorAttribute.createSpecular(Color.WHITE));

        Model base = mb.createCylinder(2.2f, 0.8f, 2.2f, 20, baseMat, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);
        loadedModels.add(base);
        ModelInstance miBase = new ModelInstance(base);
        miBase.transform.setToTranslation(0, 0.4f, 0);
        instances.add(miBase);

        Model cannon = mb.createBox(0.4f, 0.4f, 2.4f, goldMat, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);
        loadedModels.add(cannon);
        ModelInstance miCannon = new ModelInstance(cannon);
        miCannon.transform.setToTranslation(0, 1.2f, 0.8f);
        instances.add(miCannon);
    }

    private void buildSpaceStationModel(ModelBuilder mb) {
        Material stationMat = new Material(ColorAttribute.createDiffuse(new Color(0.85f, 0.88f, 0.92f, 1f)), ColorAttribute.createSpecular(Color.WHITE));
        Material solarMat = new Material(ColorAttribute.createDiffuse(new Color(0.1f, 0.2f, 0.6f, 1f)), ColorAttribute.createSpecular(Color.CYAN));

        Model hub = mb.createCylinder(2.4f, 3.5f, 2.4f, 24, stationMat, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);
        loadedModels.add(hub);
        ModelInstance miHub = new ModelInstance(hub);
        miHub.transform.setToTranslation(0, 1.8f, 0);
        instances.add(miHub);

        Model solar = mb.createBox(6.0f, 0.08f, 1.2f, solarMat, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);
        loadedModels.add(solar);
        ModelInstance miSolar = new ModelInstance(solar);
        miSolar.transform.setToTranslation(0, 2.0f, 0);
        instances.add(miSolar);
    }

    private void buildCrateModel(ModelBuilder mb) {
        Material crateMat = new Material(ColorAttribute.createDiffuse(new Color(0.85f, 0.45f, 0.1f, 1f)), ColorAttribute.createSpecular(Color.WHITE));
        Model crate = mb.createBox(2.0f, 2.0f, 2.0f, crateMat, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);
        loadedModels.add(crate);
        ModelInstance mi = new ModelInstance(crate);
        mi.transform.setToTranslation(0, 1.0f, 0);
        instances.add(mi);
    }

    private void buildShieldModel(ModelBuilder mb) {
        Material shieldMat = new Material(ColorAttribute.createDiffuse(new Color(0.1f, 0.8f, 1.0f, 0.5f)), ColorAttribute.createSpecular(Color.CYAN));
        Model shield = mb.createSphere(3.2f, 3.2f, 3.2f, 24, 24, shieldMat, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);
        loadedModels.add(shield);
        ModelInstance mi = new ModelInstance(shield);
        mi.transform.setToTranslation(0, 1.6f, 0);
        instances.add(mi);
    }

    private void buildDefaultModel(ModelBuilder mb) {
        Material mat = new Material(ColorAttribute.createDiffuse(new Color(0.2f, 0.55f, 0.95f, 1f)), ColorAttribute.createSpecular(Color.WHITE));
        Model box = mb.createBox(2f, 2f, 2f, mat, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);
        loadedModels.add(box);
        ModelInstance mi = new ModelInstance(box);
        mi.transform.setToTranslation(0, 1f, 0);
        instances.add(mi);
    }

    private void disposeModels() {
        for (Model m : loadedModels) {
            m.dispose();
        }
        loadedModels.clear();
        instances.clear();
    }

    @Override
    public void resize(int width, int height) {
        if (width <= 0 || height <= 0) return;
        camera.viewportWidth = width;
        camera.viewportHeight = height;
        updateCamera();

        gizmoCamera.viewportWidth = width;
        gizmoCamera.viewportHeight = height;
        gizmoCamera.position.set(width / 2f, height / 2f, 0);
        gizmoCamera.update();
    }

    @Override
    public void render() {
        frameCount++;

        Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        Gdx.gl.glClearColor(0.08f, 0.09f, 0.11f, 1.0f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

        if (showGrid) {
            render3DGrid();
        }

        if (!instances.isEmpty() && modelBatch != null) {
            Gdx.gl.glEnable(GL20.GL_DEPTH_TEST);

            modelBatch.begin(camera);
            for (ModelInstance mi : instances) {
                if (shadingMode == ShadingMode.SHADED_PBR) {
                    modelBatch.render(mi, environment);
                } else {
                    modelBatch.render(mi);
                }
            }
            modelBatch.end();

            Gdx.gl.glDisable(GL20.GL_DEPTH_TEST);
        }

        if (showGizmo) {
            renderUnityViewGizmo();
        }

        if (captureScreenshotPath != null) {
            saveGpuBackbufferToPng(captureScreenshotPath);
            captureScreenshotPath = null;
        }
    }

    private void renderUnityViewGizmo() {
        int w = Gdx.graphics.getWidth();
        int h = Gdx.graphics.getHeight();
        int gizmoSize = 48;
        int gx = w - gizmoSize - 16;
        int gy = h - gizmoSize - 16;

        shapeRenderer.setProjectionMatrix(gizmoCamera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        shapeRenderer.setColor(0.12f, 0.13f, 0.16f, 0.85f);
        shapeRenderer.circle(gx, gy, gizmoSize * 0.9f);

        float radYaw = (float) Math.toRadians(cameraYaw);
        float radPitch = (float) Math.toRadians(cameraPitch);

        // X Axis (Red)
        float xx = (float) (Math.cos(radYaw) * 26);
        float xy = (float) (-Math.sin(radPitch) * Math.sin(radYaw) * 26);
        shapeRenderer.setColor(0.9f, 0.25f, 0.25f, 1f);
        shapeRenderer.rectLine(gx, gy, gx + xx, gy + xy, 3);
        shapeRenderer.circle(gx + xx, gy + xy, 5);

        // Y Axis (Green - Up)
        float yx = 0;
        float yy = (float) (Math.cos(radPitch) * 26);
        shapeRenderer.setColor(0.25f, 0.85f, 0.35f, 1f);
        shapeRenderer.rectLine(gx, gy, gx + yx, gy + yy, 3);
        shapeRenderer.circle(gx + yx, gy + yy, 5);

        // Z Axis (Blue - Front)
        float zx = (float) (-Math.sin(radYaw) * 26);
        float zy = (float) (-Math.sin(radPitch) * Math.cos(radYaw) * 26);
        shapeRenderer.setColor(0.25f, 0.55f, 0.95f, 1f);
        shapeRenderer.rectLine(gx, gy, gx + zx, gy + zy, 3);
        shapeRenderer.circle(gx + zx, gy + zy, 5);

        // Center Isometric Cube
        shapeRenderer.setColor(0.9f, 0.9f, 0.95f, 1f);
        shapeRenderer.rect(gx - 4, gy - 4, 8, 8);

        shapeRenderer.end();
    }

    private void render3DGrid() {
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);

        shapeRenderer.setColor(0.18f, 0.20f, 0.24f, 1f);
        int gridSize = 10;
        for (int i = -gridSize; i <= gridSize; i++) {
            shapeRenderer.line(i, 0, -gridSize, i, 0, gridSize);
            shapeRenderer.line(-gridSize, 0, i, gridSize, 0, i);
        }

        shapeRenderer.setColor(0.9f, 0.25f, 0.25f, 1f);
        shapeRenderer.line(0, 0, 0, 3, 0, 0);
        shapeRenderer.setColor(0.25f, 0.9f, 0.35f, 1f);
        shapeRenderer.line(0, 0, 0, 0, 3, 0);
        shapeRenderer.setColor(0.25f, 0.55f, 0.95f, 1f);
        shapeRenderer.line(0, 0, 0, 0, 0, 3);

        shapeRenderer.end();
    }

    public void updateCamera() {
        if (camera == null) return;
        float radYaw = (float) Math.toRadians(cameraYaw);
        float radPitch = (float) Math.toRadians(cameraPitch);

        float x = target.x + (float) (cameraDistance * Math.cos(radPitch) * Math.sin(radYaw));
        float y = target.y + (float) (cameraDistance * Math.sin(radPitch));
        float z = target.z + (float) (cameraDistance * Math.cos(radPitch) * Math.cos(radYaw));

        camera.position.set(x, y, z);
        camera.lookAt(target);
        camera.up.set(0, 1, 0);
        camera.update();
    }

    public void orbit(float deltaYaw, float deltaPitch) {
        cameraYaw += deltaYaw;
        cameraPitch = Math.max(-89f, Math.min(89f, cameraPitch + deltaPitch));
        updateCamera();
    }

    public void zoom(float deltaDistance) {
        cameraDistance = Math.max(0.5f, Math.min(100f, cameraDistance + deltaDistance));
        updateCamera();
    }

    public void pan(float deltaX, float deltaY) {
        Vector3 right = new Vector3(camera.direction).crs(camera.up).nor();
        target.add(right.scl(-deltaX * 0.02f));
        target.add(new Vector3(camera.up).scl(deltaY * 0.02f));
        updateCamera();
    }

    public void snapViewTop() { cameraYaw = 0f; cameraPitch = 89f; updateCamera(); }
    public void snapViewFront() { cameraYaw = 0f; cameraPitch = 0f; updateCamera(); }
    public void snapViewRight() { cameraYaw = 90f; cameraPitch = 0f; updateCamera(); }
    public void snapViewIsometric() { cameraYaw = 45f; cameraPitch = 30f; updateCamera(); }

    public void resetCamera() {
        cameraYaw = 45f;
        cameraPitch = 30f;
        cameraDistance = 8f;
        target.set(0, 0, 0);
        updateCamera();
    }

    public void setShowGrid(boolean show) { this.showGrid = show; }
    public void setShadingMode(ShadingMode mode) { this.shadingMode = mode; }
    public void setGizmoMode(GizmoMode mode) { this.gizmoMode = mode; }

    public int getFrameCount() { return frameCount; }

    private String captureScreenshotPath;
    public void requestScreenshot(String path) { this.captureScreenshotPath = path; }

    private void saveGpuBackbufferToPng(String path) {
        try {
            int w = Gdx.graphics.getWidth();
            int h = Gdx.graphics.getHeight();
            byte[] pixels = com.badlogic.gdx.utils.ScreenUtils.getFrameBufferPixels(0, 0, w, h, true);

            java.awt.image.BufferedImage img = new java.awt.image.BufferedImage(w, h, java.awt.image.BufferedImage.TYPE_INT_RGB);
            for (int y = 0; y < h; y++) {
                for (int x = 0; x < w; x++) {
                    int i = (x + (h - 1 - y) * w) * 4;
                    int r = pixels[i] & 0xFF;
                    int g = pixels[i + 1] & 0xFF;
                    int b = pixels[i + 2] & 0xFF;
                    int rgb = (r << 16) | (g << 8) | b;
                    img.setRGB(x, y, rgb);
                }
            }
            javax.imageio.ImageIO.write(img, "png", new File(path));
            System.out.println("Saved GPU Native 3D Screenshot: " + path);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override public void pause() {}
    @Override public void resume() {}

    @Override
    public void dispose() {
        disposeModels();
        if (modelBatch != null) modelBatch.dispose();
        if (shapeRenderer != null) shapeRenderer.dispose();
    }
}
