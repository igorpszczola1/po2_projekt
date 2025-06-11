package com.example.javaproject;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class CartService {


    // singleton: jeden obiekt (static final) - aplikacja operuje na tylko jednej liscie (koszyku)

    private static final CartService instance = new CartService();
    private final Map<String, Double> cartQuantities = new HashMap<>();

    private CartService() {}

    public static CartService getInstance() {
        return instance;
    }


    public synchronized void addToCart(String productName, double amount) {
        if (productName == null || amount <= 0) return;
        cartQuantities.put(
                productName,
                cartQuantities.getOrDefault(productName, 0.0) + amount
        );
    }


    public synchronized void removeFromCart(String productName, double amount) {
        if (productName == null || amount <= 0) return;
        double current = cartQuantities.getOrDefault(productName, 0.0);
        double newVal = current - amount;
        if (newVal <= 0) {
            cartQuantities.remove(productName);
        } else {
            cartQuantities.put(productName, newVal);
        }
    }


    public synchronized Map<String, Double> getCartContents() {
        return Collections.unmodifiableMap(cartQuantities);
    }


    public synchronized void clearCart() {
        cartQuantities.clear();
    }
}