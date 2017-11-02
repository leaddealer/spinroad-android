package com.leaddealer.spinroad.items;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.leaddealer.spinroad.animations.ItemAnimation;

public abstract class Item {
    public boolean isDisposed = false;

    private final int SIDE_LEFT = 3;
    private final int SIDE_RIGHT = 2;
    private final int SIDE_TOP = 0;
    private final int SIDE_BOTTOM = 1;

    private final int SPEED_TYPE_1 = 1;
    private final int SPEED_TYPE_2 = 2;

    protected int mWidth;
    protected int mHeight;
    private int side;
    private int speedType;

    private float Xo;
    private float Yo;
    protected float x;
    protected float y;

    protected float itemSpeedX;
    protected float itemSpeedY;
    protected int itemAngle;
    protected float iteration = 1;

    public Item(int screenWidth, int screenHeight) {
        mWidth = screenWidth;
        mHeight = screenHeight;

        speedType = MathUtils.random(1, 2);
    }

    abstract public boolean move();
    abstract public void draw(SpriteBatch batch);
    abstract public boolean overlapsSpinner(Spinner spinner);
    abstract public Rectangle getExplosionRectangle();

    public void updateSpeed() {
        iteration += com.leaddealer.spinroad.ItemCollection.itemSpeed;
        if(speedType == SPEED_TYPE_1){
            if(side == SIDE_TOP) {
                y = Yo - itemSpeedY * iteration;
                x = Xo + itemSpeedX * (iteration * iteration);
            } else if (side == SIDE_BOTTOM) {
                y = Yo + itemSpeedY * iteration;
                x = Xo + itemSpeedX * (iteration * iteration);
            } else if (side == SIDE_RIGHT) {
                x = Xo - itemSpeedX * iteration;
                y = Yo + itemSpeedY * (iteration * iteration);
            } else if (side == SIDE_LEFT) {
                x = Xo + itemSpeedX * iteration;
                y = Yo + itemSpeedY * (iteration * iteration);
            }
        } else if (speedType == SPEED_TYPE_2){
            if(side == SIDE_TOP) {
                y = Yo - itemSpeedY * iteration;
                x = (float) (Xo + mWidth * Math.sin(0.02 * iteration) / 3);
            } else if (side == SIDE_BOTTOM) {
                y = Yo + itemSpeedY * iteration;
                x = (float) (Xo + mWidth * Math.sin(0.02 * iteration) / 3);
            } else if (side == SIDE_RIGHT) {
                x = Xo - itemSpeedX * iteration;
                y = (float) (Yo + mHeight * Math.sin(0.02 * iteration) / 5);
            } else if (side == SIDE_LEFT) {
                x = Xo + itemSpeedX * iteration;
                y = (float) (Yo + mHeight * Math.sin(0.02 * iteration) / 5);
            }
        }
    }

    protected void setSide(Rectangle rectangle){
        side = MathUtils.random(0, 3);
        if(side == SIDE_TOP){
            rectangle.x = Xo = MathUtils.random(0, mWidth -  rectangle.width);
            rectangle.y = Yo = mHeight;
            itemSpeedY = MathUtils.random(5, 10);

            if(rectangle.x < (mWidth -  rectangle.width)/2)
                itemSpeedX = MathUtils.random(0.01f, 0.03f);
            else
                itemSpeedX = MathUtils.random(-0.03f, -0.01f);

        } else if (side == SIDE_BOTTOM) {
            rectangle.x = Xo = MathUtils.random(0, mWidth -  rectangle.width);
            rectangle.y = Yo = 0 - rectangle.height;
            itemSpeedY = MathUtils.random(5, 10);

            if(rectangle.x < (mWidth -  rectangle.width)/2)
                itemSpeedX = MathUtils.random(0.01f, 0.03f);
            else
                itemSpeedX = MathUtils.random(-0.03f, -0.01f);

        } else if (side == SIDE_RIGHT) {
            rectangle.x = Xo = mWidth;
            rectangle.y = Yo = MathUtils.random(0, mHeight - rectangle.height);;
            itemSpeedX = MathUtils.random(5, 10);

            if(rectangle.y < (mHeight -  rectangle.height)/2)
                itemSpeedY  = MathUtils.random(0.01f, 0.03f);
            else
                itemSpeedY = MathUtils.random(-0.03f, -0.01f);

        } else if (side == SIDE_LEFT) {
            rectangle.x = Xo = 0 - rectangle.width;
            rectangle.y = Yo = MathUtils.random(0, mHeight - rectangle.height);
            itemSpeedX = MathUtils.random(5, 10);

            if(rectangle.y < (mHeight -  rectangle.height)/2)
                itemSpeedY = MathUtils.random(0.01f, 0.03f);
            else
                itemSpeedY = MathUtils.random(-0.03f, -0.01f);
        }
    }

    protected void setSide(Circle circle){
        side = MathUtils.random(0, 3);

        if(side == SIDE_TOP){
            circle.x = Xo = MathUtils.random(0 - circle.radius, mWidth + circle.radius);
            circle.y = Yo = mHeight + circle.radius;
            itemSpeedY = MathUtils.random(5, 10);

            if(circle.x < (mWidth -  circle.radius)/2)
                itemSpeedX = MathUtils.random(0.01f, 0.03f);
            else
                itemSpeedX = MathUtils.random(-0.03f, -0.01f);

        } else if (side == SIDE_BOTTOM) {
            circle.x = Xo = MathUtils.random(0 - circle.radius, mWidth + circle.radius);
            circle.y = Yo = 0 - circle.radius;
            itemSpeedY = MathUtils.random(5, 10);

            if(circle.x < (mWidth -  circle.radius)/2)
                itemSpeedX = MathUtils.random(0.01f, 0.03f);
            else
                itemSpeedX = MathUtils.random(-0.03f, -0.01f);

        } else if (side == SIDE_RIGHT) {
            circle.x = Xo = mWidth + circle.radius;
            circle.y = Yo = MathUtils.random(0 - circle.radius, mHeight + circle.radius);;
            itemSpeedX = MathUtils.random(5, 10);

            if(circle.y < (mHeight -  circle.radius)/2)
                itemSpeedY = MathUtils.random(0.01f, 0.03f);
            else
                itemSpeedY = MathUtils.random(-0.03f, -0.01f);

        } else if (side == SIDE_LEFT) {
            circle.x = Xo = 0 - circle.radius;
            circle.y = Yo = MathUtils.random(0 - circle.radius, mHeight + circle.radius);
            itemSpeedX = MathUtils.random(5, 10);

            if(circle.y < (mHeight -  circle.radius)/2)
                itemSpeedY = MathUtils.random(0.01f, 0.03f);
            else
                itemSpeedY = MathUtils.random(-0.03f, -0.01f);
        }
    }

    public abstract ItemAnimation getAnimation();
    public abstract void dispose();
}
