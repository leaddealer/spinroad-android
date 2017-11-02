package com.leaddealer.spinroad;

import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.leaddealer.spinroad.activities.FinalScreenActivity;
import com.leaddealer.spinroad.activities.HighscoreActivity;
import com.leaddealer.spinroad.activities.ShopActivity;
import com.leaddealer.spinroad.billing.IabBroadcastReceiver;
import com.leaddealer.spinroad.billing.IabHelper;
import com.leaddealer.spinroad.billing.IabResult;
import com.leaddealer.spinroad.billing.Inventory;
import com.leaddealer.spinroad.billing.Purchase;
import com.leaddealer.spinroad.utils.Backgrounds;
import com.leaddealer.spinroad.utils.Score;
import com.leaddealer.spinroad.utils.Shields;
import com.leaddealer.spinroad.utils.Spinners;
import com.leaddealer.spinroad.utils.TimerUtils;
import com.leaddealer.spinroad.utils.Utils;
import com.leaddealer.spinroad.utils.Vapes;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.SimpleTimeZone;

import io.branch.indexing.BranchUniversalObject;
import io.branch.referral.Branch;
import io.branch.referral.BranchError;
import io.branch.referral.util.LinkProperties;

public class MainActivity extends AndroidApplication implements IabBroadcastReceiver.IabBroadcastListener{

    private static final String TAG = "TAG";
    public static boolean isPremium;
    public static final String SKU_NO_ADS = "no_ads";
    public static IabHelper mHelper;
    IabBroadcastReceiver mBroadcastReceiver;

    public static boolean gameOver = false;
    public static boolean newGame = false;
    private boolean callOnlyOnce = true;

    FrameLayout mGameFrame;
    SpinRoadGame mSpinRoadGame;
    ImageView mGamePause;
    TextView mScoreTextView;
    ImageView mLife1;
    ImageView mLife2;
    ImageView mLife3;

    RelativeLayout mMainMenu;
    ImageView mMainMenuPlay;
    ImageView mMainMenuShop;
    ImageView mMainMenuScore;
    TextView mTitle;
    TextView mTimerTextView;

    RelativeLayout mPauseMenu;
    ImageView mPausePlay;
    ImageView mPauseReplay;
    ImageView mPauseHome;
    TextView mPauseCoins;

    RelativeLayout mFirstTutorial;
    ImageView mFirstTutorialPlay;

    int score = 0;

    public static InterstitialAd mInterstitialAd;
    CountDownTimer countDownTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        isPremium = Utils.isPremium(this);
        requestPremium();

        MobileAds.initialize(this, "ca-app-pub-6959636415626524~7648606698");

        init();
        initGame();

        Typeface tf = Typeface.createFromAsset(getAssets(),"10367.otf");

        //---------------------GAME --------------------------

        mGamePause = (ImageView) findViewById(R.id.game_pause);
        mScoreTextView = (TextView) findViewById(R.id.score);
        mLife1 = (ImageView) findViewById(R.id.life1);
        mLife2 = (ImageView) findViewById(R.id.life2);
        mLife3 = (ImageView) findViewById(R.id.life3);
        mScoreTextView.setTypeface(tf);
        mGamePause.setOnClickListener(v -> {
            mPauseMenu.setVisibility(View.VISIBLE);
            mSpinRoadGame.pauseGame();
            mPauseCoins.setText(String.valueOf(this.score));
        });

        //---------------- PAUSE MENU -------------------------

        mPauseMenu = (RelativeLayout) findViewById(R.id.pause_menu);
        mPausePlay = (ImageView) findViewById(R.id.pause_menu_play);
        mPauseReplay = (ImageView) findViewById(R.id.pause_menu_replay);
        mPauseHome = (ImageView) findViewById(R.id.pause_menu_home);
        mPauseCoins = (TextView) findViewById(R.id.pause_menu_coins);
        mPauseCoins.setTypeface(tf);
        mPausePlay.setOnClickListener(v -> {
            mSpinRoadGame.resumeGame();
            mPauseMenu.setVisibility(View.GONE);
        });

        mPauseReplay.setOnClickListener(v -> {
            startGame();
            mPauseMenu.setVisibility(View.GONE);
        });

        mPauseHome.setOnClickListener(v -> {
            mSpinRoadGame.pauseGame();
            mPauseMenu.setVisibility(View.GONE);
            mMainMenu.setVisibility(View.VISIBLE);
        });

        //---------------- MAIN MENU ---------------------------

