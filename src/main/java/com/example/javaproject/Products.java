package com.example.javaproject;

public class Products {

    private String category;
    private String name;
    private String unit;

    public Products() { }

    public Products(String category, String name, String unit) {
        this.category = category;
        this.name = name;
        this.unit = unit;
    }

    public String getCategory() {
        return category;
    }
    public void setCategory(String category) {
        this.category = category;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getUnit() {
        return unit;
    }
    public void setUnit(String unit) {
        this.unit = unit;
    }

    @Override
    public String toString() {
        return String.format("%s (%s, %s)", name, category, unit);
    }
}
