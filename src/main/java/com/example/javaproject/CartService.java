package com.example.javaproject;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class CartService {

    private static final CartService instance = new CartService();
    private static final String CART_FILENAME = "cart.json";

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
        try (Writer writer = new FileWriter(CART_FILENAME)) {
            String json = new GsonBuilder().setPrettyPrinting().create().toJson(cartItems);
            writer.write(json);
            writer.flush();
            System.out.println("Cart saved to: " + new File(CART_FILENAME).getAbsolutePath());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadCartFromFile() {
        File file = new File(CART_FILENAME);
        if (file.exists()) {
            try (Reader reader = new FileReader(file)) {
                Type listType = new TypeToken<List<CartItem>>() {}.getType();
                cartItems = new Gson().fromJson(reader, listType);
                if (cartItems == null) cartItems = new ArrayList<>();
                System.out.println("Cart loaded from file.");
            } catch (Exception e) {
                e.printStackTrace();
                cartItems = new ArrayList<>();
            }
        } else {
            try (InputStream is = getClass().getResourceAsStream("/com/example/javaproject/cart.json")) {
                if (is != null) {
                    try (Reader reader = new InputStreamReader(is)) {
                        Type listType = new TypeToken<List<CartItem>>() {}.getType();
                        cartItems = new Gson().fromJson(reader, listType);
                        if (cartItems == null) cartItems = new ArrayList<>();
                        System.out.println("Cart loaded from resource.");
                    }
                } else {
                    cartItems = new ArrayList<>();
                    System.out.println("Cart resource not found.");
                }
            } catch (Exception e) {
                e.printStackTrace();
                cartItems = new ArrayList<>();
            }
        }
    }
}
