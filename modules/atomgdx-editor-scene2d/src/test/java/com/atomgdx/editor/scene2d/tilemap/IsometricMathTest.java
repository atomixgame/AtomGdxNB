package com.atomgdx.editor.scene2d.tilemap;

import com.atomgdx.editor.scene2d.tilemap.data.IsometricMath;
import org.junit.Test;
import java.awt.Point;
import java.awt.Polygon;

import static org.junit.Assert.*;

public class IsometricMathTest {

    @Test
    public void testIsometricDiamondRoundtrip() {
        int tw = 64;
        int th = 32;
        int originX = 200;
        int originY = 100;

        int[][] testCells = new int[][]{
                {0, 0}, {1, 0}, {0, 1}, {5, 5}, {10, 3}, {15, 20}
        };

        for (int[] cell : testCells) {
            int cx = cell[0];
            int cy = cell[1];

            Point screen = IsometricMath.cellToScreen(cx, cy, tw, th, originX, originY);
            assertNotNull(screen);

            // Test center of the diamond
            int testScreenX = screen.x + tw / 2;
            int testScreenY = screen.y + th / 2;

            Point mappedBack = IsometricMath.screenToCell(testScreenX, testScreenY, tw, th, originX, originY);
            assertEquals("X mismatch for cell (" + cx + "," + cy + ")", cx, mappedBack.x);
            assertEquals("Y mismatch for cell (" + cx + "," + cy + ")", cy, mappedBack.y);
        }
    }

    @Test
    public void testDiamondPolygonVertices() {
        int tw = 64;
        int th = 32;
        int originX = 100;
        int originY = 50;

        Polygon poly = IsometricMath.getDiamondPolygon(2, 3, tw, th, originX, originY);
        assertEquals(4, poly.npoints);

        Point p = IsometricMath.cellToScreen(2, 3, tw, th, originX, originY);
        assertEquals(p.x + tw / 2, poly.xpoints[0]);
        assertEquals(p.y, poly.ypoints[0]);
    }

    @Test
    public void testStaggeredIsometricTransformations() {
        int tw = 64;
        int th = 32;
        int originX = 50;
        int originY = 50;

        Point pEven = IsometricMath.staggeredCellToScreen(2, 2, tw, th, originX, originY, true, true);
        Point pOdd = IsometricMath.staggeredCellToScreen(2, 3, tw, th, originX, originY, true, true);

        // Odd row should be staggered horizontally by half-width (32px)
        assertEquals(pEven.x + tw / 2, pOdd.x);
    }
}
