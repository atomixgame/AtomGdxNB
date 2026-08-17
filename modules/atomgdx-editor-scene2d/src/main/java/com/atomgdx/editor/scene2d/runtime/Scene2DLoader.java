package com.atomgdx.editor.scene2d.runtime;

import com.atomgdx.editor.scene2d.data.vo.HyperLap2DSerializer;
import com.atomgdx.editor.scene2d.data.vo.ProjectVO;
import com.atomgdx.editor.scene2d.data.vo.SceneVO;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Stage;

import java.io.File;
import java.io.IOException;

/**
 * High-level runtime scene loader to instantiate and manage HyperLap2D scenes in LibGDX games.
 */
public class Scene2DLoader {

    private ProjectVO projectVO;
    private TextureAtlas atlas;

    public Scene2DLoader() {}

    public Scene2DLoader(TextureAtlas atlas) {
        this.atlas = atlas;
    }

    public void loadProject(FileHandle projectFile) throws IOException {
        this.projectVO = HyperLap2DSerializer.deserializeProject(projectFile.readString());
    }

    public SceneVO loadScene(FileHandle sceneFile) {
        return HyperLap2DSerializer.deserializeScene(sceneFile.readString());
    }

    public CompositeActor createSceneActor(SceneVO sceneVO) {
        if (sceneVO == null) return null;
        CompositeActor root = new CompositeActor(sceneVO.composite);
        if (atlas != null) {
            for (TextureAtlas.AtlasRegion region : atlas.getRegions()) {
                root.registerTexture(region.name, region);
            }
        }
        root.rebuildHierarchy();
        return root;
    }

    public CompositeActor loadSceneToStage(FileHandle sceneFile, Stage stage) {
        SceneVO sceneVO = loadScene(sceneFile);
        CompositeActor root = createSceneActor(sceneVO);
        if (stage != null && root != null) {
            stage.addActor(root);
        }
        return root;
    }

    public ProjectVO getProjectVO() {
        return projectVO;
    }
}
