package com.atomgdx.editor.scene2d.ui;

import com.atomgdx.editor.scene2d.data.vo.*;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.ScreenUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * LibGDX ApplicationListener powering the hardware-accelerated 2D scene viewport.
 */
public class Scene2DViewportListener implements ApplicationListener {

    private final SceneVO scene;
    private final TransformGizmo gizmo = new TransformGizmo();
    private final Map<String, Texture> textureCache = new HashMap<>();

    private SpriteBatch batch;
    private ShapeRenderer shapeRenderer;
    private OrthographicCamera camera;
    private BitmapFont font;
    private Texture defaultTexture;

    private boolean showGrid = true;
    private float gridSize = 32f;
    private int frameCount = 0;

    public Scene2DViewportListener(SceneVO scene) {
        this.scene = scene != null ? scene : new SceneVO("MainScene");
    }

    public TransformGizmo getGizmo() {
        return gizmo;
    }

    public OrthographicCamera getCamera() {
        return camera;
    }

    public int getFrameCount() {
        return frameCount;
    }

    public void setShowGrid(boolean showGrid) {
        this.showGrid = showGrid;
    }

    public void setGridSize(float gridSize) {
        this.gridSize = gridSize;
    }

    public Vector3 screenToWorld(int screenX, int screenY) {
        if (camera == null) return new Vector3(screenX, screenY, 0);
        Vector3 vec = new Vector3(screenX, screenY, 0);
        camera.unproject(vec);
        return vec;
    }

    public void loadTexture(String name, File file) {
        if (file != null && file.exists()) {
            try {
                Texture tex = new Texture(new FileHandle(file));
                textureCache.put(name, tex);
            } catch (Throwable t) {
                System.err.println("Failed to load scene texture: " + t.getMessage());
            }
        }
    }

    @Override
    public void create() {
        batch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();
        camera = new OrthographicCamera(1280, 720);
        camera.position.set(640, 360, 0);
        camera.update();

        font = new BitmapFont();

        // Procedural default checker/box texture
        Pixmap pixmap = new Pixmap(64, 64, Pixmap.Format.RGBA8888);
        pixmap.setColor(0.35f, 0.45f, 0.65f, 1f);
        pixmap.fill();
        pixmap.setColor(0.25f, 0.35f, 0.55f, 1f);
        pixmap.fillRectangle(0, 0, 32, 32);
        pixmap.fillRectangle(32, 32, 32, 32);
        defaultTexture = new Texture(pixmap);
        pixmap.dispose();
    }

    @Override
    public void resize(int width, int height) {
        if (width <= 0 || height <= 0) return;
        if (camera != null) {
            camera.viewportWidth = width;
            camera.viewportHeight = height;
            camera.update();
        }
    }

