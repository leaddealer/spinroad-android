package com.leaddealer.spinroad.items;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import com.leaddealer.spinroad.ItemCollection;
import com.leaddealer.spinroad.animations.CrashAnimation;

public class ItemLong extends Item {
    private final int WIDTH = 125;
    private final int HEIGHT = 320;

    private Rectangle rectangle;
    private TextureRegion itemImage;

    private Sound sound;

    public ItemLong(int screenWidth, int screenHeight) {
        super(screenWidth, screenHeight);
        itemImage = new TextureRegion(new Texture("long.png"));

        rectangle = new Rectangle();
        rectangle.width = WIDTH;
        rectangle.height = HEIGHT;
        setSide(rectangle);
        sound = Gdx.audio.newSound(Gdx.files.internal("explosion.wav"));
    }

    @Override
    public boolean move() {
        rectangle.x = x;
        rectangle.y = y;
        return rectangle.y + HEIGHT < 0 || rectangle.x + WIDTH < 0 || rectangle.x > mWidth;
    }

    @Override
    public void draw(SpriteBatch batch) {
        itemAngle += 8 * ItemCollection.itemSpeed;
        itemAngle %= 360;
        while (itemAngle < 0)
            itemAngle+=360;
        batch.draw(itemImage, rectangle.x, rectangle.y, WIDTH/2, HEIGHT/2, WIDTH, HEIGHT, 1, 1, itemAngle);
    }

    @Override
    public boolean overlapsSpinner(Spinner spinner){
        boolean overlaps = Intersector.overlaps(spinner.getCircle(), rectangle);
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
        explosionRect.width = HEIGHT;
        explosionRect.height = HEIGHT;
        explosionRect.x = rectangle.x - (HEIGHT/2 - WIDTH/2);
        explosionRect.y = rectangle.y;
        return explosionRect;
    }

    public com.leaddealer.spinroad.animations.ItemAnimation getAnimation(){
        return new CrashAnimation(getExplosionRectangle());
    }

    @Override
    public void dispose() {
        isDisposed = true;
        itemImage.getTexture().dispose();
        sound.dispose();
    }
}
