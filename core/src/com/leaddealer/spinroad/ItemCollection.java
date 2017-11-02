package com.leaddealer.spinroad;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;
import com.leaddealer.spinroad.animations.CrashAnimation;
import com.leaddealer.spinroad.animations.ItemAnimation;
import com.leaddealer.spinroad.boss.Boss;
import com.leaddealer.spinroad.interfaces.ScoreObserver;
import com.leaddealer.spinroad.items.Item;
import com.leaddealer.spinroad.items.ItemGlasses;
import com.leaddealer.spinroad.items.ItemGyro;
import com.leaddealer.spinroad.items.ItemLong;
import com.leaddealer.spinroad.items.ItemMustashe;
import com.leaddealer.spinroad.items.ItemSelfiestick;
import com.leaddealer.spinroad.items.ItemSmoothie;
import com.leaddealer.spinroad.items.ItemVape;
import com.leaddealer.spinroad.items.Spinner;

import java.util.Iterator;

public class ItemCollection {
    private static final int LEVEL_UP = 5;
    private static final float LEVEL_UP_COEF = 0.04f;
    private static final int FROZEN_ITERATIONS = 5;

    public static int lifes = 3;
    public static float itemSpeed = 0.6f;
    public static int frozenCount = 0;
    public static boolean isShieldActive = false;
    public static int score = 0;
    public static int lastBossScore = 0;
    public boolean GameOver = false;
    public static int shields = 0;
    private ScoreObserver mScoreObserver;

    private int mWidth;
    private int mHeight;

    private Boss boss;
    private Array<Item> items;
    private Array<ItemAnimation> animations;
    private long lastItemTime;
    private long lastScoreTime = 0;
    private long lastFrozenTime = 0;

    public ItemCollection(int width, int height, ScoreObserver scoreObserver) {
        mScoreObserver = scoreObserver;
        boss = new Boss();

        mWidth = width;
        mHeight = height;

        items = new Array<Item>();
        animations = new Array<com.leaddealer.spinroad.animations.ItemAnimation>();
    }

    private void checkSpeed(){
        if (TimeUtils.nanoTime() - lastScoreTime > 2500000000L) {
            lastScoreTime = TimeUtils.nanoTime();
            score++;
            mScoreObserver.onScoreChanged(score);

            if (score % LEVEL_UP == 0) {
                if (frozenCount > 0)
                    itemSpeed += LEVEL_UP_COEF / (Constants.vape + 2);
                else
                    itemSpeed += LEVEL_UP_COEF;
            }
        }

        if (TimeUtils.nanoTime() - lastItemTime > 1200000000L / itemSpeed) {
            spawnItem();
        }

        if(TimeUtils.nanoTime() - lastFrozenTime > 1000000000L && frozenCount > 0){
            lastFrozenTime = TimeUtils.nanoTime();
            frozenCount--;
            if(frozenCount == 0){
                itemSpeed *= Constants.vape + 2;
            }
        }
    }

    public void drawItems(SpriteBatch batch, Spinner spinner){
        if(score - lastBossScore >= 30){
            boss.draw(batch, spinner, mScoreObserver);

            Iterator<Item> iterator = items.iterator();
            while (iterator.hasNext()) {
                Item item = iterator.next();
                animations.add(item.getAnimation());
                iterator.remove();
                item.dispose();
            }

        } else {
            checkSpeed();
        }

        Iterator<Item> iterator = items.iterator();
        while (iterator.hasNext()) {
            Item item = iterator.next();

            item.updateSpeed();

            if (item.move()) {
                iterator.remove();
                item.dispose();
            }

            if (item.overlapsSpinner(spinner) && spinner.isAlive) {
                if (item instanceof ItemSmoothie) {
                    isShieldActive = true;
                    shields = Constants.shield + 1;
                    animations.add(item.getAnimation());
                    iterator.remove();
                    item.dispose();

                    score++;
                    mScoreObserver.onScoreChanged(score);
                } else if (item instanceof ItemVape) {
                    if (frozenCount <= 0) {
                        itemSpeed /= Constants.vape + 2;
                    }
                    frozenCount += FROZEN_ITERATIONS;
                    animations.add(item.getAnimation());
                    iterator.remove();
                    item.dispose();

                    score++;
                    mScoreObserver.onScoreChanged(score);
                } else {
                    animations.add(item.getAnimation());
                    iterator.remove();
                    item.dispose();
                    if (isShieldActive) {
                        shields--;
                        if (shields == 0)
                            isShieldActive = false;
                    } else {
                        mScoreObserver.onScoreChanged(-1);
                        lifes--;
                    }
                }
            }
            if(!item.isDisposed)
                item.draw(batch);
        }

        if(!spinner.isAlive && !GameOver){
            GameOver = true;
            Rectangle rectangle = new Rectangle();
            rectangle.width = spinner.SPINNER_SIZE;
            rectangle.height = spinner.SPINNER_SIZE;
            rectangle.x = spinner.getCircle().x - spinner.SPINNER_SIZE/2;
            rectangle.y = spinner.getCircle().y - spinner.SPINNER_SIZE/2;

            animations.add(new CrashAnimation(rectangle));
        }

        if(lifes == 0){
            spinner.isAlive = false;
        }

        if(!spinner.isAlive && GameOver && animations.size == 0){
            mScoreObserver.onScoreChanged(-2);
        }

        Iterator<ItemAnimation> iteratorAnimation = animations.iterator();
        while (iteratorAnimation.hasNext()) {
            ItemAnimation itemAnimation = iteratorAnimation.next();
            if (!itemAnimation.draw(batch)) {
                iteratorAnimation.remove();
                itemAnimation.dispose();
            }
        }
    }

    private void spawnItem() {
        if(mHeight != 0) {
            int probability = MathUtils.random(0, 99);
            if(probability >= 0 && probability < 5)
                items.add(new ItemVape(mWidth, mHeight));
            else if(probability >= 5 && probability < 10)
                items.add(new ItemSmoothie(mWidth, mHeight));
            else if(probability >= 10 && probability < 28)
                items.add(new ItemGyro(mWidth, mHeight));
            else if(probability >= 28 && probability < 46)
                items.add(new ItemGlasses(mWidth, mHeight));
            else if(probability >= 46 && probability < 64)
                items.add(new ItemLong(mWidth, mHeight));
            else if(probability >= 64 && probability < 82)
                items.add(new ItemMustashe(mWidth, mHeight));
            else if(probability >= 82 && probability < 100)
                items.add(new ItemSelfiestick(mWidth, mHeight));

            lastItemTime = TimeUtils.nanoTime();
        }
    }

    public void dispose() {

    }

    public void reset() {
        items = new Array<Item>();
        animations = new Array<ItemAnimation>();

        itemSpeed = 0.6f;
        frozenCount = 0;
        isShieldActive = false;
        GameOver = false;
        shields = 0;
        lifes = 3;
        score = 0;
        lastBossScore = 0;

        boss.reset();
    }
}
