package com.leaddealer.spinroad.activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.leaddealer.spinroad.MainActivity;
import com.leaddealer.spinroad.R;
import com.leaddealer.spinroad.billing.IabHelper;

public class FinalScreenActivity extends Activity {

    static final int RC_REQUEST = 10001;

    TextView mFinalCoins;
    ImageView mFinalReplay;
    ImageView mFinalHome;
    ImageView mShop;

    int score = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.final_activity);

        score = getIntent().getIntExtra("score", 0);

        Typeface tf = Typeface.createFromAsset(getAssets(),"10367.otf");

        mFinalCoins = (TextView) findViewById(R.id.final_menu_coins);
        mFinalCoins.setTypeface(tf);
        mFinalReplay = (ImageView) findViewById(R.id.final_menu_replay);
        mFinalHome = (ImageView) findViewById(R.id.final_menu_home);
        mShop = (ImageView) findViewById(R.id.shop);
        mFinalReplay.setOnClickListener(v -> newGame());
        mFinalHome.setOnClickListener(v -> home());
        mShop.setOnClickListener(v -> shop());

        mFinalCoins.setText(String.valueOf(this.score));

        ((TextView) findViewById(R.id.remove_ads_text)).setTypeface(tf);
        if(MainActivity.isPremium)
            findViewById(R.id.remove_ads_text).setVisibility(View.INVISIBLE);
        findViewById(R.id.remove_ads_text).setOnClickListener(v -> {
            if(!MainActivity.isPremium) onPurchaseButtonClicked();
        });

        if(!MainActivity.isPremium) {
            if (MainActivity.mInterstitialAd.isLoaded())
                MainActivity.mInterstitialAd.show();
        }
    }

    private void newGame() {
        MainActivity.newGame = true;
        Intent intent = new Intent(FinalScreenActivity.this, MainActivity.class);
        intent.putExtra("FROM_ANOTHER", true);
        intent.putExtra("newGame", true);
        startActivity(intent);
        FinalScreenActivity.this.finish();
    }

    private void home(){
        Intent intent = new Intent(FinalScreenActivity.this, MainActivity.class);
        intent.putExtra("FROM_ANOTHER", true);
        startActivity(intent);
        FinalScreenActivity.this.finish();
    }

    private void shop(){
        finish();
        Intent intent = new Intent(FinalScreenActivity.this, ShopActivity.class);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {}

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
}
