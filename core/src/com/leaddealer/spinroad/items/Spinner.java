package com.leaddealer.spinroad.items;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Circle;
import com.leaddealer.spinroad.*;

import java.util.ArrayList;

public class Spinner {
    private int mWidth;
    private int mHeight;

    public final int SPINNER_SIZE = 512;

    public boolean isAlive = true;

    private Circle circle;
    private float angle;

    private ArrayList<TextureRegion> spinners;
    private ArrayList<TextureRegion> shields;

    public Spinner(int width, int height) {
        mWidth = width;
        mHeight = height;

        spinners = new ArrayList<TextureRegion>();
        spinners.add(new TextureRegion(new Texture("spin0.png")));
        spinners.add(new TextureRegion(new Texture("spin1.png")));
        spinners.add(new TextureRegion(new Texture("spin2.png")));
        spinners.add(new TextureRegion(new Texture("spin3.png")));
        spinners.add(new TextureRegion(new Texture("spin4.png")));
        spinners.add(new TextureRegion(new Texture("spin5.png")));
        spinners.add(new TextureRegion(new Texture("spin6.png")));
        spinners.add(new TextureRegion(new Texture("spin7.png")));

        shields = new ArrayList<TextureRegion>();
        shields.add(new TextureRegion(new Texture("shield0.png")));
        shields.add(new TextureRegion(new Texture("shield1.png")));
        shields.add(new TextureRegion(new Texture("shield2.png")));
        shields.add(new TextureRegion(new Texture("shield3.png")));
        shields.add(new TextureRegion(new Texture("shield4.png")));
        shields.add(new TextureRegion(new Texture("shield5.png")));

        circle = new Circle();

        circle.x = mWidth/2;
        circle.y = mHeight/2;
        circle.radius = SPINNER_SIZE/2;
    }

    public void move() {
        if(isAlive) {
            circle.x += SpinnerSpeedController.getSpeedX() * Gdx.graphics.getDeltaTime();
            circle.y += SpinnerSpeedController.getSpeedY() * Gdx.graphics.getDeltaTime();
            angle += (Math.abs(SpinnerSpeedController.getSpeedX())
                    + Math.abs(SpinnerSpeedController.getSpeedY())) * Gdx.graphics.getDeltaTime() * 2;

            angle %= 360;
            while (angle < 0)
                angle += 360;

            if (circle.x < SPINNER_SIZE / 2) {
                circle.x = SPINNER_SIZE / 2;
                SpinnerSpeedController.invertX();
            }
            if (circle.x > mWidth - SPINNER_SIZE / 2) {
                circle.x = mWidth - SPINNER_SIZE / 2;
                SpinnerSpeedController.invertX();
            }

            if (circle.y < SPINNER_SIZE / 2) {
                circle.y = SPINNER_SIZE / 2;
                SpinnerSpeedController.invertY();
            }
            if (circle.y > mHeight - SPINNER_SIZE / 2) {
                circle.y = mHeight - SPINNER_SIZE / 2;
                SpinnerSpeedController.invertY();
            }

            SpinnerSpeedController.slowdown();
        }
    }

    public void draw(SpriteBatch batch) {
        if(isAlive) {
            batch.draw(spinners.get(Constants.spinner), circle.x - SPINNER_SIZE / 2, circle.y - SPINNER_SIZE / 2,
                    SPINNER_SIZE / 2, SPINNER_SIZE / 2, SPINNER_SIZE, SPINNER_SIZE, 1, 1, angle);

            if (ItemCollection.isShieldActive)
                batch.draw(shields.get(Constants.shield), circle.x - SPINNER_SIZE / 2, circle.y - SPINNER_SIZE / 2,
                        SPINNER_SIZE / 2, SPINNER_SIZE / 2, SPINNER_SIZE, SPINNER_SIZE, 1, 1, angle);
        }
    }

    public void dispose() {
        for(TextureRegion region: spinners)
            region.getTexture().dispose();

        for(TextureRegion region: shields)
            region.getTexture().dispose();
    }

    public Circle getCircle() {
        return circle;
    }

    public void reset() {
        circle.x = mWidth/2;
        circle.y = mHeight/2;
        isAlive = true;
    }
}
