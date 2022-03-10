package com.example.obt.Class;

import android.graphics.Bitmap;


public class BookShop {
    private int BID; // 图书序列号
    private String bookName;    // 书名
    private Bitmap bookImage;   // 配图
    private String bookAuthor;  // 作者
    private String punishName;  // 出版社
    private double price;   // 售价

    public BookShop(int BID, String bookName, Bitmap bookImage, String bookAuthor, String punishName, double price) {
        this.BID = BID;
        this.bookName = bookName;
        this.bookImage = bookImage;
        this.bookAuthor = bookAuthor;
        this.punishName = punishName;
        this.price = price;
    }

    public String getBookName() {
        return bookName;
    }

    public Bitmap getBookImage() {
        return bookImage;
    }

    public String getBookAuthor() {
        return bookAuthor;
    }

    public double getPrice() {
        return price;
    }

    public String getPunishName() {
        return punishName;
    }

    public int getBID() {
        return BID;
    }

}
