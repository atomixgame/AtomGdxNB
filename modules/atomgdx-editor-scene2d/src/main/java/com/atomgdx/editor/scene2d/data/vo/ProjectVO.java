package com.atomgdx.editor.scene2d.data.vo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * HyperLap2D project manifest holding resolution rules and library scenes.
 */
public class ProjectVO implements Serializable {
    private static final long serialVersionUID = 1L;

    public String projectName = "NewProject";
    public String projectVersion = "0.2.0";
    public ResolutionEntryVO originalResolution = new ResolutionEntryVO("origin", 1920, 1080);
    public List<ResolutionEntryVO> resolutions = new ArrayList<>();
    public List<String> scenes = new ArrayList<>();
    public List<CompositeItemVO> libraryItems = new ArrayList<>();

    public static class ResolutionEntryVO implements Serializable {
        private static final long serialVersionUID = 1L;
        public String name;
        public int width;
        public int height;

        public ResolutionEntryVO() {}
        public ResolutionEntryVO(String name, int width, int height) {
            this.name = name;
            this.width = width;
            this.height = height;
        }
    }
}
