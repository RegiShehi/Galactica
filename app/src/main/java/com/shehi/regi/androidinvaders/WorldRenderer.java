package com.shehi.regi.androidinvaders;

import com.shehi.regi.framework.gl.AmbientLight;
import com.shehi.regi.framework.gl.Animation;
import com.shehi.regi.framework.gl.DirectionalLight;
import com.shehi.regi.framework.gl.LookAtCamera;
import com.shehi.regi.framework.gl.SpriteBatcher;
import com.shehi.regi.framework.gl.TextureRegion;
import com.shehi.regi.framework.impl.GLGraphics;
import com.shehi.regi.framework.math.Vector3;

import java.util.List;

import javax.microedition.khronos.opengles.GL10;

public class WorldRenderer {
    GLGraphics glGraphics;
    LookAtCamera camera;
    AmbientLight ambientLight;
    DirectionalLight directionalLight;
    SpriteBatcher batcher;
    float invaderAngle = 0;
    float coinAngle = 0;

    public WorldRenderer(GLGraphics glGraphics) {
        this.glGraphics = glGraphics;
        camera = new LookAtCamera(67, glGraphics.getWidth()
                / (float) glGraphics.getHeight(), 0.1f, 100);
        camera.getPosition().set(0, 8, 2);
        camera.getLookAt().set(8, 0, -4);
        ambientLight = new AmbientLight();
        ambientLight.setColor(0.2f, 0.2f, 0.2f, 1.0f);
        directionalLight = new DirectionalLight();
        directionalLight.setDirection(-1, -0.5f, 0);
        batcher = new SpriteBatcher(glGraphics, 10);
    }

    public void render(World world, float deltaTime) {
        GL10 gl = glGraphics.getGL();
        camera.getPosition().x = 8;
        camera.getLookAt().x = 8;
        camera.setMatrices(gl);

        gl.glEnable(GL10.GL_DEPTH_TEST);
        gl.glEnable(GL10.GL_TEXTURE_2D);
        gl.glEnable(GL10.GL_LIGHTING);
        gl.glEnable(GL10.GL_COLOR_MATERIAL);
        ambientLight.enable(gl);
        directionalLight.enable(gl, GL10.GL_LIGHT0);

        renderShip(gl, world.ship);
        renderInvaders(gl, world.invaders, deltaTime);
        renderCoin(gl, world.coin, deltaTime);

        gl.glDisable(GL10.GL_TEXTURE_2D);

        renderShots(gl, world.shots);

        gl.glDisable(GL10.GL_COLOR_MATERIAL);
        gl.glDisable(GL10.GL_LIGHTING);
        gl.glDisable(GL10.GL_DEPTH_TEST);
    }

    private void renderShip(GL10 gl, Ship ship) {
        if (ship.state == Ship.SHIP_EXPLODING) {
            gl.glDisable(GL10.GL_LIGHTING);
            renderExplosion(gl, ship.position, ship.stateTime);
            gl.glEnable(GL10.GL_LIGHTING);
        } else {
            Assets.shipTexture.bind();
            Assets.shipModel.bind();
            gl.glPushMatrix();
            gl.glTranslatef(ship.position.x, ship.position.y, ship.position.z);
            gl.glRotatef(ship.velocity.x / Ship.SHIP_VELOCITY * 90, 0, 0, -1);
            Assets.shipModel.draw(GL10.GL_TRIANGLES, 0,
                    Assets.shipModel.getNumVertices());
            gl.glPopMatrix();
            Assets.shipModel.unbind();
        }
    }

    private void renderCoin(GL10 gl, Coin coin, float deltaTime) {
        coinAngle += 45 * deltaTime * 2;

        Assets.coinTexture.bind();
        Assets.coinModel.bind();
        if (coin.state == Coin.COIN_DEAD) {
            gl.glDisable(GL10.GL_LIGHTING);
            Assets.coinModel.unbind();
            Assets.coinModel.bind();
            gl.glEnable(GL10.GL_LIGHTING);
        } else {
            gl.glPushMatrix();
            gl.glTranslatef(coin.position.x, coin.position.y,
                    coin.position.z);
            gl.glRotatef(coinAngle, 0, 1, 0);
            Assets.coinModel.draw(GL10.GL_TRIANGLES, 0,
                    Assets.coinModel.getNumVertices());
            gl.glPopMatrix();
        }
        Assets.coinModel.unbind();
    }

    private void renderInvaders(GL10 gl, Invader[] invaders, float deltaTime) {
        invaderAngle += 45 * deltaTime;

        Assets.invaderTexture.bind();
        Assets.invaderModel.bind();
        for (int i = 0; i < World.NUM_INVADERS; i++) {
            if (invaders[i].state == Invader.INVADER_DEAD) {
                gl.glDisable(GL10.GL_LIGHTING);
                Assets.invaderModel.unbind();
                renderExplosion(gl, invaders[i].position, invaders[i].stateTime);
                Assets.invaderTexture.bind();
                Assets.invaderModel.bind();
                gl.glEnable(GL10.GL_LIGHTING);
            } else {
                gl.glPushMatrix();
                gl.glTranslatef(invaders[i].position.x, invaders[i].position.y,
                        invaders[i].position.z);
                gl.glRotatef(invaderAngle, 0, 1, 0);
                Assets.invaderModel.draw(GL10.GL_TRIANGLES, 0,
                        Assets.invaderModel.getNumVertices());
                gl.glPopMatrix();
            }
        }
        Assets.invaderModel.unbind();
    }

    private void renderShots(GL10 gl, List<Shot> shots) {
        gl.glColor4f(1, 1, 0, 1);
        Assets.shotModel.bind();
        int len = shots.size();
        for (int i = 0; i < len; i++) {
            Shot shot = shots.get(i);
            gl.glPushMatrix();
            gl.glTranslatef(shot.position.x, shot.position.y, shot.position.z);
            Assets.shotModel.draw(GL10.GL_TRIANGLES, 0,
                    Assets.shotModel.getNumVertices());
            gl.glPopMatrix();
        }
        Assets.shotModel.unbind();
        gl.glColor4f(1, 1, 1, 1);
    }

    private void renderExplosion(GL10 gl, Vector3 position, float stateTime) {
        TextureRegion frame = Assets.explosionAnim.getKeyFrame(stateTime,
                Animation.ANIMATION_NONLOOPING);

        gl.glEnable(GL10.GL_BLEND);
        gl.glPushMatrix();
        gl.glTranslatef(position.x, position.y, position.z);
        batcher.beginBatch(Assets.explosionTexture);
        batcher.drawSprite(0, 0, 2, 2, frame);
        batcher.endBatch();
        gl.glPopMatrix();
        gl.glDisable(GL10.GL_BLEND);
    }
}
