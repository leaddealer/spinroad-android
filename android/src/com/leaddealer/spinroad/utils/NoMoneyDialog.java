package com.leaddealer.spinroad.utils;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;
import com.leaddealer.spinroad.R;

public class NoMoneyDialog extends Dialog {

    Context context;

    private boolean needShow = false;
    private RewardedVideoAd mAd;

    public NoMoneyDialog(Context context, int themeResId) {
        super(context, themeResId);
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_get_item);

        ImageView close = (ImageView) findViewById(R.id.close);
        close.setOnClickListener(v -> {
            this.dismiss();
        });

        Typeface tf = Typeface.createFromAsset(context.getAssets(),"10367.otf");
        ((TextView) findViewById(R.id.title)).setTypeface(tf);
        ((TextView) findViewById(R.id.get_coins_text)).setTypeface(tf);

        findViewById(R.id.get_coins).setOnClickListener(v -> {
            showAd();
        });

        loadAd();
    }

    private void loadAd() {
        mAd = MobileAds.getRewardedVideoAdInstance(context);
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

            }

            @Override
            public void onRewarded(RewardItem rewardItem) {
                Score.addCoins(context, 100);
                NoMoneyDialog.this.dismiss();
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
                    Toast.makeText(context, context.getString(R.string.no_video), Toast.LENGTH_SHORT).show();
                }
            }
        });
        mAd.loadAd("ca-app-pub-6959636415626524/3078806292", new AdRequest.Builder()
                .addTestDevice("3D767D0B948DA335B93BF7850E1887AE")
                .build());
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
        mLoadingDialog = new LoadingDialog(context, android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
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