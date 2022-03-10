package com.example.obt.Mysql;

public class User {
    private String phone; // 手机号码， 非空
    private String passkey; // 密码， 非空， 正则判断

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPhone() {
        return phone;
    }

    public void setPasskey(String passkey) {
        this.passkey = passkey;
    }

    public String getPasskey() {
        return passkey;
    }

}
