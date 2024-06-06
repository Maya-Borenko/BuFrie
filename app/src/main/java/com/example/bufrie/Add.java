package com.example.bufrie;

public class Add {
    int price, id;
    byte[] image;
    String title;
    String seller;
    public String descriptions;
    public boolean relevant;
    public boolean like = false;
    Add(int id, int price, byte[] image, String title, String seller){
        this.id = id;
        this.price = price;
        if (image != null) {
            this.image = new byte[image.length];
            for (int i = 0; i < image.length; i++) this.image[i] = image[i];
        }
        this.title = title;
        this.seller = seller;
    }
}
