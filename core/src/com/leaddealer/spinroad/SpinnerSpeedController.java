package com.leaddealer.spinroad;

import com.badlogic.gdx.input.GestureDetector;

public class SpinnerSpeedController extends GestureDetector {

    public static float speedCoefficient;
    private static int speedX;
    private static int speedY;

    private static int speed = 3;

    public SpinnerSpeedController() {
        super(new DirectionGestureListener());
    }

    private static class DirectionGestureListener extends GestureDetector.GestureAdapter {
        @Override
        public boolean pan(float x, float y, float deltaX, float deltaY) {
            deltaX *= speedCoefficient;
            deltaY *= speedCoefficient;

            if((speedX > 0 && deltaX < 0) || (speedX < 0 && deltaX > 0))
                speedX = 0;
            speedX +=  speed * deltaX;

            if((speedY < 0 && deltaY < 0) || (speedY > 0 && deltaY > 0))
                speedY = 0;
            speedY -=  speed * deltaY;

            return super.pan(x, y, deltaX, deltaY);
        }
    }

    public static int getSpeedX() {
        return speedX;
    }

    public static int getSpeedY() {
        return speedY;
    }

    public static void slowdown() {
        speedX *= 0.995;
        speedY *= 0.995;
    }

    public static void invertX() {
        speedX *= -1;
    }

    public static void invertY() {
        speedY *= -1;
    }

    public static void newGame(){
        speedX *= 0;
        speedY *= 0;
    }
}
