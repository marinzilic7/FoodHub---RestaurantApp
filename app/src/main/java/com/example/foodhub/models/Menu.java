package com.example.foodhub.models;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class Menu {

    public String menuId;
    public String name;
    public String price;
    public String category;
    public String image;

    public String role;


    public String getMealId() {
        return menuId;
    }

    public Menu() {
    }

    public Menu(String menuId, String name, String price, String category, String image){
        this.menuId = menuId;
        this.name = name;
        this.price = price;
        this.category = category;
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

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
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