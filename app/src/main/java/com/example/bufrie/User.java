package com.example.bufrie;

public class User {
    public int id;
    public byte[] image;
    public String userName, password, phone;
    public boolean sell;
    User(String userName, boolean seller){
        this.userName = userName;
        this.sell = seller;
    }
    public void setImage(byte[] image){
        if (image != null) {
            this.image = new byte[image.length];
            for (int i = 0; i < image.length; i++) this.image[i] = image[i];
        }
    }
}
