package com.shehi.regi.androidinvaders;

import com.shehi.regi.framework.DynamicGameObject3D;

import java.util.Random;

public class Invader extends DynamicGameObject3D {
    static final int INVADER_ALIVE = 0;
    static final int INVADER_DEAD = 1;
    static final float INVADER_RADIUS = 0.75f;

    int state = INVADER_ALIVE;
    float stateTime = 0;
    Random rand;

    public Invader(float x, float y, float z) {
        super(x, y, z, INVADER_RADIUS);
        velocity.z = (int) (Math.random() * 7 + 5);
        rand = new Random();
    }

    public void update(float deltaTime) {
        if (state == INVADER_ALIVE) {
            position.z += velocity.z * deltaTime;
            bounds.center.set(position);
            if (position.z > World.WORLD_MAX_Z) {
                position.set((float) Math.random() * World.WORLD_MAX_X, 0, World.WORLD_MIN_Z);
                velocity.z = (int) (Math.random() * 7 + 5);
            }
        }
        stateTime += deltaTime;
    }
}
