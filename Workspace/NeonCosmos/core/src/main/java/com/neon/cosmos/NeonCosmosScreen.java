package com.neon.cosmos;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

/**
 * Interactive 2D Sci-Fi Game Screen for NeonCosmos.
 * Features:
 * - Parallax Nebula Background
 * - Rotating Orbital Space Station
 * - Player Starship with keyboard/mouse navigation and thruster glow
 * - Dynamic Laser Projectiles
 * - Drifting Asteroid Field
 * - Real-time Heads-Up Display (HUD)
 */
public class NeonCosmosScreen implements Screen {

    private final NeonCosmosGame game;
    private OrthographicCamera camera;
    private Viewport viewport;

    public static final float WORLD_WIDTH = 1280f;
    public static final float WORLD_HEIGHT = 720f;

    // Textures
    private Texture bgTexture;
    private Texture shipTexture;
    private Texture stationTexture;
    private Texture asteroidTexture;
    private Texture laserTexture;
    private Texture pixelTexture;

    // Entities
    private Vector2 shipPos = new Vector2(WORLD_WIDTH / 2f, WORLD_HEIGHT / 2f);
    private float shipRotation = 0f;
    private float shipSpeed = 320f;
    private float timeElapsed = 0f;

    private float stationRotation = 0f;
    private Array<Asteroid> asteroids = new Array<>();
    private Array<Laser> lasers = new Array<>();
    private float shootTimer = 0f;

    public static class Asteroid {
        public Vector2 pos = new Vector2();
        public Vector2 vel = new Vector2();
        public float rotation = 0f;
        public float rotSpeed = 20f;
        public float size = 64f;

        public Asteroid(float x, float y, float vx, float vy, float size, float rotSpeed) {
            this.pos.set(x, y);
            this.vel.set(vx, vy);
            this.size = size;
            this.rotSpeed = rotSpeed;
        }
    }

    public static class Laser {
        public Vector2 pos = new Vector2();
        public Vector2 vel = new Vector2();
        public float life = 2.0f;

        public Laser(float x, float y, float vx, float vy) {
            this.pos.set(x, y);
            this.vel.set(vx, vy);
        }
    }

    public NeonCosmosScreen(NeonCosmosGame game) {
        this.game = game;
    }

    @Override
    public void show() {
        camera = new OrthographicCamera();
        viewport = new FitViewport(WORLD_WIDTH, WORLD_HEIGHT, camera);
        camera.position.set(WORLD_WIDTH / 2f, WORLD_HEIGHT / 2f, 0);

        loadTextures();
        initWorld();
    }

    private void loadTextures() {
        bgTexture = loadTextureSafely("textures/nebula_bg.png", createSolidPixmap(64, 64, new Color(0.05f, 0.07f, 0.12f, 1f)));
        shipTexture = loadTextureSafely("textures/player_ship.png", createShipPixmap());
        stationTexture = loadTextureSafely("textures/space_station.png", createStationPixmap());
        asteroidTexture = loadTextureSafely("textures/asteroid.png", createAsteroidPixmap());
        laserTexture = loadTextureSafely("textures/laser_bolt.png", createSolidPixmap(6, 16, Color.CYAN));

        Pixmap pixelPm = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixelPm.setColor(Color.WHITE);
        pixelPm.fill();
        pixelTexture = new Texture(pixelPm);
        pixelPm.dispose();
    }

    private Texture loadTextureSafely(String internalPath, Pixmap fallback) {
        try {
            if (Gdx.files != null && Gdx.files.internal(internalPath).exists()) {
                return new Texture(Gdx.files.internal(internalPath));
            }
        } catch (Exception ignored) {}
        Texture tex = new Texture(fallback);
        fallback.dispose();
        return tex;
    }

    private Pixmap createSolidPixmap(int w, int h, Color color) {
        Pixmap pm = new Pixmap(w, h, Pixmap.Format.RGBA8888);
        pm.setColor(color);
        pm.fill();
        return pm;
    }

