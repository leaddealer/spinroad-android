package com.leaddealer.spinroad.boss;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import com.leaddealer.spinroad.ItemCollection;
import com.leaddealer.spinroad.SpinRoadGame;
import com.leaddealer.spinroad.items.Spinner;

public class Head {
    public boolean onScreen = true;
    public boolean goBack = false;

    private int WIDTH = 2;
    private int HEIGHT = 3;

    private final int HORIZONTAL_SPEED = 4;

    private TextureRegion lifeLine;
    private TextureRegion headImageClosed;
    private TextureRegion headImageOpened;
    private Rectangle rectangle;
    private Circle circleOverlaps1;
    private Circle circleOverlaps2;
    private boolean leftSide = true;
    private boolean isMouthOpen = false;

    public Head() {
        WIDTH = 2*SpinRoadGame.mWidth/5;
        HEIGHT = 3*WIDTH/2;

        lifeLine = new TextureRegion(new Texture("life_line.png"));
        headImageClosed = new TextureRegion(new Texture("head_closed.png"));
        headImageOpened = new TextureRegion(new Texture("head_open.png"));

        rectangle = new Rectangle();
        rectangle.width = WIDTH;
        rectangle.height = HEIGHT;
        rectangle.x = SpinRoadGame.mWidth/2 - WIDTH/2;
        rectangle.y = SpinRoadGame.mHeight;

        circleOverlaps1 = new Circle();
        circleOverlaps2 = new Circle();
        circleOverlaps1.radius = WIDTH/2;
        circleOverlaps2.radius = 3*WIDTH/8;
    }

    public void draw(SpriteBatch batch, float percent) {
        move();
        if(isMouthOpen)
            batch.draw(headImageOpened, rectangle.x, rectangle.y, rectangle.width, rectangle.height);
        else
            batch.draw(headImageClosed, rectangle.x, rectangle.y, rectangle.width, rectangle.height);

        batch.draw(lifeLine,
                rectangle.x + (1-percent) * rectangle.width/2,
                rectangle.y + rectangle.height + WIDTH/30,
                percent * rectangle.width, WIDTH/12);
    }

    public void move() {
        if(goBack) {
            if (rectangle.x < SpinRoadGame.mWidth/2 - WIDTH/2 - HORIZONTAL_SPEED){
                rectangle.x += HORIZONTAL_SPEED;
            } else if (rectangle.x > SpinRoadGame.mWidth/2 - WIDTH/2 + HORIZONTAL_SPEED) {
                rectangle.x -= HORIZONTAL_SPEED;
            } else {
                rectangle.y += 5;
            }

            if(rectangle.y >= SpinRoadGame.mHeight){
                onScreen = false;
                ItemCollection.lastBossScore = ItemCollection.score;
            }
        } else {
            if (!isReadyToAttack())
                rectangle.y -= 5;
            else {
                if (leftSide) {
                    rectangle.x -= HORIZONTAL_SPEED;
                    if (rectangle.x <= SpinRoadGame.mWidth / 20)
                        leftSide = false;
                } else {
                    rectangle.x += HORIZONTAL_SPEED;
                    if (rectangle.x >= 19 * SpinRoadGame.mWidth / 20 - WIDTH)
                        leftSide = true;
                }
            }
        }
    }

    public void reset() {
        rectangle.x = SpinRoadGame.mWidth/2 - WIDTH/2;
        rectangle.y = SpinRoadGame.mHeight;
        goBack = false;
        onScreen = true;
        isMouthOpen = false;
    }

    public Rectangle getRectangle(){
        return rectangle;
    }

    public boolean isOverlaps(Spinner spinner){
        circleOverlaps1.x = rectangle.x + WIDTH/2;
        circleOverlaps1.y = rectangle.y + HEIGHT - circleOverlaps1.radius;

        circleOverlaps2.x = rectangle.x + WIDTH/2;
        circleOverlaps2.y = rectangle.y + circleOverlaps2.radius;

        return Intersector.overlaps(spinner.getCircle(), circleOverlaps1) ||
                Intersector.overlaps(spinner.getCircle(), circleOverlaps2);
    }

    public boolean isReadyToAttack(){
        return rectangle.y < SpinRoadGame.mHeight - 8 * HEIGHT / 6;
    }

    public void openMouth() {
        isMouthOpen = true;
    }

    public void closeMouth() {
        isMouthOpen = false;
    }
}
