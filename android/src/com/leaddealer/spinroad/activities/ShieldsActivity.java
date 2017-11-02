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
import com.leaddealer.spinroad.utils.NoMoneyDialog;
import com.leaddealer.spinroad.utils.Score;
import com.leaddealer.spinroad.utils.Shields;

import java.util.ArrayList;

public class ShieldsActivity extends FragmentActivity {

    public static final int MAX_SHIELDS = 6;

    ViewPager pager;
    PagerAdapter pagerAdapter;

    TextView coinsTextView;

    ImageView back;
    ImageView forward;
    ImageView buy;

    LinearLayout spinnerPriceLayout;
    TextView spinnerPrice;
    TextView description;

    ArrayList<ShopItem> items;
    ArrayList<String> descriptions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spinners);

        items = new ArrayList<>();
        items.add(new ShopItem(100, "shield0.png", Shields.isShieldBought(this, 0), Shields.isShieldChosen(this, 0)));
        items.add(new ShopItem(2000, "shield1.png", Shields.isShieldBought(this, 1), Shields.isShieldChosen(this, 1)));
        items.add(new ShopItem(4000, "shield2.png", Shields.isShieldBought(this, 2), Shields.isShieldChosen(this, 2)));
        items.add(new ShopItem(6000, "shield3.png", Shields.isShieldBought(this, 3), Shields.isShieldChosen(this, 3)));
        items.add(new ShopItem(8000, "shield4.png", Shields.isShieldBought(this, 4), Shields.isShieldChosen(this, 4)));
        items.add(new ShopItem(10000, "shield5.png", Shields.isShieldBought(this, 5), Shields.isShieldChosen(this, 5)));

        descriptions = new ArrayList<>();
        descriptions.add("shield:1");
        descriptions.add("shield:2");
        descriptions.add("shield:3");
        descriptions.add("shield:4");
        descriptions.add("shield:5");
        descriptions.add("shield:6");

        pager = (ViewPager) findViewById(R.id.pager);
        pagerAdapter = new ShopFragmentPagerAdapter(getSupportFragmentManager());
        pager.setAdapter(pagerAdapter);

        back = (ImageView) findViewById(R.id.back);
        forward = (ImageView) findViewById(R.id.forward);
        buy = (ImageView) findViewById(R.id.buy);
        spinnerPriceLayout = (LinearLayout) findViewById(R.id.item_price_layout);
        spinnerPrice = (TextView) findViewById(R.id.item_price);
        description = (TextView) findViewById(R.id.description);

        back.setOnClickListener(v -> pager.setCurrentItem(previousItem(), true));
        forward.setOnClickListener(v -> pager.setCurrentItem(nextItem(), true));

        Typeface tf = Typeface.createFromAsset(getAssets(),"10367.otf");
        coinsTextView = (TextView) findViewById(R.id.final_menu_coins);
        coinsTextView.setTypeface(tf);
        coinsTextView.setText(String.valueOf(Score.getAllCoins(this)));

        spinnerPrice.setTypeface(tf);
        description.setTypeface(tf);

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

                description.setText(descriptions.get(position));
            }

            @Override
            public void onPageScrollStateChanged(int state) {}
        });

        if(Shields.isShieldChosen(this, 0)){
            buy.setBackgroundDrawable(getResources().getDrawable(R.drawable.shop_current));
        } else {
            buy.setBackgroundDrawable(getResources().getDrawable(R.drawable.shop_empty));
        }

        spinnerPriceLayout.setVisibility(View.GONE);
        spinnerPrice.setText(String.valueOf(items.get(0).getPrice()));
        description.setText(descriptions.get(0));
    }

    private void buyPressed() {
        int position = pager.getCurrentItem();
        if(!Shields.isShieldBought(this, position)){
            if(items.get(position).getPrice() <= Score.getAllCoins(this)) {
                Shields.setShieldBought(this, position);
                items.get(position).setBought(true);
                buy.setBackgroundDrawable(getResources().getDrawable(R.drawable.shop_empty));
                spinnerPriceLayout.setVisibility(View.GONE);
                Score.addCoins(this, -items.get(position).getPrice());
                coinsTextView.setText(String.valueOf(Score.getAllCoins(this)));
            } else {
                noMoney();
            }
        } else {
            Constants.shield = position;
            Shields.setShieldChosen(this, position);
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


