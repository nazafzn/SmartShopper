package com.smartshopper;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ShoppingRepository {

    private static final List<ShoppingItem> items = new ArrayList<>();

    // Add or increase quantity
    public static void addItem(String name, double price) {
        for (ShoppingItem item : items) {
            if (item.getName().equalsIgnoreCase(name)) {
                item.increaseQuantity();
                return;
            }
        }
        items.add(new ShoppingItem(name, 1, price));
    }

    // Remove item completely
    public static void removeItem(String name) {
        Iterator<ShoppingItem> iterator = items.iterator();
        while (iterator.hasNext()) {
            ShoppingItem item = iterator.next();
            if (item.getName().equalsIgnoreCase(name)) {
                iterator.remove();
                return;
            }
        }
    }

    // Decrease quantity by 1
    public static void decreaseQuantity(String name) {
        Iterator<ShoppingItem> iterator = items.iterator();
        while (iterator.hasNext()) {
            ShoppingItem item = iterator.next();
            if (item.getName().equalsIgnoreCase(name)) {
                item.decreaseQuantity();
                if (item.getQuantity() <= 0) {
                    iterator.remove();
                }
                return;
            }
        }
    }

    // Get all items
    public static List<ShoppingItem> getItems() {
        return items;
    }

    // Calculate total price
    public static double getTotalPrice() {
        double total = 0.0;
        for (ShoppingItem item : items) {
            total += item.getQuantity() * item.getPrice();
        }
        return total;
    }

    // Clear shopping list
    public static void clearList() {
        items.clear();
    }

    // For Audio (TTS)
    public static String getTotalAsSpeech() {
        return "Your total amount is RM " + String.format("%.2f", getTotalPrice());
    }
}
