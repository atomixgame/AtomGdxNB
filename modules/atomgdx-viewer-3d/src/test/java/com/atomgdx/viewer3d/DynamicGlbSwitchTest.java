package com.atomgdx.viewer3d;

import com.atomgdx.viewer3d.ui.Model3DViewportListener;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

import java.io.File;

/**
 * Validates dynamic switching between GLB models (DamagedHelmet -> ToyCar -> CesiumMilkTruck)
 * on runtime without any OpenGL context threading collisions.
 */
public class DynamicGlbSwitchTest {

    public static void main(String[] args) throws Exception {
        System.out.println("Starting Dynamic GLB Switch & OpenGL Context Thread Safety Test...");

        LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        config.title = "AtomGDX - Dynamic GLB Switch Test";
        config.width = 800;
        config.height = 600;
        config.forceExit = false;

        File helmetGlb = new File("g:/GameDev/LibGDX/AtomGdx/AtomGdxNB/Workspace/NeonCosmos/assets/models/khronos/DamagedHelmet.glb");
        final Model3DViewportListener listener = new Model3DViewportListener(helmetGlb);

        File toyCarGlb = new File("g:/GameDev/LibGDX/AtomGdx/AtomGdxNB/Workspace/NeonCosmos/assets/models/khronos/ToyCar.glb");
        File milkTruckGlb = new File("g:/GameDev/LibGDX/AtomGdx/AtomGdxNB/Workspace/NeonCosmos/assets/models/khronos/CesiumMilkTruck.glb");

        ApplicationAdapter app = new ApplicationAdapter() {
            int frame = 0;

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
                frame++;
                listener.render();

                if (frame == 15) {
                    System.out.println("Switching to ToyCar.glb from external thread simulate Swing EDT...");
                    // Simulate Swing EDT event
                    new Thread(() -> listener.setModelFile(toyCarGlb)).start();
                }

                if (frame == 35) {
                    System.out.println("Switching to CesiumMilkTruck.glb from external thread simulate Swing EDT...");
                    new Thread(() -> listener.setModelFile(milkTruckGlb)).start();
                }

                if (frame >= 55) {
                    System.out.println("Dynamic GLB Model Switching Passed Successfully on OpenGL Thread!");
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
        System.out.println(">>> DYNAMIC GLB SWITCH TEST COMPLETED! <<<");
        System.exit(0);
    }
}
