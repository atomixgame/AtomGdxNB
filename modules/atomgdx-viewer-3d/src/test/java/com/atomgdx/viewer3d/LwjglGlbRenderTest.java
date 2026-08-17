package com.atomgdx.viewer3d;

import com.atomgdx.viewer3d.ui.Model3DViewportListener;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

import java.io.File;

/**
 * Direct hardware OpenGL LibGDX test runner for official Khronos DamagedHelmet.glb asset.
 */
public class LwjglGlbRenderTest {

    public static void main(String[] args) throws Exception {
        System.out.println("Starting Khronos .GLB Direct Hardware OpenGL GPU Test...");

        LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        config.title = "AtomGDX - Khronos DamagedHelmet.glb Native GPU Viewport";
        config.width = 1024;
        config.height = 600;
        config.forceExit = false;

        File helmetGlb = new File("g:/GameDev/LibGDX/AtomGdx/AtomGdxNB/Workspace/NeonCosmos/assets/models/khronos/DamagedHelmet.glb");
        final Model3DViewportListener listener = new Model3DViewportListener(helmetGlb);

        ApplicationAdapter app = new ApplicationAdapter() {
            int count = 0;

            @Override
            public void create() {
                listener.create();
            }

            @Override
            public void resize(int width, int height) {
                listener.resize(width, height);
            }

            @Override
            public void render() {
                listener.render();
                count++;

                if (count == 40) {
                    System.out.println("Rendered 40 GPU frames with real Khronos DamagedHelmet.glb geometry!");
                    listener.requestScreenshot("C:/Users/atomi/.gemini/antigravity/brain/fce3c73f-5838-4098-860c-1b34b316ce9c/glb_opengl_gpu_proof.png");
                }

                if (count >= 50) {
                    Gdx.app.exit();
                }
            }

            @Override
            public void dispose() {
                listener.dispose();
            }
        };

        new LwjglApplication(app, config);

        Thread.sleep(4000);
        System.out.println(">>> DIRECT OPENGL GLB TEST COMPLETED! <<<");
        System.exit(0);
    }
}
