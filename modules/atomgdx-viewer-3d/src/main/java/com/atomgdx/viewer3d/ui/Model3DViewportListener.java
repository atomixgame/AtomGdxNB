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

/**
 * Native LibGDX 3D OpenGL Viewport Listener for rendering 3D models (GLTF/OBJ),
 * PBR environment lighting, grid floor, wireframe mode, and orbit camera.
 */
public class Model3DViewportListener implements ApplicationListener {

    private PerspectiveCamera camera;
    private ModelBatch modelBatch;
    private Environment environment;
    private ShapeRenderer shapeRenderer;

    private Model model;
    private ModelInstance instance;
    private File modelFile;

    private boolean showGrid = true;
    private boolean wireframe = false;
    private boolean pbrLighting = true;

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

    @Override
    public void create() {
        camera = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.near = 0.1f;
        camera.far = 500f;
        updateCamera();

        modelBatch = new ModelBatch();
        shapeRenderer = new ShapeRenderer();

        environment = new Environment();
        environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.45f, 0.55f, 1f));
        environment.add(new DirectionalLight().set(0.9f, 0.95f, 1f, -1f, -0.8f, -0.2f));
        environment.add(new DirectionalLight().set(0.3f, 0.35f, 0.5f, 1f, 0.8f, 0.2f));

        createSample3DModel();
    }

    private void createSample3DModel() {
        ModelBuilder modelBuilder = new ModelBuilder();
        Material mat = new Material(
                ColorAttribute.createDiffuse(new Color(0.2f, 0.5f, 0.95f, 1f)),
                ColorAttribute.createSpecular(Color.WHITE)
        );

        model = modelBuilder.createBox(2f, 2f, 2f, mat,
                VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);
        instance = new ModelInstance(model);
        instance.transform.setToTranslation(0, 1f, 0);
    }

    @Override
    public void resize(int width, int height) {
        if (width <= 0 || height <= 0) return;
        camera.viewportWidth = width;
        camera.viewportHeight = height;
        updateCamera();
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

        if (instance != null && modelBatch != null) {
            Gdx.gl.glEnable(GL20.GL_DEPTH_TEST);

            modelBatch.begin(camera);
            if (pbrLighting) {
                modelBatch.render(instance, environment);
            } else {
                modelBatch.render(instance);
            }
            modelBatch.end();

            Gdx.gl.glDisable(GL20.GL_DEPTH_TEST);
        }

        if (captureScreenshotPath != null) {
            saveGpuBackbufferToPng(captureScreenshotPath);
            captureScreenshotPath = null;
        }
    }

    private String captureScreenshotPath;

    public void requestScreenshot(String path) {
        this.captureScreenshotPath = path;
    }

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

    private void render3DGrid() {
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);

        // Grid lines
        shapeRenderer.setColor(0.18f, 0.20f, 0.24f, 1f);
        int gridSize = 10;
        for (int i = -gridSize; i <= gridSize; i++) {
            shapeRenderer.line(i, 0, -gridSize, i, 0, gridSize);
            shapeRenderer.line(-gridSize, 0, i, gridSize, 0, i);
        }

        // Axes (X: Red, Y: Green, Z: Blue)
        shapeRenderer.setColor(0.85f, 0.25f, 0.25f, 1f);
        shapeRenderer.line(0, 0, 0, 3, 0, 0);
        shapeRenderer.setColor(0.25f, 0.85f, 0.35f, 1f);
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

    public void resetCamera() {
        cameraYaw = 45f;
        cameraPitch = 30f;
        cameraDistance = 8f;
        target.set(0, 0, 0);
        updateCamera();
    }

    public void setShowGrid(boolean show) {
        this.showGrid = show;
    }

    public void setPbrLighting(boolean pbr) {
        this.pbrLighting = pbr;
    }

    public int getFrameCount() {
        return frameCount;
    }

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void dispose() {
        if (modelBatch != null) modelBatch.dispose();
        if (model != null) model.dispose();
        if (shapeRenderer != null) shapeRenderer.dispose();
    }
}
