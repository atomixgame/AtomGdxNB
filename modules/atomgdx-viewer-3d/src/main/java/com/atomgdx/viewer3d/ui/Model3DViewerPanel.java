package com.atomgdx.viewer3d.ui;

import com.atomgdx.core.ui.DarkThemeUtils;
import com.atomgdx.core.viewport.GdxAwtViewport;
import com.atomgdx.viewer3d.Model3DDescriptor;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.function.Consumer;

/**
 * 3D Model & GLTF/GLB Viewer panel (Read-Only Preview Mode).
 * Features icon-only toolbar with hover tooltips, quick-selector for popular GLTF/GLB demo models,
 * orbit camera controls, and edge-to-edge hardware OpenGL viewport.
 */
public class Model3DViewerPanel extends JPanel {

    private Model3DDescriptor descriptor;
    private final Model3DViewportListener viewportListener;
    private final GdxAwtViewport gdxViewport;
    private Consumer<Model3DDescriptor> modelChangedListener;

    private int lastMouseX, lastMouseY;
    private boolean isOrbiting = false;
    private boolean isPanning = false;

    private static final String[] DEMO_MODELS = {
            "khronos/DamagedHelmet.glb",
            "khronos/AntiqueCamera.glb",
            "khronos/BoomBox.glb",
            "khronos/Corset.glb",
            "khronos/ToyCar.glb",
            "khronos/MaterialsVariantsShoe.glb",
            "khronos/SheenChair.glb",
            "khronos/AnisotropyBarnLamp.glb",
            "khronos/GlamVelvetSofa.glb",
            "khronos/DragonAttenuation.glb",
            "khronos/IridescenceSuzanne.glb",
            "khronos/TransmissionTest.glb",
            "khronos/SpecularTest.glb",
            "khronos/ClearCoatTest.glb",
            "khronos/EmissiveStrengthTest.glb",
            "khronos/AlphaBlendModeTest.glb",
            "khronos/MetalRoughSpheres.glb",
            "khronos/BrainStem.glb",
            "khronos/WaterBottle.glb",
            "khronos/BarramundiFish.glb",
            "khronos/CesiumMilkTruck.glb",
            "khronos/Avocado.glb",
            "khronos/Lantern.glb",
            "khronos/Duck.glb",
            "khronos/Fox.glb",
            "khronos/CarbonFibre.glb",
            "khronos/OrientationTest.glb",
            "khronos/TextureCoordinateTest.glb",
            "khronos/NormalTangentTest.glb",
            "khronos/VertexColorTest.glb",
            "khronos/BoxTextured.glb",
            "khronos/BoxVertexColors.glb",
            "khronos/RecursiveSkeletons.glb",
            "khronos/RiggedFigure.glb",
            "spacecraft_cruiser.gltf",
            "asteroid_large.gltf",
            "scifi_turret.gltf",
            "cargo_container.gltf",
            "energy_shield_bubble.gltf",
            "space_station_module.gltf",
            "quantum_warp_beacon.gltf",
            "modular_scifi_wall.gltf",
            "plasma_cannon_heavy.gltf"
    };

    public Model3DViewerPanel(Model3DDescriptor descriptor) {
        this.descriptor = descriptor != null ? descriptor : new Model3DDescriptor(new File("g:/GameDev/LibGDX/AtomGdx/AtomGdxNB/Workspace/NeonCosmos/assets/models/" + DEMO_MODELS[0]));
        setLayout(new BorderLayout(0, 0));
        setBackground(DarkThemeUtils.BG_DARK);
        setBorder(null);

        File file = this.descriptor.getModelFile();
        viewportListener = new Model3DViewportListener(file);
        gdxViewport = new GdxAwtViewport(viewportListener);

        setupMouseInteractions();

        // Top Toolbar with icon-only buttons & hover tooltips
        JToolBar toolbar = createToolBar();

        add(toolbar, BorderLayout.NORTH);
        add(gdxViewport, BorderLayout.CENTER);
    }

    public void setModelChangedListener(Consumer<Model3DDescriptor> listener) {
        this.modelChangedListener = listener;
    }

    public Model3DDescriptor getDescriptor() {
        return descriptor;
    }

    public int getRenderedFrameCount() {
        return viewportListener != null ? viewportListener.getFrameCount() : 0;
    }

