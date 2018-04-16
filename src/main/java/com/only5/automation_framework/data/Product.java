package com.only5.automation_framework.data;

import org.openqa.selenium.WebElement;

public class Product {
    WebElement productImage;
    float price;
    String productName;
    int rating;
    WebElement addToCartButton;

    public WebElement getProductImage() {
        return productImage;
    }

    public void setProductImage(WebElement productImage) {
        this.productImage = productImage;
    }

    public WebElement getAddToCartButton() {
        return addToCartButton;
    }

    public void setAddToCartButton(WebElement addToCartButton) {
        this.addToCartButton = addToCartButton;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }
}
