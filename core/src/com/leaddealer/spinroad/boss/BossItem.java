package com.leaddealer.spinroad.boss;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.leaddealer.spinroad.SpinRoadGame;
import com.leaddealer.spinroad.animations.ItemAnimation;
import com.leaddealer.spinroad.items.Spinner;

public abstract class BossItem {
    public boolean isDisposed = false;
    protected int mWidth = 4*237/3;
    protected int mHeight = 4*100/3;

    private float Xo, Yo, finalX, finalY;
    protected float x, y;

    protected float itemSpeed;
    protected float iteration = 1;

    public BossItem(float Xo, float Yo, float finalX, float finalY) {
        this.Xo = Xo - mWidth/6;
        this.Yo = Yo - mHeight/6;
        this.finalX = finalX;
        this.finalY = finalY;

        itemSpeed = MathUtils.random(90, 110);
    }

    public BossItem(int width, int height, float Xo, float Yo, float finalX, float finalY) {
        mWidth = width;
        mHeight = height;
        this.Xo = Xo - mWidth/6;
        this.Yo = Yo - mHeight/6;
        this.finalX = finalX;
        this.finalY = finalY;

        itemSpeed = MathUtils.random(90, 110);
    }

    abstract public TextureRegion getItemImage();
    abstract public boolean overlapsSpinner(Spinner spinner);
    abstract public Rectangle getExplosionRectangle();
    public abstract ItemAnimation getAnimation();
    public abstract void dispose();

    public void draw(SpriteBatch batch) {
        float percent = (30 + 7*iteration/10)/itemSpeed;
        batch.draw(getItemImage(), x, y, percent * mWidth, percent * mHeight);
    }

    public boolean move() {
        iteration += 0.8;
        x = Xo + iteration * (finalX - Xo)/itemSpeed;
        y = Yo + iteration * (finalY - Yo)/itemSpeed;

        return y + mHeight < 0 || x + mWidth < 0 || x > SpinRoadGame.mWidth;
    }
}