    private Pixmap createShipPixmap() {
        Pixmap pm = new Pixmap(64, 64, Pixmap.Format.RGBA8888);
        pm.setColor(Color.CYAN);
        pm.fillTriangle(32, 4, 8, 60, 56, 60);
        pm.setColor(Color.WHITE);
        pm.fillTriangle(32, 16, 20, 56, 44, 56);
        return pm;
    }

    private Pixmap createStationPixmap() {
        Pixmap pm = new Pixmap(128, 128, Pixmap.Format.RGBA8888);
        pm.setColor(new Color(0.3f, 0.5f, 0.8f, 1f));
        pm.fillCircle(64, 64, 48);
        pm.setColor(new Color(0.1f, 0.2f, 0.4f, 1f));
        pm.fillCircle(64, 64, 28);
        pm.setColor(Color.CYAN);
        pm.drawCircle(64, 64, 48);
        return pm;
    }

    private Pixmap createAsteroidPixmap() {
        Pixmap pm = new Pixmap(64, 64, Pixmap.Format.RGBA8888);
        pm.setColor(new Color(0.45f, 0.42f, 0.48f, 1f));
        pm.fillCircle(32, 32, 26);
        pm.setColor(new Color(0.35f, 0.32f, 0.38f, 1f));
        pm.fillCircle(24, 24, 8);
        pm.fillCircle(40, 36, 6);
        return pm;
    }

    private void initWorld() {
        asteroids.clear();
        asteroids.add(new Asteroid(150, 550, 15, -8, 80, 25));
        asteroids.add(new Asteroid(1050, 480, -20, 12, 64, -18));
        asteroids.add(new Asteroid(850, 180, 10, 15, 96, 12));
        asteroids.add(new Asteroid(320, 120, -12, -10, 52, -30));
    }

    @Override
    public void render(float delta) {
        timeElapsed += delta;
        shootTimer += delta;

        handleInput(delta);
        updateWorld(delta);

        ScreenUtils.clear(0.04f, 0.05f, 0.08f, 1f);

        camera.update();
        game.batch.setProjectionMatrix(camera.combined);

        game.batch.begin();

        // 1. Nebula Starfield Background
        game.batch.setColor(1f, 1f, 1f, 1f);
        game.batch.draw(bgTexture, 0, 0, WORLD_WIDTH, WORLD_HEIGHT);

        // 2. Distant Space Station in Orbit
        float stW = 160f, stH = 160f;
        game.batch.setColor(0.9f, 0.95f, 1f, 0.85f);
        game.batch.draw(stationTexture, 920, 420, stW / 2f, stH / 2f, stW, stH, 1f, 1f, stationRotation, 0, 0, stationTexture.getWidth(), stationTexture.getHeight(), false, false);

        // 3. Asteroid Field
        game.batch.setColor(1f, 1f, 1f, 1f);
        for (Asteroid a : asteroids) {
            game.batch.draw(asteroidTexture, a.pos.x - a.size / 2f, a.pos.y - a.size / 2f, a.size / 2f, a.size / 2f, a.size, a.size, 1f, 1f, a.rotation, 0, 0, asteroidTexture.getWidth(), asteroidTexture.getHeight(), false, false);
        }

        // 4. Lasers
        game.batch.setColor(0f, 1f, 1f, 1f);
        for (Laser l : lasers) {
            game.batch.draw(laserTexture, l.pos.x - 4, l.pos.y - 12, 8, 24);
        }

        // 5. Player Starship
        game.batch.setColor(1f, 1f, 1f, 1f);
        float shipSize = 72f;
        game.batch.draw(shipTexture, shipPos.x - shipSize / 2f, shipPos.y - shipSize / 2f, shipSize / 2f, shipSize / 2f, shipSize, shipSize, 1f, 1f, shipRotation - 90f, 0, 0, shipTexture.getWidth(), shipTexture.getHeight(), false, false);

        // 6. HUD & Status
        drawHUD();

        game.batch.end();
    }

