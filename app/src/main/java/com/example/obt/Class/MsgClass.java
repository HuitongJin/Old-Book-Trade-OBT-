package com.example.obt.Class;

import android.graphics.Bitmap;

public class MsgClass {
    private Bitmap userImage;
    private String userName;
    private String msg;

    public MsgClass(Bitmap userImage, String userName, String msg) {
        this.userImage = userImage;
        this.userName = userName;
        this.msg = msg;
    }

    public Bitmap getUserImage() {
        return userImage;
    }

    public String getMsg() {
        return msg;
    }

    public String getUserName() {
        return userName;
    }
}
