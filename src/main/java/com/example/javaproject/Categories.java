package com.example.javaproject;
import java.util.List;

public class Categories {


    public String category;
    public List<Products> products;


    public Categories() {}

    public Categories(String categoryName, List<Products> products) {
        this.category = categoryName;
        this.products = products;
    }

    public String getCategory() {
        return category;
    }
    public void setCategory(String category) {
        this.category = category;
    }

    public List<Products> getProducts() {
        return products;
    }
    public void setProducts(List<Products> products) {
        this.products = products;
    }

    @Override
    public String toString() {
        return category + ": " + products;
    }




}
