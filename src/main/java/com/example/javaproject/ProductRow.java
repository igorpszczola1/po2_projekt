package com.example.javaproject;


public class ProductRow {
    private final String category;
    private final String name;
    private final double currentQuantity;
    private final double initialQuantity;
    private final String unit;

    public ProductRow(String category, String name, double currentQuantity, double initialQuantity, String unit) {
        this.category = category;
        this.name = name;
        this.currentQuantity = currentQuantity;
        this.initialQuantity = initialQuantity;
        this.unit = unit;
    }

    public String getCategory() {
        return category;
    }
    public String getName() {
        return name;
    }
    public double getCurrentQuantity() {
        return currentQuantity;
    }
    public double getInitialQuantity() {
        return initialQuantity;
    }
    public String getUnit() {
        return unit;
    }

    @Override
    public String toString() {
        return name + " (" + currentQuantity + " " + unit + ")";
    }
}