    public Model3DViewportListener getViewportListener() {
        return viewportListener;
    }

    private JToolBar createToolBar() {
        JToolBar tb = new JToolBar();
        tb.setFloatable(false);
        tb.setBackground(DarkThemeUtils.BG_HEADER);
        tb.setBorder(new LineBorder(DarkThemeUtils.BORDER, 1));

        JLabel modelIcon = new JLabel(DarkThemeUtils.getFatcowIcon("box.png"));
        modelIcon.setToolTipText("Khronos GLB & GLTF 3D Models");
        tb.add(modelIcon);

        JComboBox<String> modelCombo = new JComboBox<>(DEMO_MODELS);
        modelCombo.setBackground(DarkThemeUtils.BG_INPUT);
        modelCombo.setForeground(DarkThemeUtils.TEXT_PRIMARY);
        modelCombo.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        modelCombo.setMaximumSize(new Dimension(190, 24));
        modelCombo.setToolTipText("Select 3D Model File (.glb / .gltf)");
        modelCombo.addActionListener(e -> {
            String selected = (String) modelCombo.getSelectedItem();
            if (selected != null) {
                File mf = new File("g:/GameDev/LibGDX/AtomGdx/AtomGdxNB/Workspace/NeonCosmos/assets/models/" + selected);
                this.descriptor = new Model3DDescriptor(mf);
                viewportListener.setModelFile(mf);
                if (modelChangedListener != null) {
                    modelChangedListener.accept(this.descriptor);
                }
            }
        });
        tb.add(modelCombo);

        tb.addSeparator();

        JToggleButton gridBtn = new JToggleButton(DarkThemeUtils.getFatcowIcon("layout.png"), true);
        gridBtn.setToolTipText("Toggle Ground Grid & Axes");

        JToggleButton pbrBtn = new JToggleButton(DarkThemeUtils.getFatcowIcon("weather_sun.png"), true);
        pbrBtn.setToolTipText("Toggle PBR Environment Lighting");

        JButton resetCamBtn = new JButton(DarkThemeUtils.getFatcowIcon("camera.png"));
        resetCamBtn.setToolTipText("Reset Camera Orbit (Home)");

        for (AbstractButton b : new AbstractButton[]{gridBtn, pbrBtn, resetCamBtn}) {
            b.setBackground(DarkThemeUtils.BG_HEADER);
            b.setForeground(DarkThemeUtils.TEXT_PRIMARY);
            b.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            b.setFocusPainted(false);
            b.setPreferredSize(new Dimension(28, 26));
            tb.add(b);
        }

        gridBtn.addActionListener(e -> viewportListener.setShowGrid(gridBtn.isSelected()));
        pbrBtn.addActionListener(e -> viewportListener.setShadingMode(pbrBtn.isSelected() ? Model3DViewportListener.ShadingMode.SHADED_PBR : Model3DViewportListener.ShadingMode.UNLIT));
        resetCamBtn.addActionListener(e -> viewportListener.resetCamera());

        return tb;
    }

    private void setupMouseInteractions() {
        if (gdxViewport.getCanvas() == null) return;
        Canvas canvas = gdxViewport.getCanvas().getCanvas();

        canvas.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                lastMouseX = e.getX();
                lastMouseY = e.getY();
                if (SwingUtilities.isLeftMouseButton(e)) {
                    isOrbiting = true;
                } else if (SwingUtilities.isRightMouseButton(e) || SwingUtilities.isMiddleMouseButton(e)) {
                    isPanning = true;
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                isOrbiting = false;
                isPanning = false;
            }
        });

        canvas.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                int dx = e.getX() - lastMouseX;
                int dy = e.getY() - lastMouseY;
                lastMouseX = e.getX();
                lastMouseY = e.getY();

                if (isOrbiting) {
                    viewportListener.orbit(dx * 0.5f, dy * 0.5f);
                } else if (isPanning) {
                    viewportListener.pan(dx, dy);
                }
            }
        });

        canvas.addMouseWheelListener(e -> {
            float delta = (float) e.getPreciseWheelRotation();
            viewportListener.zoom(delta * 0.8f);
        });
    }

    @Override
    public Dimension getMinimumSize() {
        return new Dimension(0, 0);
    }
}
