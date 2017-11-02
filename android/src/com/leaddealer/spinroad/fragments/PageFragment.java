package com.leaddealer.spinroad.fragments;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.leaddealer.spinroad.R;
import com.leaddealer.spinroad.models.ShopItem;
import com.leaddealer.spinroad.utils.Utils;

public class PageFragment extends Fragment {

    ShopItem shopItem;

    public void setItem(ShopItem shopItem) {
        this.shopItem = shopItem;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.page_layout, null);

        ImageView image = (ImageView) view.findViewById(R.id.image);
        image.setImageBitmap(Utils.loadBitmap(getContext(), shopItem.getImageName()));

        return view;
    }

}
