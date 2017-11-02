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
import com.leaddealer.spinroad.utils.Spinners;

import java.util.ArrayList;

public class SpinnersActivity extends FragmentActivity {

    public static final int MAX_SPINNERS = 8;

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
        items.add(new ShopItem(100, "spin0.png", Spinners.isSpinnerBought(this, 0), Spinners.isSpinnerChosen(this, 0)));
        items.add(new ShopItem(500, "spin1.png", Spinners.isSpinnerBought(this, 1), Spinners.isSpinnerChosen(this, 1)));
        items.add(new ShopItem(1000, "spin2.png", Spinners.isSpinnerBought(this, 2), Spinners.isSpinnerChosen(this, 2)));
        items.add(new ShopItem(1500, "spin3.png", Spinners.isSpinnerBought(this, 3), Spinners.isSpinnerChosen(this, 3)));
        items.add(new ShopItem(2000, "spin4.png", Spinners.isSpinnerBought(this, 4), Spinners.isSpinnerChosen(this, 4)));
        items.add(new ShopItem(3000, "spin5.png", Spinners.isSpinnerBought(this, 5), Spinners.isSpinnerChosen(this, 5)));
        items.add(new ShopItem(4000, "spin6.png", Spinners.isSpinnerBought(this, 6), Spinners.isSpinnerChosen(this, 6)));
        items.add(new ShopItem(5000, "spin7.png", Spinners.isSpinnerBought(this, 7), Spinners.isSpinnerChosen(this, 7)));

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

        if(Spinners.isSpinnerChosen(this, 0)){
            buy.setBackgroundDrawable(getResources().getDrawable(R.drawable.shop_current));
        } else {
            buy.setBackgroundDrawable(getResources().getDrawable(R.drawable.shop_empty));
        }

        spinnerPriceLayout.setVisibility(View.GONE);
        spinnerPrice.setText(String.valueOf(items.get(0).getPrice()));
    }

    private void buyPressed() {
        int position = pager.getCurrentItem();
        if(!Spinners.isSpinnerBought(this, position)){
            if(items.get(position).getPrice() <= Score.getAllCoins(this)) {
                Spinners.setSpinnerBought(this, position);
                items.get(position).setBought(true);
                buy.setBackgroundDrawable(getResources().getDrawable(R.drawable.shop_empty));
                spinnerPriceLayout.setVisibility(View.GONE);
                Score.addCoins(this, -items.get(position).getPrice());
                coinsTextView.setText(String.valueOf(Score.getAllCoins(this)));
            } else {
                noMoney();
            }
        } else {
            Constants.spinner = position;
            Spinners.setSpinnerChosen(this, position);
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

