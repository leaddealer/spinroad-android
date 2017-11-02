package com.leaddealer.spinroad.activities;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;
import com.leaddealer.spinroad.MainActivity;
import com.leaddealer.spinroad.R;
import com.leaddealer.spinroad.billing.IabHelper;
import com.leaddealer.spinroad.utils.LoadingDialog;
import com.leaddealer.spinroad.utils.Score;

public class ShopActivity extends FragmentActivity {

    static final int RC_REQUEST = 10001;

    private boolean needShow = false;
    private boolean rewarded = false;
    private RewardedVideoAd mAd;

    TextView coinsTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop);

        Typeface tf = Typeface.createFromAsset(getAssets(),"10367.otf");
        coinsTextView = (TextView) findViewById(R.id.final_menu_coins);
        coinsTextView.setTypeface(tf);
        coinsTextView.setText(String.valueOf(Score.getAllCoins(this)));

        ((TextView) findViewById(R.id.spinners_text_view)).setTypeface(tf);
        ((TextView) findViewById(R.id.shields_text_view)).setTypeface(tf);
        ((TextView) findViewById(R.id.vapes_text_view)).setTypeface(tf);
        ((TextView) findViewById(R.id.backs_text_view)).setTypeface(tf);
        ((TextView) findViewById(R.id.get_coins_text)).setTypeface(tf);
        ((TextView) findViewById(R.id.remove_ads_text)).setTypeface(tf);
        ((TextView) findViewById(R.id.rewarded_coins_text)).setTypeface(tf);

        findViewById(R.id.spinners).setOnClickListener(v -> {
            startActivity(new Intent(ShopActivity.this, SpinnersActivity.class));
        });

        findViewById(R.id.backgrounds).setOnClickListener(v -> {
            startActivity(new Intent(ShopActivity.this, BackgroundActivity.class));
        });

        findViewById(R.id.shields).setOnClickListener(v -> {
            startActivity(new Intent(ShopActivity.this, ShieldsActivity.class));
        });

        findViewById(R.id.vapes).setOnClickListener(v -> {
            startActivity(new Intent(ShopActivity.this, VapesActivity.class));
        });

        findViewById(R.id.close).setOnClickListener(v -> {
            Intent intent = new Intent(ShopActivity.this, MainActivity.class);
            intent.putExtra("FROM_ANOTHER", true);
            startActivity(intent);
            finish();
        });

        if(MainActivity.isPremium)
            findViewById(R.id.remove_ads_text).setVisibility(View.INVISIBLE);

        findViewById(R.id.remove_ads_text).setOnClickListener(v -> {
            if(!MainActivity.isPremium) onPurchaseButtonClicked();
        });

        findViewById(R.id.get_coins).setOnClickListener(v -> {
            showAd();
        });

        loadAd();
    }


    public void onPurchaseButtonClicked() {
        if (!MainActivity.mHelper.subscriptionsSupported()) {
            Log.e("TAG", "Subscriptions not supported on your device yet. Sorry!");
            return;
        }

        Log.d("TAG", "Launching purchase flow.");


        try {
            if (MainActivity.mHelper != null) MainActivity.mHelper.flagEndAsync();
            MainActivity.mHelper.launchPurchaseFlow(this, MainActivity.SKU_NO_ADS, IabHelper.ITEM_TYPE_INAPP,
                    null, RC_REQUEST, mPurchaseFinishedListener, "");
        } catch (IabHelper.IabAsyncInProgressException e) {
            Log.e("TAG", "Error launching purchase flow. Another async operation in progress.");
        }
    }

    private IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener = (result, purchase) -> {
        Log.d("TAG", "Purchase finished: " + result + ", purchase: " + purchase);
        if(result.isSuccess()){
            MainActivity.isPremium = true;
            findViewById(R.id.remove_ads_text).setVisibility(View.INVISIBLE);
        }
    };

    private void loadAd() {
        mAd = MobileAds.getRewardedVideoAdInstance(this);
        mAd.setRewardedVideoAdListener(new RewardedVideoAdListener() {
            @Override
            public void onRewardedVideoAdLoaded() {
                if(needShow) {
                    dismissLoadingDialog();
                    mAd.show();
                    needShow = false;
                }
            }

            @Override
            public void onRewardedVideoAdOpened() {

            }

            @Override
            public void onRewardedVideoStarted() {

            }

            @Override
            public void onRewardedVideoAdClosed() {
                Log.d("TAG", "onRewardedVideoAdClosed");
                if(rewarded)
                    showRewarded();

            }

            @Override
            public void onRewarded(RewardItem rewardItem) {
                Log.d("TAG", "onRewarded");
                rewarded = true;
//                showRewarded();
                Score.addCoins(ShopActivity.this, 100);
            }

            @Override
            public void onRewardedVideoAdLeftApplication() {

            }

            @Override
            public void onRewardedVideoAdFailedToLoad(int i) {
                if(needShow) {
                    dismissLoadingDialog();
                    needShow = false;
                    Log.d("TAG", "failed to load");
                    Toast.makeText(ShopActivity.this, getString(R.string.no_video), Toast.LENGTH_SHORT).show();
                }
            }
        });
        mAd.loadAd("ca-app-pub-6959636415626524/3078806292", new AdRequest.Builder()
                .addTestDevice("3D767D0B948DA335B93BF7850E1887AE")
                .build());
    }

    private void showRewarded() {
        rewarded = false;
        Animation fadeInAnimation = AnimationUtils.loadAnimation(this, R.anim.fadein2);
        Animation fadeOutAnimation = AnimationUtils.loadAnimation(this, R.anim.fadeout);
        fadeOutAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                findViewById(R.id.rewarded).setVisibility(View.GONE);
                coinsTextView.setText(String.valueOf(Score.getAllCoins(ShopActivity.this)));
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        fadeInAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                findViewById(R.id.rewarded).startAnimation(fadeOutAnimation);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        findViewById(R.id.rewarded).setVisibility(View.VISIBLE);
        findViewById(R.id.rewarded).startAnimation(fadeInAnimation);
    }

    private void showAd(){
        if (mAd.isLoaded()) {
            mAd.show();
        } else if(!needShow) {
            needShow = true;
            showLoadingDialog();
        } else {
            needShow = true;
            showLoadingDialog();
            loadAd();
        }
    }

    LoadingDialog mLoadingDialog;

    public void showLoadingDialog(){
        mLoadingDialog = new LoadingDialog(this, android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
        mLoadingDialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        WindowManager.LayoutParams layoutParams = mLoadingDialog.getWindow().getAttributes();
        layoutParams.dimAmount = .7f;
        mLoadingDialog.getWindow().setAttributes(layoutParams);
        mLoadingDialog.show();
    }

    public void dismissLoadingDialog(){
        if(mLoadingDialog != null)
            mLoadingDialog.dismiss();
        mLoadingDialog = null;
    }
}
