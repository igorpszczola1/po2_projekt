package com.example.javaproject;

public class Products {

    private String category;
    private String name;
    private double quantity;
    private double initialQuantity;
    private String unit;

    public Products() { }

    public Products(String category, String name, double quantity, String unit) {
        this.category = category;
        this.name = name;
        this.quantity = quantity;
        this.initialQuantity = quantity;
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

    public double getQuantity() {
        return quantity;
    }
    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }

    public double getInitialQuantity() {
        return initialQuantity;
    }
    public void setInitialQuantity(double initialQuantity) {
        this.initialQuantity = initialQuantity;
    }

    public String getUnit() {
        return unit;
    }
    public void setUnit(String unit) {
        this.unit = unit;
    }

    @Override
    public String toString() {
        return String.format(
                "%s (%s: %.2f %s, init=%.2f)",
                name, category, quantity, unit, initialQuantity
        );
    }
}