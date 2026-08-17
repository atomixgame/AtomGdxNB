package com.atomgdx.editor.scene2d.runtime;

import com.atomgdx.editor.scene2d.data.vo.MainItemVO;
import com.atomgdx.editor.scene2d.data.vo.PhysicsBodyDataVO;
import com.atomgdx.editor.scene2d.data.vo.PolygonVO;
import com.atomgdx.editor.scene2d.data.vo.ShapeVO;
import com.badlogic.gdx.math.Vector2;

/**
 * Utility helper to convert HyperLap2D physics data to Box2D definitions.
 */
public class Physics2DWorldHelper {

    public static class BodyDefConfig {
        public int type; // 0=Static, 1=Kinematic, 2=Dynamic
        public Vector2 position = new Vector2();
        public float angle = 0f;
        public float linearDamping = 0f;
        public float angularDamping = 0f;
        public boolean allowSleep = true;
        public boolean awake = true;
        public boolean fixedRotation = false;
        public boolean bullet = false;
        public float gravityScale = 1f;
    }

    public static class FixtureDefConfig {
        public float density = 1f;
        public float friction = 0.2f;
        public float restitution = 0f;
        public boolean isSensor = false;
        public short categoryBits = 0x0001;
        public short maskBits = -1;
        public short groupIndex = 0;
    }

    public static BodyDefConfig createBodyDef(MainItemVO item) {
        if (item == null || item.physics == null) return null;
        PhysicsBodyDataVO p = item.physics;
        BodyDefConfig def = new BodyDefConfig();
        def.type = p.bodyType;
        def.position.set(item.x, item.y);
        def.angle = (float) Math.toRadians(item.rotation);
        def.allowSleep = p.allowSleep;
        def.awake = p.awake;
        def.fixedRotation = p.fixedRotation;
        def.bullet = p.bullet;
        def.gravityScale = p.gravityScale;
        return def;
    }

    public static FixtureDefConfig createFixtureDef(PhysicsBodyDataVO physics) {
        if (physics == null) return null;
        FixtureDefConfig fix = new FixtureDefConfig();
        fix.density = physics.density;
        fix.friction = physics.friction;
        fix.restitution = physics.restitution;
        fix.isSensor = physics.sensor;
        fix.categoryBits = physics.categoryBits;
        fix.maskBits = physics.maskBits;
        fix.groupIndex = physics.groupIndex;
        return fix;
    }
}
