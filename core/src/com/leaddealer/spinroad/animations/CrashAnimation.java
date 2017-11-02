package com.leaddealer.spinroad.animations;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;

public class CrashAnimation extends ItemAnimation{

    public CrashAnimation(Rectangle rectangle) {
        this.x = (int) (rectangle.x) - (int) rectangle.width / 2;
        this.y = (int) (rectangle.y) - (int) rectangle.height / 2;
        this.width = 2 * (int) rectangle.width;
        this.height = 2 * (int) rectangle.height;

        int FRAME_COLS = 3;
        int FRAME_ROWS = 3;
        textureSheet = new Texture(Gdx.files.internal("explosion.png"));
        TextureRegion[][] textureRegions = TextureRegion.split(textureSheet, textureSheet.getWidth()/FRAME_COLS, textureSheet.getHeight()/FRAME_ROWS);
        TextureRegion[] explosionFrames = new TextureRegion[FRAME_COLS * FRAME_ROWS];
        int index = 0;
        for (int i = 0; i < FRAME_ROWS; i++) {
            for (int j = 0; j < FRAME_COLS; j++) {
                explosionFrames[index++] = textureRegions[i][j];
            }
        }
        animation = new Animation(0.05f, explosionFrames);
    }

    public boolean draw(SpriteBatch batch){
        stateTime += Gdx.graphics.getDeltaTime();
        if(stateTime < animation.getKeyFrames().length * 0.05f){
            TextureRegion currentFrame = (TextureRegion) animation.getKeyFrame(stateTime, true);
            batch.draw(currentFrame, x, y, width, height);
            return true;
        } else
            return false;
    }
}
