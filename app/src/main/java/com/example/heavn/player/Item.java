package com.example.heavn.player;

/**
 * Created by Administrator on 2017/4/22 0022.
 */

public class Item {
    private String name;
    private int imageId;
    private String number;

    public Item(String name, int imageId, String number) {
        this.name = name;
        this.imageId = imageId;
        this.number = number;
    }


    public String getName() {
        return name;
    }

    public int getImageId() {
        return imageId;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }
}

