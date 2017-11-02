package com.leaddealer.spinroad.boss.items;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.leaddealer.spinroad.animations.CrashAnimation;
import com.leaddealer.spinroad.animations.ItemAnimation;
import com.leaddealer.spinroad.boss.BossItem;
import com.leaddealer.spinroad.items.Spinner;


public class TextItem extends BossItem {

    private TextureRegion itemImage;
    private Rectangle rectangle;
    private Sound sound;

    public TextItem(float Xo, float Yo, float finalX, float finalY) {
        super(Xo, Yo, finalX, finalY);
        int probability = MathUtils.random(0, 5);
        if(probability == 0)
            itemImage = new TextureRegion(new Texture("hype.png"));
        else if(probability == 1)
            itemImage = new TextureRegion(new Texture("yolo.png"));
        else if(probability == 2)
            itemImage = new TextureRegion(new Texture("swag.png"));
        else if(probability == 3)
            itemImage = new TextureRegion(new Texture("lol.png"));
        else
            itemImage = new TextureRegion(new Texture("spinner.png"));

        rectangle = new Rectangle();
        rectangle.width = mWidth;
        rectangle.height = mHeight;
        sound = Gdx.audio.newSound(Gdx.files.internal("explosion.wav"));
    }

    @Override
    public TextureRegion getItemImage() {
        return itemImage;
    }

    @Override
    public boolean overlapsSpinner(Spinner spinner) {
        rectangle.x = x;
        rectangle.y = y;
        boolean overlaps = Intersector.overlaps(spinner.getCircle(), rectangle);
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
        explosionRect.y = y - (mWidth/2 - mHeight/2);
        return explosionRect;
    }

    public ItemAnimation getAnimation(){
        return new CrashAnimation(getExplosionRectangle());
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
