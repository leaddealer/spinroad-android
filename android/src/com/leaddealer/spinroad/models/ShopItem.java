package com.leaddealer.spinroad.models;

public class ShopItem {
    int price;
    String imageName;
    boolean isBought;
    boolean isChosen;

    public ShopItem(int price, String imageName, boolean isBought, boolean isChosen) {
        this.price = price;
        this.imageName = imageName;
        this.isBought = isBought;
        this.isChosen = isChosen;
    }

    public int getPrice() {
        return price;
    }

    public String getImageName() {
        return imageName;
    }

    public boolean isBought() {
        return isBought;
    }

    public boolean isChosen() {
        return isChosen;
    }

    public void setBought(boolean bought) {
        isBought = bought;
    }

    public void setChosen(boolean chosen) {
        isChosen = chosen;
    }
}
