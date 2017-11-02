package com.leaddealer.spinroad.animations;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public abstract class ItemAnimation {

    protected Animation animation;
    protected Texture textureSheet;
    protected int x;
    protected int y;
    protected int width;
    protected int height;
    protected float stateTime = 0f;

    public abstract boolean draw(SpriteBatch batch);

    public void dispose(){
        textureSheet.dispose();
    }
}