        mMainMenu = (RelativeLayout) findViewById(R.id.main_menu);
        mMainMenuPlay = (ImageView) findViewById(R.id.main_menu_play);
        mMainMenuScore = (ImageView) findViewById(R.id.main_menu_highscore);
        mMainMenuScore.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, HighscoreActivity.class);
            startActivity(intent);
            finish();
        });

        mMainMenuPlay.setOnClickListener(v -> {
            showStartGameTutorial();
            mMainMenu.setVisibility(View.GONE);
        });
        mTitle = (TextView) findViewById(R.id.title);
        mTitle.setTypeface(tf);
        mMainMenuShop = (ImageView) findViewById(R.id.main_menu_shop);
        mMainMenuShop.setOnClickListener(v -> {
            shop();
        });

        //---------------- FIRST TUTORIAL ---------------------------

        mFirstTutorial = (RelativeLayout) findViewById(R.id.first_tutorial_menu);
        mFirstTutorialPlay = (ImageView) findViewById(R.id.first_tutorial_menu_play);
        mFirstTutorialPlay.setOnClickListener(v -> {
            mFirstTutorial.setVisibility(View.GONE);
            startGame();
        });
        ((TextView) findViewById(R.id.first_tutorial_text1)).setTypeface(tf);
        ((TextView) findViewById(R.id.first_tutorial_text2)).setTypeface(tf);
        ((TextView) findViewById(R.id.first_tutorial_text3)).setTypeface(tf);
        ((TextView) findViewById(R.id.first_tutorial_text4)).setTypeface(tf);


        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId("ca-app-pub-6959636415626524/1602073094");

        //---------------- FREE COINS ----------------------------------

        RelativeLayout freeCoinsPopup = (RelativeLayout) findViewById(R.id.free_coins_popup);
        ((TextView) findViewById(R.id.free_coins_title)).setTypeface(tf);
        ((TextView) findViewById(R.id.free_coins_ok)).setTypeface(tf);
        ((TextView) findViewById(R.id.free_coins_count)).setTypeface(tf);
        findViewById(R.id.free_coins_ok).setOnClickListener(v -> {
            freeCoinsPopup.setVisibility(View.GONE);
            Score.addCoins(this, 100);
        });
        if(getIntent().getStringExtra("data") != null && !getIntent().getStringExtra("data").isEmpty()){
            freeCoinsPopup.setVisibility(View.VISIBLE);
        }
        //---------------- TIMER --------------------------------

        mTimerTextView = (TextView) findViewById(R.id.timer);
        mTimerTextView.setTypeface(tf);

        long day = 24 * 60 * 60 * 1000;

        Long now = System.currentTimeMillis();
        if(now - TimerUtils.getLastTime(this) > day){
            TimerUtils.setLastTime(this, now);
            //TODO button available
        } else {
            DateFormat sdf = new SimpleDateFormat("HH:mm:ss");
            sdf.setTimeZone(new SimpleTimeZone(0, "Out Timezone"));
            countDownTimer = new CountDownTimer(TimerUtils.getLastTime(this) + day - now, 1000) {
                public void onTick(long millisUntilFinished) {
                    Date netDate = (new Date(millisUntilFinished));
                    mTimerTextView.setText(sdf.format(netDate));
                }

                @Override
                public void onFinish() {
                }
            };
            countDownTimer.start();
        }

        //----------------------------------------------------------------
    }

    private void initGame(){
        mGameFrame = (FrameLayout) findViewById(R.id.game_frame);
        mGameFrame.addView(getGameView());
    }

    private View getGameView(){
        AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
        config.useAccelerometer = false;
        config.useCompass = false;

        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        mSpinRoadGame = new SpinRoadGame(displaymetrics.widthPixels,
                score -> mScoreTextView.post(() -> {
                    if(mMainMenu.getVisibility() == View.GONE && mFirstTutorial.getVisibility() == View.GONE) {
                        if (score == -1) {
                            if (mLife3.getVisibility() == View.VISIBLE)
                                mLife3.setVisibility(View.GONE);
                            else if (mLife2.getVisibility() == View.VISIBLE)
                                mLife2.setVisibility(View.GONE);
                        } else if (score == -2){
                            finalScreen();
                        } else if (score == -3){
                            mLife1.setVisibility(View.VISIBLE);
                            mLife2.setVisibility(View.VISIBLE);
                            mLife3.setVisibility(View.VISIBLE);
                        } else {
                            this.score = score;
                            mScoreTextView.setText(String.valueOf(this.score));
                        }
                    }
                }));
        return initializeForView(mSpinRoadGame, config);
    }

    @Override
    protected void onResume() {
        super.onResume();
        callOnlyOnce = true;
        mInterstitialAd.loadAd(new AdRequest.Builder()
                .build());
        if(getIntent().getBooleanExtra("newGame", false)){
            mMainMenu.setVisibility(View.GONE);
            getIntent().putExtra("newGame", false);
            startGame();
        } else {
            mSpinRoadGame.pauseGame();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(!gameOver && mMainMenu.getVisibility() == View.GONE) {
            mPauseMenu.setVisibility(View.VISIBLE);
            mSpinRoadGame.pauseGame();
            mPauseCoins.setText(String.valueOf(this.score));
        }
    }

    private void startGame(){
        score = 0;
        mScoreTextView.setText(String.valueOf(this.score));
        mLife1.setVisibility(View.VISIBLE);
        mLife2.setVisibility(View.VISIBLE);
        mLife3.setVisibility(View.VISIBLE);
        mSpinRoadGame.newGame();
    }

    private void showStartGameTutorial(){
        mFirstTutorial.setVisibility(View.VISIBLE);
    }

    private void finalScreen(){
        if(callOnlyOnce) {
            callOnlyOnce = false;
            gameOver = true;
            Intent intent = new Intent(MainActivity.this, FinalScreenActivity.class);
            intent.putExtra("score", score);

            Score.addHighScore(this, score);
            Score.addCoins(this, score);

            startActivity(intent);
            finish();
        }
    }

    private void shop() {
        Intent intent = new Intent(MainActivity.this, ShopActivity.class);
        startActivity(intent);
        finish();
    }

    private void init(){
        Constants.spinner = Spinners.getCurrent(this);
        Constants.background = Backgrounds.getCurrent(this);
        Constants.shield = Shields.getCurrent(this);
        Constants.vape = Vapes.getCurrent(this);
    }

    //--------------------------------------------------
    IabHelper.QueryInventoryFinishedListener mGotInventoryListener = new IabHelper.QueryInventoryFinishedListener() {
        public void onQueryInventoryFinished(IabResult result, Inventory inventory) {
            Log.d(TAG, "Query inventory finished.");
            if (mHelper == null) return;
            if (result.isFailure()) {
                Log.e("TAG", "Failed to query inventory: " + result);
                return;
            }
            Log.d(TAG, "Query inventory was successful.");

            Purchase noAds = inventory.getPurchase(SKU_NO_ADS);
            isPremium = noAds != null;
            Utils.setPremium(MainActivity.this, isPremium);
            Log.d(TAG, "User " + (isPremium ? "HAS" : "DOES NOT HAVE") + " purchase.");
        }
    };

    @Override
    public void receivedBroadcast() {
        Log.d(TAG, "Received broadcast notification. Querying inventory.");
        try {
            mHelper.queryInventoryAsync(mGotInventoryListener);
        } catch (IabHelper.IabAsyncInProgressException e) {
            Log.e("TAG", "Error querying inventory. Another async operation in progress.");
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("TAG", "onActivityResult(" + requestCode + "," + resultCode + "," + data);
        if (mHelper == null) return;
        if (!mHelper.handleActivityResult(requestCode, resultCode, data)) {
            super.onActivityResult(requestCode, resultCode, data);
        }
        else {
            Log.d("TAG", "onActivityResult handled by IABUtil.");
        }
    }

    public void requestPremium() {
        try {
            String base64EncodedPublicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAg67jeypeP6l/QBFbch+FtK7V5t+xjrpOlXoR6n3P5CAUDIJKXM/Qj/MQfya6de/RgFxscyInDz17zV4xXmWpDJgvIDsE3Fai2hpMjh5sjsBGGcrHAseC3swnM40VxoDF2xrd2pxwbIMPJRLVEp1NzXye2mNzZTx8WPA4O8sKRsyBza6Qoomhyk0CEhlDz0ABfC+JGUzk7z+LznbJXVxuWsF1F7dwH8h2tIuTYTkeqc6alPwKEzlOesmoi+9fHDzktzJ1LFbq32MALUx8T3iNskIow20DT5jwWgfC88W7kS6QnJQjnVQsaC9pKDxTNmxV1KvN/2q8C7E1bVTAgMZsMwIDAQAB";
            mHelper = new IabHelper(this, base64EncodedPublicKey);
            mHelper.enableDebugLogging(true);
            mHelper.startSetup(result -> {
                Log.d(TAG, "Setup finished.");
                if (!result.isSuccess()) {
                    Log.e("TAG", "Problem setting up in-app billing: " + result);
                    return;
                }
                if (mHelper == null) return;

                mBroadcastReceiver = new IabBroadcastReceiver(MainActivity.this);
                IntentFilter broadcastFilter = new IntentFilter(IabBroadcastReceiver.ACTION);
                registerReceiver(mBroadcastReceiver, broadcastFilter);

                Log.d(TAG, "Setup successful. Querying inventory.");
                try {
                    mHelper.queryInventoryAsync(mGotInventoryListener);
                } catch (IabHelper.IabAsyncInProgressException e) {
                    Log.e("TAG", "Error querying inventory. Another async operation in progress.");
                }
            });
        } catch (Throwable t){t.printStackTrace();}
    }


    @Override
    public void onStart() {
        super.onStart();
        Branch branch = Branch.getInstance();
        branch.initSession((branchUniversalObject, linkProperties, error) -> {
            if (error != null) {
                Log.i("MyApp", error.getMessage());
            }
        }, this.getIntent().getData(), this);
    }

    @Override
    public void onNewIntent(Intent intent) {
        this.setIntent(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        countDownTimer.cancel();
    }
}