    private void handleInput(float delta) {
        float moveX = 0f;
        float moveY = 0f;

        if (Gdx.input.isKeyPressed(Input.Keys.W) || Gdx.input.isKeyPressed(Input.Keys.UP)) moveY += 1f;
        if (Gdx.input.isKeyPressed(Input.Keys.S) || Gdx.input.isKeyPressed(Input.Keys.DOWN)) moveY -= 1f;
        if (Gdx.input.isKeyPressed(Input.Keys.A) || Gdx.input.isKeyPressed(Input.Keys.LEFT)) moveX -= 1f;
        if (Gdx.input.isKeyPressed(Input.Keys.D) || Gdx.input.isKeyPressed(Input.Keys.RIGHT)) moveX += 1f;

        if (moveX != 0 || moveY != 0) {
            Vector2 dir = new Vector2(moveX, moveY).nor();
            shipPos.add(dir.scl(shipSpeed * delta));
            shipRotation = dir.angleDeg();
        }

        // Mouse aiming / click to fire
        if (Gdx.input.isButtonPressed(Input.Buttons.LEFT) || Gdx.input.isKeyPressed(Input.Keys.SPACE)) {
            if (shootTimer >= 0.18f) {
                shootTimer = 0f;
                fireLaser();
            }
        }

        // Screen boundary clamp
        shipPos.x = MathUtils.clamp(shipPos.x, 36, WORLD_WIDTH - 36);
        shipPos.y = MathUtils.clamp(shipPos.y, 36, WORLD_HEIGHT - 36);
    }

    private void fireLaser() {
        float angleRad = shipRotation * MathUtils.degRad;
        float vx = MathUtils.cos(angleRad) * 650f;
        float vy = MathUtils.sin(angleRad) * 650f;
        lasers.add(new Laser(shipPos.x, shipPos.y, vx, vy));
    }

    private void updateWorld(float delta) {
        stationRotation += 8f * delta;

        // Update Asteroids
        for (Asteroid a : asteroids) {
            a.pos.add(a.vel.x * delta, a.vel.y * delta);
            a.rotation += a.rotSpeed * delta;

            if (a.pos.x < -60) a.pos.x = WORLD_WIDTH + 60;
            if (a.pos.x > WORLD_WIDTH + 60) a.pos.x = -60;
            if (a.pos.y < -60) a.pos.y = WORLD_HEIGHT + 60;
            if (a.pos.y > WORLD_HEIGHT + 60) a.pos.y = -60;
        }

        // Update Lasers
        for (int i = lasers.size - 1; i >= 0; i--) {
            Laser l = lasers.get(i);
            l.pos.add(l.vel.x * delta, l.vel.y * delta);
            l.life -= delta;
            if (l.life <= 0 || l.pos.x < -20 || l.pos.x > WORLD_WIDTH + 20 || l.pos.y < -20 || l.pos.y > WORLD_HEIGHT + 20) {
                lasers.removeIndex(i);
            }
        }
    }

    private void drawHUD() {
        game.font.setColor(Color.CYAN);
        game.font.draw(game.batch, "NEON COSMOS - LibGDX Active Screen", 24, WORLD_HEIGHT - 24);

        game.font.setColor(Color.WHITE);
        game.font.draw(game.batch, "Controls: WASD / Arrow Keys to Fly | SPACE / Left-Click to Fire Laser", 24, WORLD_HEIGHT - 52);

        game.font.setColor(new Color(0.7f, 0.85f, 1f, 1f));
        game.font.draw(game.batch, String.format("FPS: %d | Ship: (%.0f, %.0f) | Lasers: %d", Gdx.graphics.getFramesPerSecond(), shipPos.x, shipPos.y, lasers.size), 24, 40);
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void hide() {}

    @Override
    public void dispose() {
        if (bgTexture != null) bgTexture.dispose();
        if (shipTexture != null) shipTexture.dispose();
        if (stationTexture != null) stationTexture.dispose();
        if (asteroidTexture != null) asteroidTexture.dispose();
        if (laserTexture != null) laserTexture.dispose();
        if (pixelTexture != null) pixelTexture.dispose();
    }
}
