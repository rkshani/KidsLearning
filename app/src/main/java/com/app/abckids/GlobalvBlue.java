package com.app.abckids;

import android.app.Application;

public class GlobalvBlue extends Application {
    private int Num = 0;
    public static String inter_ad_unit_id;
    public static String banner_ad_unit_id;

    public int getNum() {
        return this.Num;
    }

    public void setNum(int i) {
        this.Num = i;
    }
}