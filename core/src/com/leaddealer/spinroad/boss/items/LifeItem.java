package com.leaddealer.spinroad.boss.items;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import com.leaddealer.spinroad.animations.BonusAnimation;
import com.leaddealer.spinroad.animations.CrashAnimation;
import com.leaddealer.spinroad.animations.ItemAnimation;
import com.leaddealer.spinroad.boss.BossItem;
import com.leaddealer.spinroad.items.Spinner;

public class LifeItem extends BossItem {

    private TextureRegion itemImage;
    private Circle circle;
    private Sound sound;

    public LifeItem(float Xo, float Yo, float finalX, float finalY) {
        super(200, 200, Xo, Yo, finalX, finalY);
        itemImage = new TextureRegion(new Texture("life.png"));

        circle = new Circle();
        circle.radius = mWidth;
        sound = Gdx.audio.newSound(Gdx.files.internal("bonus.wav"));
    }

    @Override
    public TextureRegion getItemImage() {
        return itemImage;
    }

    @Override
    public boolean overlapsSpinner(Spinner spinner) {
        circle.x = x + mWidth/2;
        circle.y = y + mWidth/2;
        boolean overlaps = Intersector.overlaps(spinner.getCircle(), circle);
        if(overlaps)
            playSound();
        return overlaps;
    }

    @Override
    public Rectangle getExplosionRectangle() {
        Rectangle explosionRect = new Rectangle();
        explosionRect.width = mWidth;
        explosionRect.height = mWidth;
        explosionRect.x = x;
        explosionRect.y = y;
        return explosionRect;
    }

    public ItemAnimation getAnimation(){
        return new BonusAnimation(getExplosionRectangle());
    }

    @Override
    public void dispose() {
        isDisposed = true;
        itemImage.getTexture().dispose();
        sound.dispose();
    }

    private void playSound() {
        sound.play();
    }
}

