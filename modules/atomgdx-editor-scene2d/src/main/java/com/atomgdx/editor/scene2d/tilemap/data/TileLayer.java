package com.atomgdx.editor.scene2d.tilemap.data;

import java.util.Arrays;

/**
 * 2D Tile Grid Layer storing tile global IDs (GIDs). 0 represents an empty cell.
 */
public class TileLayer extends TilemapLayer {
    private static final long serialVersionUID = 1L;

    private int width;
    private int height;
    private int[][] tiles;

    public TileLayer(String name, int width, int height) {
        super(name, LayerType.TILE_LAYER);
        this.width = width;
        this.height = height;
        this.tiles = new int[height][width];
    }

    public int getWidth() { return width; }
    public int getHeight() { return height; }

    public int getTile(int x, int y) {
        if (x < 0 || x >= width || y < 0 || y >= height) return 0;
        return tiles[y][x];
    }

    public void setTile(int x, int y, int tileGid) {
        if (x >= 0 && x < width && y >= 0 && y < height) {
            tiles[y][x] = tileGid;
        }
    }

    public void clear() {
        for (int[] row : tiles) {
            Arrays.fill(row, 0);
        }
    }

    public void fill(int tileGid) {
        for (int[] row : tiles) {
            Arrays.fill(row, tileGid);
        }
    }

    public void fillRect(int startX, int startY, int rectW, int rectH, int tileGid) {
        for (int y = startY; y < startY + rectH; y++) {
            for (int x = startX; x < startX + rectW; x++) {
                setTile(x, y, tileGid);
            }
        }
    }

    /**
     * 4-Way flood fill algorithm matching target tile GID.
     */
    public void floodFill(int startX, int startY, int newGid) {
        if (startX < 0 || startX >= width || startY < 0 || startY >= height) return;
        int targetGid = getTile(startX, startY);
        if (targetGid == newGid) return;

        boolean[][] visited = new boolean[height][width];
        java.util.Queue<int[]> queue = new java.util.LinkedList<>();
        queue.add(new int[]{startX, startY});
        visited[startY][startX] = true;

        while (!queue.isEmpty()) {
            int[] pos = queue.poll();
            int x = pos[0];
            int y = pos[1];

            setTile(x, y, newGid);

            int[][] dirs = {{0, 1}, {0, -1}, {1, 0}, {-1, 0}};
            for (int[] d : dirs) {
                int nx = x + d[0];
                int ny = y + d[1];
                if (nx >= 0 && nx < width && ny >= 0 && ny < height && !visited[ny][nx] && getTile(nx, ny) == targetGid) {
                    visited[ny][nx] = true;
                    queue.add(new int[]{nx, ny});
                }
            }
        }
    }

    public int[][] getRawTiles() {
        return tiles;
    }
}
