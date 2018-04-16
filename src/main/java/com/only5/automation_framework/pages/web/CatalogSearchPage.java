package com.only5.automation_framework.pages.web;

import com.only5.automation_framework.data.Product;
import java.util.ArrayList;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

public class CatalogSearchPage extends BasePage {

    By productsGridSection = By.className("products-grid");

    public boolean isProductsGridSectionPresent() {
        return isElementPresent(productsGridSection);
    }

    public List<Product> getAllProductsFromTheList() {
        List<WebElement> elements = waitForElementsBy(waitForElement(productsGridSection), By.xpath(".//li[contains(@class,'product-item')]"));
        List<Product> products = new ArrayList<>();
        for (WebElement element : elements) {
            Product product = new Product();
            product.setProductImage(element.findElement(By.className("product-image-photo")));
            product.setPrice(Float.parseFloat(element.findElement(By.xpath(".//span[@data-price-type='finalPrice']")).getAttribute("data-price-amount")));
            product.setProductName(element.findElement(By.className("product-item-name")).getText());
            product.setRating(Integer.parseInt(element.findElement(By.className("rating-result")).getText().replace("%", "")));
            product.setAddToCartButton(element.findElement(By.xpath(".//button[@title='Add to Cart']")));
            products.add(product);
        }
        return products;
    }

    public Product selectProductByIndex(int index) {
        Product product = getAllProductsFromTheList().get(index);
        product.getAddToCartButton().click();
        return product;
    }
}
