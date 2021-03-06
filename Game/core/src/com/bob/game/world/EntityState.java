package com.bob.game.world;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public enum EntityState {
    IDLE_RIGHT (Textures.BOB_W_EAST),
    IDLE_DOWN (Textures.BOB_W_SOUTH),
    IDLE_LEFT (Textures.BOB_W_WEST),
    IDLE_UP (Textures.BOB_W_NORTH),
    WALK_RIGHT (Textures.BOB_W_EAST, 1, 0),
    WALK_DOWN (Textures.BOB_W_SOUTH, 0, -1),
    WALK_LEFT (Textures.BOB_W_WEST, -1, 0),
    WALK_UP (Textures.BOB_W_NORTH, 0, 1),
    WET (Textures.BOB_WET),
    WON (Textures.BOB_WON, 0, 0),
    LIGHT (Textures.LIGHT, 0, 0),
    BOAT (Textures.BOAT, 0, 0),
    CONFUSED(Textures.BOB_CONFUSED, 0, 0);

    private final Animation animation;
    private final float dx;
    private final float dy;

    EntityState(Textures text) {
        this.animation = new Animation(text.getSpeed(), text.getTexture().findRegions("0001"));
        this.dx = 0;
        this.dy = 0;
    }

    EntityState(Textures text, float dx, float dy) {
        this.animation = new Animation(text.getSpeed(), text.getTexture().getRegions());
        this.dx = dx;
        this.dy = dy;
    }

    public TextureRegion getFrame(float stateTime) {
        return this.animation.getKeyFrame(stateTime, true);
    }

    public float getDx() {
        return this.dx;
    }

    public float getDy() {
        return this.dy;
    }

}
