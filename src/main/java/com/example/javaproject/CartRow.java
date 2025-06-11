package com.example.javaproject;

/**
 * Klasa pomocnicza do wyświetlania pozycji w koszyku:
 * • name – nazwa produktu,
 * • quantity – ilość (double) w koszyku,
 * • unit – jednostka (String), żeby np. wyświetlać "L", "kg", "pcs".
 */
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