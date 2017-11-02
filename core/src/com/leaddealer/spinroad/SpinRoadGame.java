package com.leaddealer.spinroad;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.leaddealer.spinroad.interfaces.ScoreObserver;
import com.leaddealer.spinroad.items.Spinner;

import java.util.ArrayList;

public class SpinRoadGame extends Game {
    private OrthographicCamera camera;
    private SpriteBatch batch;
    private Texture frozenTexture;
    private Spinner spinner;
    private ItemCollection itemCollection;
    private ScoreObserver mScoreObserver;

    Music backgroundSound;


    private boolean isPaused = true;

    private int[] r = {223, 249, 217, 247, 237, 241};
    private int[] g = {244, 228, 237, 241, 230, 215};
    private int[] b = {239, 212, 233, 218, 221, 242};
    private ArrayList<TextureRegion> backs;

    public static int mHeight;
    public static int mWidth = 2048;

    public SpinRoadGame(int width, ScoreObserver scoreObserver) {
        SpinnerSpeedController.speedCoefficient = mWidth/width;
        mScoreObserver = scoreObserver;
    }

    @Override
	public void create () {
        Gdx.input.setInputProcessor(new SpinnerSpeedController());

        camera = new OrthographicCamera();
		batch = new SpriteBatch();
        frozenTexture = new Texture("frozen.png");
        backgroundSound = Gdx.audio.newMusic(Gdx.files.internal("background.mp3"));

        backs = new ArrayList<TextureRegion>();
        backs.add(new TextureRegion(new Texture("back0.png")));
        backs.add(new TextureRegion(new Texture("back1.png")));
        backs.add(new TextureRegion(new Texture("back2.png")));
        backs.add(new TextureRegion(new Texture("back3.png")));
        backs.add(new TextureRegion(new Texture("back4.png")));
        backs.add(new TextureRegion(new Texture("back5.png")));


        backgroundSound.setLooping(true);
        backgroundSound.play();
	}

	@Override
	public void render () {
        if(!isPaused) {
            Gdx.gl.glClearColor(r[Constants.background]/255.0f, g[Constants.background]/255.0f,
                    b[Constants.background]/255.0f, 1);
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

            camera.update();

            batch.setProjectionMatrix(camera.combined);
            batch.begin();
            batch.draw(backs.get(Constants.background), mWidth/6, mHeight/2 - 2*mWidth/6, 2*mWidth/3, 2*mWidth/3);

            spinner.move();
            itemCollection.drawItems(batch, spinner);
            spinner.draw(batch);

            if(ItemCollection.frozenCount > 0)
                batch.draw(frozenTexture, 0, 0, mWidth, mHeight);

            batch.end();
        }
	}

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
        mHeight = mWidth * height / width;

        camera.setToOrtho(false, mWidth, mHeight);
        if(spinner == null)
            spinner = new Spinner(mWidth, mHeight);
        if(itemCollection == null)
            itemCollection = new ItemCollection(mWidth, mHeight, mScoreObserver);
    }

    @Override
    public void dispose() {
        batch.dispose();
        spinner.dispose();
        itemCollection.dispose();
        for(TextureRegion region: backs)
            region.getTexture().dispose();
        frozenTexture.dispose();
        backgroundSound.dispose();
    }

    public void pauseGame() {
        isPaused = true;
    }

    public void resumeGame() {
        isPaused = false;
    }

    public void newGame() {
        isPaused = false;
        SpinnerSpeedController.newGame();
        itemCollection.score = 0;
        if(spinner == null)
            spinner = new Spinner(mWidth, mHeight);
        if(itemCollection == null)
            itemCollection = new ItemCollection(mWidth, mHeight, mScoreObserver);
        spinner.reset();
        itemCollection.reset();
    }
}
