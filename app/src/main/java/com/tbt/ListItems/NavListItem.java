package com.tbt.ListItems;

/**
 * Created by bradley on 04-03-2017.
 */

public class NavListItem {
    private String name;
    private int imgRes;

    public NavListItem() {}

    public NavListItem(String name, int imgRes) {
        this.name = name;
        this.imgRes = imgRes;
    }

    public void setImgRes(int imgRes) {
        this.imgRes = imgRes;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getImgRes() {
        return imgRes;
    }

    public String getName() {
        return name;
    }
}
