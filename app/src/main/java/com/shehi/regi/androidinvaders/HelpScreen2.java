package com.shehi.regi.androidinvaders;

import com.shehi.regi.framework.Game;
import com.shehi.regi.framework.Input;
import com.shehi.regi.framework.gl.Camera2D;
import com.shehi.regi.framework.gl.GLScreen;
import com.shehi.regi.framework.gl.SpriteBatcher;
import com.shehi.regi.framework.math.OverlapTester;
import com.shehi.regi.framework.math.Rectangle;
import com.shehi.regi.framework.math.Vector2;

import java.util.List;

import javax.microedition.khronos.opengles.GL10;

public class HelpScreen2 extends GLScreen {
    Camera2D guiCam;
    SpriteBatcher batcher;
    Rectangle backBounds;
    Rectangle nextBounds;
    Vector2 touchPoint;
    String textString;
    String textString1;
    String textString2;
    String textString3;

    public HelpScreen2(Game game) {
        super(game);

        guiCam = new Camera2D(glGraphics, 480, 320);
        backBounds = new Rectangle(0, 0, 64, 64);
        nextBounds = new Rectangle(416, 0, 64, 64);
        touchPoint = new Vector2();
        batcher = new SpriteBatcher(glGraphics, 100);
        textString = "3. Touch on the screen to";
        textString1 = "shoot missiles";
        textString2 = "4. Grab the coin to score";
        textString3 = "10 extra points";
    }

    @Override
    public void update(float deltaTime) {

        List<Input.TouchEvent> events = game.getInput().getTouchEvents();
        int len = events.size();
        for (int i = 0; i < len; i++) {
            Input.TouchEvent event = events.get(i);
            if (event.type != Input.TouchEvent.TOUCH_UP)
                continue;
            guiCam.touchToWorld(touchPoint.set(event.x, event.y));
            if (OverlapTester.pointInRectangle(backBounds, touchPoint)) {
                game.setScreen(new MainMenuScreen(game));
                Assets.playSound(Assets.clickSound);
                return;
            }
            if (OverlapTester.pointInRectangle(nextBounds, touchPoint)) {
                game.setScreen(new HelpScreen3(game));
                Assets.playSound(Assets.clickSound);
                return;
            }
        }
    }

    @Override
    public void present(float deltaTime) {
        GL10 gl = glGraphics.getGL();
        gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
        guiCam.setViewportAndMatrices();

        gl.glEnable(GL10.GL_TEXTURE_2D);

        batcher.beginBatch(Assets.background);
        batcher.drawSprite(240, 160, 480, 320, Assets.backgroundRegion);
        batcher.endBatch();

        gl.glEnable(GL10.GL_BLEND);
        gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);

        batcher.beginBatch(Assets.items);
        Assets.font.drawText(batcher, textString, 30, 240);
        Assets.font.drawText(batcher, textString1, 130, 240-30);

        Assets.font.drawText(batcher, textString2, 30, 240-30-70);
        Assets.font.drawText(batcher, textString3, 120, 240-30-70-30);

        batcher.drawSprite(32, 32, 64, 64, Assets.leftRegion);
        batcher.drawSprite(480-32, 32, -64, 64, Assets.leftRegion);
        batcher.endBatch();

        gl.glDisable(GL10.GL_BLEND);
    }

    @Override
    public void resume() {
    }

    @Override
    public void pause() {
    }

    @Override
    public void dispose() {
    }
}

