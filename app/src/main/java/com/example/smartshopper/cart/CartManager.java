package com.example.smartshopper.cart;

import java.util.ArrayList;
import java.util.List;

public class CartManager {
    private static CartManager instance;
    private List<CartItem> cartItems;

    private CartManager() {
        cartItems = new ArrayList<>();
        // Initialize with dummy data
        cartItems.add(new CartItem("1", "Apple", 4.50, 2, ""));
        cartItems.add(new CartItem("2", "Banana", 2.50, 3, ""));
        cartItems.add(new CartItem("3", "Milk", 7.00, 1, ""));
        cartItems.add(new CartItem("4", "Bread", 5.50, 1, ""));
        cartItems.add(new CartItem("5", "Eggs", 12.00, 1, ""));
        cartItems.add(new CartItem("6", "Chicken Breast", 25.00, 2, ""));
        cartItems.add(new CartItem("7", "Rice", 15.00, 1, ""));
        cartItems.add(new CartItem("8", "Tomatoes", 3.00, 5, ""));
        cartItems.add(new CartItem("9", "Onions", 2.00, 4, ""));
        cartItems.add(new CartItem("10", "Potatoes", 4.00, 6, ""));
    }

    public static synchronized CartManager getInstance() {
        if (instance == null) {
            instance = new CartManager();
        }
        return instance;
    }

    public List<CartItem> getCartItems() {
        return cartItems;
    }

    public double getTotalPrice() {
        double totalPrice = 0;
        for (CartItem item : cartItems) {
            totalPrice += item.getPrice() * item.getQuantity();
        }
        return totalPrice;
    }

    public int getItemCount() {
        int count = 0;
        for (CartItem item : cartItems) {
            count += item.getQuantity();
        }
        return count;
    }
}
