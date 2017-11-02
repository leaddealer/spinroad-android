package com.leaddealer.spinroad.items;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import com.leaddealer.spinroad.ItemCollection;
import com.leaddealer.spinroad.animations.ItemAnimation;
import com.leaddealer.spinroad.animations.BonusAnimation;

public class ItemSmoothie extends Item{
    private final int RADIUS = 128;

    private Circle circle;
    private TextureRegion itemImage;

    private Sound sound;

    public ItemSmoothie(int screenWidth, int screenHeight) {
        super(screenWidth, screenHeight);
        itemImage = new TextureRegion(new Texture("smoothie.png"));

        circle = new Circle();
        circle.radius = RADIUS;
        setSide(circle);
        sound = Gdx.audio.newSound(Gdx.files.internal("bonus.wav"));
    }

    @Override
    public boolean move() {
        circle.x = x;
        circle.y = y;

        return circle.y + RADIUS < 0 || circle.x + RADIUS < 0 || circle.x - RADIUS > mWidth;
    }

    @Override
    public void draw(SpriteBatch batch) {
        itemAngle += 8 * ItemCollection.itemSpeed;
        itemAngle %= 360;
        while (itemAngle < 0)
            itemAngle+=360;
        batch.draw(itemImage, circle.x - RADIUS, circle.y - RADIUS, RADIUS, RADIUS, 2 * RADIUS, 2 * RADIUS, 1, 1, itemAngle);
    }

    @Override
    public boolean overlapsSpinner(Spinner spinner){
        boolean overlaps = Intersector.overlaps(spinner.getCircle(), circle);
        if(overlaps)
            playSound();
        return overlaps;
    }

    private void playSound() {
        sound.play();
    }

    @Override
    public Rectangle getExplosionRectangle() {
        Rectangle explosionRect = new Rectangle();
        explosionRect.width = RADIUS * 2;
        explosionRect.height = RADIUS * 2;
        explosionRect.x = circle.x - RADIUS;
        explosionRect.y = circle.y - RADIUS;
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
}
