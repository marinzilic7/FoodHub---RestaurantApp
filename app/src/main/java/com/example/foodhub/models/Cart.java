package com.example.foodhub.models;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class Cart {

    public String cartId;
    public String name;
    public String price;
    public String image;

    public String role;


    public String getCartId() {
        return cartId;
    }

    public String cartId() {
        return cartId;
    }

    public Cart() {
    }

    public Cart(String cartId, String name, String price, String image){
        this.cartId = cartId;
        this.name = name;
        this.price = price;
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }


}