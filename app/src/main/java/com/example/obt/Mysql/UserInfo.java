package com.example.obt.Mysql;

public class UserInfo {
    private String phone; // 手机号码， 非空
    private String SID; // 学号，非空
    private String SName;   // 姓名非空
    private int Grade;  // 年级， 非空
    private String major;   // 专业， 非空
    private String Email;   // 邮箱，可选
    private String birthdate;   // 生日， 可选
    private char sex;   // 性别，可选
    private String LOC; // 地址，可选
    private int Qpoint; // Q点，非空，初值0
    private String image;   // 头像地址，可选

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPhone() {
        return phone;
    }

    public void setSID(String sid) {
        this.SID = sid;
    }

    public String getSID() {
        return SID;
    }

    public void setSName(String sName) {
        this.SName = SName;
    }

    public String getSName() {
        return SName;
    }

    public void setGrade(int grade) {
        this.Grade = grade;
    }

    public int getGrade() {
        return Grade;
    }

    public void setMajor(String major) {
        this.major = major;
    }

    public String getMajor() {
        return major;
    }

    public void setEmail(String email) {
        this.Email = email;
    }

    public String getEmail() {
        return Email;
    }

    public void setBirthdate(String birthdate) {
        this.birthdate = birthdate;
    }

    public String getBirthdate() {
        return birthdate;
    }

    public void setSex(char sex) {
        this.sex = sex;
    }

    public char getSex() {
        return sex;
    }

    public void setLOC(String loc) {
        this.LOC = loc;
    }

    public String getLOC() {
        return LOC;
    }

    public void setQpoint(int qpoint) {
        this.Qpoint = qpoint;
    }

    public int getQpoint() {
        return Qpoint;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getImage() {
        return image;
    }
}
