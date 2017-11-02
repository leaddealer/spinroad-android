package com.leaddealer.spinroad.utils;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Window;

import com.github.ybq.android.spinkit.SpinKitView;
import com.github.ybq.android.spinkit.style.FadingCircle;
import com.leaddealer.spinroad.R;

public class LoadingDialog extends Dialog {

    public LoadingDialog(Context context, int themeResId) {
        super(context, themeResId);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_loading);

        SpinKitView mLoadingBar = (SpinKitView) findViewById(R.id.spin_kit);
        mLoadingBar.setIndeterminateDrawable(new FadingCircle());
    }
}
