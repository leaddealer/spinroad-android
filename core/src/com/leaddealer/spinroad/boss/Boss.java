package com.leaddealer.spinroad.boss;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;
import com.leaddealer.spinroad.ItemCollection;
import com.leaddealer.spinroad.SpinRoadGame;
import com.leaddealer.spinroad.animations.ItemAnimation;
import com.leaddealer.spinroad.boss.items.CoinItem;
import com.leaddealer.spinroad.boss.items.LifeItem;
import com.leaddealer.spinroad.boss.items.TextItem;
import com.leaddealer.spinroad.interfaces.ScoreObserver;
import com.leaddealer.spinroad.items.Spinner;

import java.util.Iterator;

public class Boss {
    private final int HP = 20;

    private final long spawnTime = 1000000000L;

    private Head head;
    private Array<BossItem> items;
    private Array<ItemAnimation> animations;
    private long lastItemTime = 0;
    private long hp = HP;

    public Boss() {
        head = new Head();

        items = new Array<BossItem>();
        animations = new Array<ItemAnimation>();
    }

    public void draw(SpriteBatch batch, Spinner spinner, ScoreObserver scoreObserver) {

        head.draw(batch, (float)hp/(float) HP);
        if(!head.onScreen){
            bossGone();
        }

        if(head.isOverlaps(spinner)){
            spinner.isAlive = false;
        }

        if(head.isReadyToAttack()){
            if (TimeUtils.nanoTime() - lastItemTime > 3*spawnTime/4)
                head.openMouth();
            else if (TimeUtils.nanoTime() - lastItemTime > spawnTime/4)
                head.closeMouth();


            if (TimeUtils.nanoTime() - lastItemTime > spawnTime){
                spawn(spinner, scoreObserver);
                lastItemTime = TimeUtils.nanoTime();
                hp--;
                if(hp <= 0){
                    head.goBack = true;
                }
            }
        }

        Iterator<BossItem> iterator = items.iterator();
        while (iterator.hasNext()) {
            BossItem item = iterator.next();
            if (item.move()) {
                iterator.remove();
                item.dispose();
            }

            if (item.overlapsSpinner(spinner) && spinner.isAlive) {
                if (item instanceof CoinItem) {
                    animations.add(item.getAnimation());
                    iterator.remove();
                    item.dispose();

                    ItemCollection.score += 5;
                    scoreObserver.onScoreChanged(ItemCollection.score);

                } else if (item instanceof LifeItem) {
                    animations.add(item.getAnimation());
                    iterator.remove();
                    item.dispose();

                    ItemCollection.lifes = 3;
                    scoreObserver.onScoreChanged(-3);

                } else {
                    animations.add(item.getAnimation());
                    iterator.remove();
                    item.dispose();
                    if (ItemCollection.isShieldActive) {
                        ItemCollection.shields--;
                        if (ItemCollection.shields == 0)
                            ItemCollection.isShieldActive = false;
                    } else {
                        scoreObserver.onScoreChanged(-1);
                        ItemCollection.lifes--;
                    }
                }
            }
            if(!item.isDisposed)
                item.draw(batch);
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

    private void spawn(Spinner spinner, ScoreObserver scoreObserver) {
        ItemCollection.score ++;
        scoreObserver.onScoreChanged(ItemCollection.score);

        float Xo = head.getRectangle().x + (head.getRectangle().width/2);
        float Yo = head.getRectangle().y + (67 * head.getRectangle().height/ 300);
        float finalX, finalY;

        if(hp%2 == 0){
            finalX = spinner.getCircle().x;
            finalY = spinner.getCircle().y;
        } else{
            finalX = MathUtils.random(0, SpinRoadGame.mWidth);
            finalY = 0;
        }

        int probability = MathUtils.random(0, 99);
        if(probability >= 0 && probability < 5)
            items.add(new LifeItem(Xo, Yo, finalX, finalY));
        else if(probability >= 5 && probability < 15)
            items.add(new CoinItem(Xo, Yo, finalX, finalY));
        else
            items.add(new TextItem(Xo, Yo, finalX, finalY));
    }

    private void bossGone(){
        reset();
        head.reset();
    }

    public void reset() {
        hp = HP;
        head.reset();
        items = new Array<BossItem>();
        animations = new Array<ItemAnimation>();
    }
}
