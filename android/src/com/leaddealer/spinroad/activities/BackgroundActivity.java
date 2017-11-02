package com.leaddealer.spinroad.activities;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.leaddealer.spinroad.Constants;
import com.leaddealer.spinroad.R;
import com.leaddealer.spinroad.fragments.PageFragment;
import com.leaddealer.spinroad.models.ShopItem;
import com.leaddealer.spinroad.utils.Backgrounds;
import com.leaddealer.spinroad.utils.NoMoneyDialog;
import com.leaddealer.spinroad.utils.Score;

import java.util.ArrayList;

public class BackgroundActivity extends FragmentActivity {

    public static final int MAX_BACKGROUNDS = 6;

    ViewPager pager;
    PagerAdapter pagerAdapter;

    TextView coinsTextView;

    ImageView back;
    ImageView forward;
    ImageView buy;

    LinearLayout spinnerPriceLayout;
    TextView spinnerPrice;

    ArrayList<ShopItem> items;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spinners);

        items = new ArrayList<>();
        items.add(new ShopItem(1000, "back0.png", Backgrounds.isBackgroundBought(this, 0), Backgrounds.isBackgroundChosen(this, 0)));
        items.add(new ShopItem(1000, "back1.png", Backgrounds.isBackgroundBought(this, 1), Backgrounds.isBackgroundChosen(this, 1)));
        items.add(new ShopItem(1000, "back2.png", Backgrounds.isBackgroundBought(this, 2), Backgrounds.isBackgroundChosen(this, 2)));
        items.add(new ShopItem(1000, "back3.png", Backgrounds.isBackgroundBought(this, 3), Backgrounds.isBackgroundChosen(this, 3)));
        items.add(new ShopItem(1000, "back4.png", Backgrounds.isBackgroundBought(this, 4), Backgrounds.isBackgroundChosen(this, 4)));
        items.add(new ShopItem(1000, "back5.png", Backgrounds.isBackgroundBought(this, 5), Backgrounds.isBackgroundChosen(this, 5)));

        pager = (ViewPager) findViewById(R.id.pager);
        pagerAdapter = new ShopFragmentPagerAdapter(getSupportFragmentManager());
        pager.setAdapter(pagerAdapter);

        back = (ImageView) findViewById(R.id.back);
        forward = (ImageView) findViewById(R.id.forward);
        buy = (ImageView) findViewById(R.id.buy);
        spinnerPriceLayout = (LinearLayout) findViewById(R.id.item_price_layout);
        spinnerPrice = (TextView) findViewById(R.id.item_price);
        findViewById(R.id.description).setVisibility(View.GONE);

        back.setOnClickListener(v -> pager.setCurrentItem(previousItem(), true));
        forward.setOnClickListener(v -> pager.setCurrentItem(nextItem(), true));

        Typeface tf = Typeface.createFromAsset(getAssets(),"10367.otf");
        coinsTextView = (TextView) findViewById(R.id.final_menu_coins);
        coinsTextView.setTypeface(tf);
        coinsTextView.setText(String.valueOf(Score.getAllCoins(this)));

        spinnerPrice.setTypeface(tf);

        findViewById(R.id.close).setOnClickListener(v -> {
            finish();
        });

        buy.setOnClickListener(v -> buyPressed());

        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

            @Override
            public void onPageSelected(int position) {
                if(!items.get(position).isBought())
                    buy.setBackgroundDrawable(getResources().getDrawable(R.drawable.buy));
                else if(!items.get(position).isChosen())
                    buy.setBackgroundDrawable(getResources().getDrawable(R.drawable.shop_empty));
                else
                    buy.setBackgroundDrawable(getResources().getDrawable(R.drawable.shop_current));

                if(!items.get(position).isBought()) {
                    spinnerPriceLayout.setVisibility(View.VISIBLE);
                    spinnerPrice.setText(String.valueOf(items.get(position).getPrice()));
                } else
                    spinnerPriceLayout.setVisibility(View.GONE);
            }

            @Override
            public void onPageScrollStateChanged(int state) {}
        });

        if(Backgrounds.isBackgroundChosen(this, 0)){
            buy.setBackgroundDrawable(getResources().getDrawable(R.drawable.shop_current));
        } else {
            buy.setBackgroundDrawable(getResources().getDrawable(R.drawable.shop_empty));
        }

        spinnerPriceLayout.setVisibility(View.GONE);
        spinnerPrice.setText(String.valueOf(items.get(0).getPrice()));
    }

    private void buyPressed() {
        int position = pager.getCurrentItem();
        if(!Backgrounds.isBackgroundBought(this, position)){
            if(items.get(position).getPrice() <= Score.getAllCoins(this)) {
                Backgrounds.setBackgroundBought(this, position);
                items.get(position).setBought(true);
                buy.setBackgroundDrawable(getResources().getDrawable(R.drawable.shop_empty));
                spinnerPriceLayout.setVisibility(View.GONE);
                Score.addCoins(this, -items.get(position).getPrice());
                coinsTextView.setText(String.valueOf(Score.getAllCoins(this)));
            } else {
                noMoney();
            }
        } else {
            Constants.background = position;
            Backgrounds.setBackgroundChosen(this, position);
            for(ShopItem item: items)
                item.setChosen(false);
            items.get(position).setChosen(true);
            buy.setBackgroundDrawable(getResources().getDrawable(R.drawable.shop_current));
        }
    }

    private int nextItem(){
        if((pager.getCurrentItem() + 1) >= pagerAdapter.getCount())
            return 0;
        else
            return pager.getCurrentItem() + 1;
    }

    private int previousItem(){
        if((pager.getCurrentItem() - 1) < 0)
            return pagerAdapter.getCount() - 1;
        else
            return pager.getCurrentItem() - 1;
    }

    private class ShopFragmentPagerAdapter extends FragmentPagerAdapter {

        public ShopFragmentPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            return items.size();
        }

        @Override
        public Fragment getItem(int position) {
            PageFragment fragment = new PageFragment();
            fragment.setItem(items.get(position));
            return fragment;
        }
    }

    private void noMoney(){
        NoMoneyDialog dialog = new NoMoneyDialog(this, R.style.PauseDialog);
        dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        WindowManager.LayoutParams layoutParams = dialog.getWindow().getAttributes();
        layoutParams.dimAmount = .7f;
        dialog.getWindow().setAttributes(layoutParams);
        dialog.show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        coinsTextView.setText(String.valueOf(Score.getAllCoins(this)));
    }
}