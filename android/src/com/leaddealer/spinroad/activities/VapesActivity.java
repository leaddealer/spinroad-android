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
import com.leaddealer.spinroad.utils.Vapes;

import java.util.ArrayList;

public class VapesActivity extends FragmentActivity {

    public static final int MAX_VAPES = 5;

    ViewPager pager;
    PagerAdapter pagerAdapter;

    ImageView back;
    ImageView forward;
    ImageView buy;

    TextView coinsTextView;

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
        items.add(new ShopItem(100, "vape0.png", Vapes.isVapeBought(this, 0), Vapes.isVapeChosen(this, 0)));
        items.add(new ShopItem(500, "vape1.png", Vapes.isVapeBought(this, 1), Vapes.isVapeChosen(this, 1)));
        items.add(new ShopItem(1000, "vape2.png", Vapes.isVapeBought(this, 2), Vapes.isVapeChosen(this, 2)));
        items.add(new ShopItem(1500, "vape3.png", Vapes.isVapeBought(this, 3), Vapes.isVapeChosen(this, 3)));
        items.add(new ShopItem(2000, "vape4.png", Vapes.isVapeBought(this, 4), Vapes.isVapeChosen(this, 4)));

        descriptions = new ArrayList<>();
        descriptions.add("slow:1");
        descriptions.add("slow:2");
        descriptions.add("slow:3");
        descriptions.add("slow:4");
        descriptions.add("slow:5");

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

        if(Vapes.isVapeChosen(this, 0)){
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
        if(!Vapes.isVapeBought(this, position)){
            if(items.get(position).getPrice() <= Score.getAllCoins(this)) {
                Vapes.setVapeBought(this, position);
                items.get(position).setBought(true);
                buy.setBackgroundDrawable(getResources().getDrawable(R.drawable.shop_empty));
                spinnerPriceLayout.setVisibility(View.GONE);
                Score.addCoins(this, -items.get(position).getPrice());
                coinsTextView.setText(String.valueOf(Score.getAllCoins(this)));
            } else {
                noMoney();
            }
        } else {
            Constants.vape = position;
            Vapes.setVapeChosen(this, position);
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
