package com.example.roplabda_bajnoksag;

import com.google.firebase.firestore.IgnoreExtraProperties;

@IgnoreExtraProperties
public class MatchItem {
    private String id;
    private String name;
    private String info;
    private String price;
    private int imageResource;
    private int cartedCount;

    public MatchItem(String name, String info, String price, int imageResource, int cartedCount) {
        this.name = name;
        this.info = info;
        this.price = price;
        this.imageResource = imageResource;
        this.cartedCount = cartedCount;
    }

    public MatchItem() {
    }

    public String getName() {return name;}

    public String getInfo() {return info;}

    public String getPrice() {return price;}

    public int getImageResource() {return imageResource;}

    public int getCartedCount() {return cartedCount;}

    public String _getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public void setPrice(String price) {
        this.price = price;
    }
}
