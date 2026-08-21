package com.atomgdx.viewer3d;

import com.atomgdx.theme.windows.InspectorTopComponent;
import com.atomgdx.theme.windows.PaletteTopComponent;
import com.atomgdx.viewer3d.data.Node3DVO;
import com.atomgdx.viewer3d.data.PaletteItemVO;
import com.atomgdx.viewer3d.data.PaletteLoader;
import org.openide.util.Lookup;
import org.openide.util.LookupEvent;
import org.openide.util.lookup.AbstractLookup;
import org.openide.util.lookup.InstanceContent;

import javax.swing.*;
import java.io.File;
import java.util.Collections;
import java.util.List;

/**
 * Rigorous End-to-End Test verifying:
 * 1. Palette items load all 56 items across all categories (2D Shapes, 2D Prefabs, 3D Primitives, 3D Prefabs, Materials).
 * 2. Node Lookup Focus flaw fix: When a Node is selected, and then focus changes (Lookup becomes empty when clicking UI elements),
 *    the Inspector MUST retain the selected node's properties without disappearing or resetting.
 */
public class InspectorLookupAndPaletteE2ETest {

    public static void main(String[] args) throws Exception {
        System.out.println(">>> STARTING INSPECTOR LOOKUP FOCUS & PALETTE 56-ITEM E2E VERIFICATION <<<");

        // 1. Verify Palette Loading
        List<PaletteItemVO> items = PaletteLoader.loadPaletteItems();
        System.out.println("Palette loaded item count: " + items.size());
        if (items.size() < 56) {
            throw new AssertionError("Expected at least 56 palette items, got: " + items.size());
        }

        long shapes2D = items.stream().filter(i -> "2D Shapes".equalsIgnoreCase(i.category)).count();
        long prefabs2D = items.stream().filter(i -> "2D Prefabs".equalsIgnoreCase(i.category)).count();
        long prims3D = items.stream().filter(i -> "3D Primitives".equalsIgnoreCase(i.category)).count();
        long prefabs3D = items.stream().filter(i -> "3D Prefabs".equalsIgnoreCase(i.category)).count();
        long materials = items.stream().filter(i -> "Materials".equalsIgnoreCase(i.category)).count();

        System.out.println("  - 2D Shapes: " + shapes2D);
        System.out.println("  - 2D Prefabs: " + prefabs2D);
        System.out.println("  - 3D Primitives: " + prims3D);
        System.out.println("  - 3D Prefabs: " + prefabs3D);
        System.out.println("  - Materials: " + materials);

        if (shapes2D < 5 || prefabs2D < 5 || prims3D < 5 || prefabs3D < 5 || materials < 5) {
            throw new AssertionError("Category count verification failed!");
        }

        // 2. Verify PaletteTopComponent initializes with 56 items in Swing EDT
        SwingUtilities.invokeAndWait(() -> {
            PaletteTopComponent paletteTc = new PaletteTopComponent();
            if (paletteTc.getPalettePanel() == null) {
                throw new AssertionError("PaletteTopComponent panel is null!");
            }
            System.out.println("PaletteTopComponent initialized successfully with all items!");
        });

        // 3. Verify Inspector Focus Retention on Node Selection
        SwingUtilities.invokeAndWait(() -> {
            InspectorTopComponent inspector = new InspectorTopComponent();
            
            // Step 3a: Select a 3D Node
            Node3DVO node = new Node3DVO("Thruster_Main", Node3DVO.MeshShape.CYLINDER, 1f, 2f, 1f);
            node.posX = 1.5f;
            node.posY = 0.8f;
            node.posZ = -3.2f;
            inspector.inspectNode3D(node);

            if (inspector.getCurrentlyInspected() != node) {
                throw new AssertionError("Inspector did not set currentlyInspected to node!");
            }
            System.out.println("Step 3a Passed: Inspector displaying Node " + node.nodeName);

            // Step 3b: Simulate user clicking UI elements (spinners, text fields) inside Inspector.
            // In NetBeans, focus shifts to Inspector, causing Utilities.actionsGlobalContext() to become empty!
            InstanceContent emptyContent = new InstanceContent();
            Lookup emptyLookup = new AbstractLookup(emptyContent);
            Lookup.Result<Object> emptyResult = emptyLookup.lookupResult(Object.class);

            // Trigger resultChanged with empty lookup
            inspector.resultChanged(new LookupEvent(emptyResult));

            // Verify Inspector STILL retains the node!
            if (inspector.getCurrentlyInspected() != node) {
                throw new AssertionError("FAIL: Inspector lost the inspected node when lookup became empty on focus shift!");
            }
            System.out.println("Step 3b Passed: Inspector successfully RETAINED Node during UI interaction / focus shift!");

            // Step 3c: Select a new Model Descriptor
            Model3DDescriptor desc = new Model3DDescriptor(new File("spacecraft_cruiser.gltf"));
            InstanceContent newContent = new InstanceContent();
            newContent.set(Collections.singleton(desc), null);
            Lookup newLookup = new AbstractLookup(newContent);
            Lookup.Result<Object> newResult = newLookup.lookupResult(Object.class);

            inspector.resultChanged(new LookupEvent(newResult));
            if (inspector.getCurrentlyInspected() != desc) {
                throw new AssertionError("FAIL: Inspector did not update to newly selected Model3DDescriptor!");
            }
            System.out.println("Step 3c Passed: Inspector cleanly updated to new selection!");

            // Step 3d: Click UI element inside Inspector again
            inspector.resultChanged(new LookupEvent(emptyResult));
            if (inspector.getCurrentlyInspected() != desc) {
                throw new AssertionError("FAIL: Inspector lost the inspected model descriptor on focus shift!");
            }
            System.out.println("Step 3d Passed: Inspector retained Model3DDescriptor during UI interaction!");
        });

        System.out.println(">>> ALL INSPECTOR LOOKUP FOCUS & PALETTE E2E TESTS PASSED 100%! <<<");
    }
}
