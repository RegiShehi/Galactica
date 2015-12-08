package com.shehi.regi.androidinvaders;

import com.shehi.regi.framework.DynamicGameObject3D;

import java.util.Random;

public class Coin extends DynamicGameObject3D {
    static final int COIN_ALIVE = 0;
    static final int COIN_DEAD = 1;
    static final float COIN_RADIUS = 0.75f;

    int state = COIN_ALIVE;
    float stateTime = 0;
    Random rand;

    public Coin(float x, float y, float z) {
        super(x, y, z, COIN_RADIUS);
        velocity.z = (int) (Math.random() * 7 + 5);
        rand = new Random();
    }

    public void update(float deltaTime) {
        if (state == COIN_ALIVE) {
            position.z += velocity.z * deltaTime;
            bounds.center.set(position);
            if (position.z > 100) {
                position.set((float) Math.random() * World.WORLD_MAX_X, 0, -100);
                velocity.z = (int) (Math.random() * 7 + 5);
            }
        }
        stateTime += deltaTime;
    }
}