    @Override
    public void render() {
        frameCount++;

        // Clear with Dark Theme background
        ScreenUtils.clear(0.12f, 0.12f, 0.13f, 1f);

        if (camera == null || batch == null || shapeRenderer == null) return;

        camera.update();

        // 1. Render Grid
        if (showGrid) {
            renderGrid();
        }

        // 2. Render Scene Items
        batch.setProjectionMatrix(camera.combined);
        batch.begin();

        for (SimpleImageVO img : scene.composite.sImages) {
            if (!img.isVisible) continue;
            Texture tex = textureCache.get(img.imageName);
            if (tex == null && img.imageName != null && !img.imageName.isEmpty()) {
                File texFile = new File("g:/GameDev/LibGDX/AtomGdx/AtomGdxNB/Workspace/NeonCosmos/assets/textures", img.imageName);
                if (!texFile.exists()) {
                    texFile = new File("g:/GameDev/LibGDX/AtomGdx/AtomGdxNB/Workspace/NeonCosmos/assets/textures", img.imageName + ".png");
                }
                if (texFile.exists()) {
                    loadTexture(img.imageName, texFile);
                    tex = textureCache.get(img.imageName);
                }
            }
            if (tex == null) tex = defaultTexture;

            float w = img.width > 0 ? img.width : tex.getWidth();
            float h = img.height > 0 ? img.height : tex.getHeight();

            if (img.tintColor != null) {
                batch.setColor(img.tintColor.r, img.tintColor.g, img.tintColor.b, img.tintColor.a);
            } else {
                batch.setColor(1f, 1f, 1f, 1f);
            }

            batch.draw(
                    tex,
                    img.x, img.y,
                    img.originX, img.originY,
                    w, h,
                    img.scaleX, img.scaleY,
                    img.rotation,
                    0, 0,
                    tex.getWidth(), tex.getHeight(),
                    false, false
            );
        }

        // Render Labels
        for (LabelVO lbl : scene.composite.sLabels) {
            if (!lbl.isVisible) continue;
            if (lbl.tintColor != null) {
                font.setColor(lbl.tintColor.r, lbl.tintColor.g, lbl.tintColor.b, lbl.tintColor.a);
            }
            font.draw(batch, lbl.text != null ? lbl.text : "", lbl.x, lbl.y);
        }

        batch.end();

        // 3. Render Gizmo and Selections
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        gizmo.render(shapeRenderer);
        shapeRenderer.end();

        // 4. Capture exact GPU backbuffer image at frame 30 for automated tests
        if (frameCount == 30 && Gdx.graphics.getWidth() > 0 && Gdx.graphics.getHeight() > 0) {
            try {
                int w = Gdx.graphics.getWidth();
                int h = Gdx.graphics.getHeight();
                byte[] pixels = ScreenUtils.getFrameBufferPixels(0, 0, w, h, true);
                BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
                for (int y = 0; y < h; y++) {
                    for (int x = 0; x < w; x++) {
                        int idx = (y * w + x) * 4;
                        int r = pixels[idx] & 0xFF;
                        int g = pixels[idx + 1] & 0xFF;
                        int b = pixels[idx + 2] & 0xFF;
                        int a = pixels[idx + 3] & 0xFF;
                        img.setRGB(x, y, (a << 24) | (r << 16) | (g << 8) | b);
                    }
                }
                File out = new File("C:/Users/atomi/.gemini/antigravity/brain/fce3c73f-5838-4098-860c-1b34b316ce9c/hyperlap2d_opengl_gpu_proof.png");
                ImageIO.write(img, "png", out);
                System.out.println(">>> SAVED HYPERLAP2D GPU BACKBUFFER CAPTURE (" + w + "x" + h + ") to " + out.getAbsolutePath() + " <<<");
            } catch (Throwable t) {
                System.err.println("HyperLap2D GPU capture error: " + t.getMessage());
            }
        }
    }

    private void renderGrid() {
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);

        // Subtle dark grid
        shapeRenderer.setColor(0.18f, 0.19f, 0.22f, 0.7f);

        float minX = camera.position.x - camera.viewportWidth * camera.zoom;
        float maxX = camera.position.x + camera.viewportWidth * camera.zoom;
        float minY = camera.position.y - camera.viewportHeight * camera.zoom;
        float maxY = camera.position.y + camera.viewportHeight * camera.zoom;

        float startX = (float) (Math.floor(minX / gridSize) * gridSize);
        float startY = (float) (Math.floor(minY / gridSize) * gridSize);

        for (float x = startX; x <= maxX; x += gridSize) {
            shapeRenderer.line(x, minY, x, maxY);
        }
        for (float y = startY; y <= maxY; y += gridSize) {
            shapeRenderer.line(minX, y, maxX, y);
        }

        // Axes cross (X=Red, Y=Green)
        shapeRenderer.setColor(0.8f, 0.25f, 0.25f, 0.8f);
        shapeRenderer.line(-2000, 0, 2000, 0);
        shapeRenderer.setColor(0.25f, 0.8f, 0.3f, 0.8f);
        shapeRenderer.line(0, -2000, 0, 2000);

        shapeRenderer.end();
    }

    @Override public void pause() {}
    @Override public void resume() {}

    @Override
    public void dispose() {
        if (batch != null) batch.dispose();
        if (shapeRenderer != null) shapeRenderer.dispose();
        if (font != null) font.dispose();
        if (defaultTexture != null) defaultTexture.dispose();
        for (Texture t : textureCache.values()) {
            t.dispose();
        }
        textureCache.clear();
    }
}
