package com.shehi.regi.androidinvaders;

import com.shehi.regi.framework.math.OverlapTester;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class World {
    public interface WorldListener {
        public void explosion();

        public void shot();

        public void coin();

        public void laser();

        public void hithit();
    }

    final static float WORLD_MIN_X = 0;
    final static float WORLD_MAX_X = 16;
    final static float WORLD_MIN_Z = -15;
    final static float WORLD_MAX_Z = 2;

    WorldListener listener;
    int score = 0;
    final static int NUM_INVADERS = 8;
    final Invader[] invaders;
    final List<Shot> shots = new ArrayList<Shot>();
    final Ship ship;
    final Coin coin;
    long lastShotTime;
    long powerUpTime;
    Random random;

    public World() {
        ship = new Ship(8, 0, 0);
        invaders = new Invader[NUM_INVADERS];
        coin = new Coin((float) Math.random() * WORLD_MAX_X, 0, -20);
        generateInvaders();
        lastShotTime = System.nanoTime();
        powerUpTime = System.nanoTime();
        random = new Random();
    }

    private void generateInvaders() {
        for (int i = 0; i < NUM_INVADERS; i++) {
            invaders[i] = new Invader((float) Math.random() * WORLD_MAX_X, 0, WORLD_MIN_Z);
        }
    }

    public void setWorldListener(WorldListener worldListener) {
        this.listener = worldListener;
    }

    public void update(float deltaTime, float accelX) {
        ship.update(deltaTime, accelX);
        coin.update(deltaTime);
        updateInvaders(deltaTime);
        updateShots(deltaTime);

        checkShotCollisions();
        checkInvaderCollisions();
        checkCoinCollision();
        updateScore();
    }

    private void updateInvaders(float deltaTime) {
        for (int i = 0; i < NUM_INVADERS; i++) {
            invaders[i].update(deltaTime);

            if (invaders[i].state == Invader.INVADER_ALIVE) {
                if (random.nextFloat() < 0.002f) {
                    Shot shot = new Shot(invaders[i].position.x,
                            invaders[i].position.y,
                            invaders[i].position.z,
                            Shot.SHOT_VELOCITY);
                    shots.add(shot);
                }
            }
        }
    }

    private void updateShots(float deltaTime) {
        int len = shots.size();
        for (int i = 0; i < len; i++) {
            Shot shot = shots.get(i);
            shot.update(deltaTime);
            if (shot.position.z < WORLD_MIN_Z ||
                    shot.position.z > 2) {
                shots.remove(i);
                i--;
                len--;
            }
        }
    }

    private void checkInvaderCollisions() {
        if (ship.state == Ship.SHIP_EXPLODING)
            return;

        for (int i = 0; i < NUM_INVADERS; i++) {
            if (OverlapTester.overlapSpheres(ship.bounds, invaders[i].bounds)) {
                ship.lives = 1;
                ship.kill();
                listener.explosion();
                return;
            }
        }
    }

    private void checkCoinCollision() {
        if (ship.state == Ship.SHIP_EXPLODING)
            return;
        if (OverlapTester.overlapSpheres(ship.bounds, coin.bounds) && ship.state == Ship.SHIP_ALIVE) {
            score += 10;
            coin.position.set((float) Math.random() * World.WORLD_MAX_X, 0, -100);
            coin.velocity.z = (int) (Math.random() * 8 + 4);
            listener.coin();
            return;
        }
    }

    private void checkShotCollisions() {
        int len = shots.size();
        for (int i = 0; i < len; i++) {
            Shot shot = shots.get(i);
            boolean shotRemoved = false;

            if (shotRemoved)
                continue;

            if (shot.velocity.z < 0) {
                for (int j = 0; j < NUM_INVADERS; j++) {
                    if (OverlapTester.overlapSpheres(invaders[j].bounds, shot.bounds)) {
                        listener.laser();
                        invaders[j].position.set((float) Math.random() * World.WORLD_MAX_X, 0, World.WORLD_MIN_Z - 2);
                        invaders[j].velocity.z = (int) (Math.random() * 7 + 5);
                        shots.remove(i);
                        i--;
                        len--;
                        break;
                    }
                }
            } else {
                if (OverlapTester.overlapSpheres(shot.bounds, ship.bounds)
                        && ship.state == Ship.SHIP_ALIVE) {
                    ship.lives--;
                    listener.hithit();
                    if (ship.lives == 0) {
                        ship.kill();
                        listener.explosion();
                    }
                    shots.remove(i);
                    i--;
                    len--;
                }
            }
        }
    }


    public boolean isGameOver() {
        return ship.lives == 0;
    }

    public void shoot() {
        if (ship.state == Ship.SHIP_EXPLODING)
            return;

        int friendlyShots = 0;
        int len = shots.size();
        for (int i = 0; i < len; i++) {
            if (shots.get(i).velocity.z < 0)
                friendlyShots++;
        }

        if (System.nanoTime() - lastShotTime > 1000000000 || friendlyShots == 0) {
            shots.add(new Shot(ship.position.x, ship.position.y,
                    ship.position.z, -Shot.SHOT_VELOCITY));
            lastShotTime = System.nanoTime();
            listener.shot();
        }

        if (score % 50 == 0) {
            if (System.nanoTime() - lastShotTime > 5000000 || friendlyShots == 0) {
                shots.add(new Shot(ship.position.x, ship.position.y,
                        ship.position.z, -Shot.SHOT_VELOCITY));
                lastShotTime = System.nanoTime();
                listener.shot();
            }
        }
    }

    private void updateScore() {

        if ((ship.position.x == World.WORLD_MAX_X) && !ship.touchright) {
            score += 10;
            ship.touchright = true;
            ship.touchleft = false;
        }

        if ((ship.position.x == World.WORLD_MIN_X) && !ship.touchleft) {
            score += 10;
            ship.touchright = false;
            ship.touchleft = true;
        }
    }
}
