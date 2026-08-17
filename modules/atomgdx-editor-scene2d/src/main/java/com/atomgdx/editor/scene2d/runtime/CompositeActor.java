package com.atomgdx.editor.scene2d.runtime;

import com.atomgdx.editor.scene2d.data.vo.*;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;

import java.util.HashMap;
import java.util.Map;

/**
 * LibGDX Scene2D Group representing a HyperLap2D Composite Item with nested children.
 */
public class CompositeActor extends Group {

    private final CompositeDataObject dataObject;
    private final Map<String, TextureRegion> textureMap = new HashMap<>();

    public CompositeActor(CompositeDataObject dataObject) {
        this.dataObject = dataObject != null ? dataObject : new CompositeDataObject();
    }

    public CompositeDataObject getDataObject() {
        return dataObject;
    }

    public void registerTexture(String name, TextureRegion region) {
        textureMap.put(name, region);
    }

    public void rebuildHierarchy() {
        clearChildren();
        if (dataObject == null) return;

        // Add Simple Images
        for (SimpleImageVO imgVO : dataObject.sImages) {
            if (!imgVO.isVisible) continue;
            TextureRegion region = textureMap.get(imgVO.imageName);
            ImageActor actor = new ImageActor(imgVO, region);
            addActor(actor);
        }

        // Add Nested Composites
        for (CompositeItemVO compVO : dataObject.sComposites) {
            if (!compVO.isVisible) continue;
            CompositeActor childComp = new CompositeActor(compVO.composite);
            childComp.setPosition(compVO.x, compVO.y);
            childComp.setScale(compVO.scaleX, compVO.scaleY);
            childComp.setRotation(compVO.rotation);
            childComp.setOrigin(compVO.originX, compVO.originY);
            childComp.rebuildHierarchy();
            addActor(childComp);
        }
    }

    public static class ImageActor extends Actor {
        private final SimpleImageVO vo;
        private final TextureRegion region;

        public ImageActor(SimpleImageVO vo, TextureRegion region) {
            this.vo = vo;
            this.region = region;
            setPosition(vo.x, vo.y);
            setScale(vo.scaleX, vo.scaleY);
            setRotation(vo.rotation);
            setOrigin(vo.originX, vo.originY);
            if (vo.width > 0 && vo.height > 0) {
                setSize(vo.width, vo.height);
            } else if (region != null) {
                setSize(region.getRegionWidth(), region.getRegionHeight());
            }
            if (vo.tintColor != null) {
                setColor(vo.tintColor.r, vo.tintColor.g, vo.tintColor.b, vo.tintColor.a);
            }
        }

        public SimpleImageVO getVo() {
            return vo;
        }

        @Override
        public void draw(Batch batch, float parentAlpha) {
            if (region == null) return;
            Color color = getColor();
            batch.setColor(color.r, color.g, color.b, color.a * parentAlpha);
            batch.draw(
                    region,
                    getX(), getY(),
                    getOriginX(), getOriginY(),
                    getWidth(), getHeight(),
                    getScaleX(), getScaleY(),
                    getRotation()
            );
        }
    }
}
