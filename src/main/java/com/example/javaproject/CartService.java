package com.example.javaproject;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class CartService {

    private static final CartService instance = new CartService();
    private static final Path CART_PATH = Paths.get("cart.json");

    private List<CartItem> cartItems;

    private CartService() {
        loadCartFromFile();
    }

    public static CartService getInstance() {
        return instance;
    }

    public synchronized List<CartItem> getCartItems() {
        return new ArrayList<>(cartItems);
    }

    public synchronized void addToCart(CartItem item) {
        for (CartItem existing : cartItems) {
            if (existing.getName().equals(item.getName())) {
                existing.setQuantity(existing.getQuantity() + item.getQuantity());
                return;
            }
        }
        cartItems.add(item);
    }

    public synchronized void removeFromCart(String productName) {
        cartItems.removeIf(item -> item.getName().equals(productName));
    }

    public synchronized void clearCart() {
        cartItems.clear();
    }

    public synchronized void saveCartToFile() {
        try {
            String json = new GsonBuilder().setPrettyPrinting().create().toJson(cartItems);
            Files.writeString(CART_PATH, json);
        } catch (Exception e) {
            throw new RuntimeException("Nie udało się zapisać koszyka: " + e.getMessage(), e);
        }
    }

    private void loadCartFromFile() {
        try {
            var resource = getClass().getResourceAsStream("/com/example/javaproject/cart.json");
            if (resource != null) {
                Reader reader = new InputStreamReader(resource);
                Type listType = new TypeToken<List<CartItem>>() {}.getType();
                cartItems = new Gson().fromJson(reader, listType);
            } else {
                cartItems = new ArrayList<>();
                System.out.println("Cart file not found in resources.");
            }
        } catch (Exception e) {
            cartItems = new ArrayList<>();
            e.printStackTrace();
        }
    }
}
