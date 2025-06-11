package com.example.javaproject;

public class CartRow {

    private final String name;
    private final double quantity;
    private final String unit;

    public CartRow(String name, double quantity, String unit) {
        this.name = name;
        this.quantity = quantity;
        this.unit = unit;
    }

    public String getName() {
        return name;
    }
    public double getQuantity() {
        return quantity;
    }
    public String getUnit() {
        return unit;
    }
}