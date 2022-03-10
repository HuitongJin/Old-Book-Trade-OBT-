package com.example.obt.Tab;

import com.example.obt.Fragment.CartFm;
import com.example.obt.Fragment.HomeFm;
import com.example.obt.Fragment.MineFm;
import com.example.obt.Fragment.MsgFm;
import com.example.obt.Fragment.ShopFm;
import com.example.obt.R;

public class TabDb {
    // 获得底部所有项
    public static String[] getTabsTxt() {
        String[] tabs = {"首页", "消息", "我的店铺", "购物车", "我的"};
        return tabs;
    }

    // 获得所有碎片
    public static Class[] getFragment() {
        Class[] cls = {HomeFm.class, MsgFm.class, ShopFm.class, CartFm.class, MineFm.class};
        return cls;
    }

    // 获得点击前的图片
    public static int[] getTabsImg() {
        int[] img = {R.drawable.home1, R.drawable.msg1,
                R.drawable.mystore1, R.drawable.buycar1, R.drawable.mine1};
        return img;
    }

    // 获得点击后的图片
    public static int[] getTabsImgLight() {
        int[] img = {R.drawable.home2, R.drawable.msg2, R.drawable.mystore2,
        R.drawable.buycar2, R.drawable.mine2};
        return img;
    }

}
