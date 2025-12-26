package com.smartshopper;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class ShoppingListActivity extends AppCompatActivity {

    private ArrayAdapter<String> adapter;
    private List<ShoppingItem> items;
    private List<String> displayList = new ArrayList<>();
    private TextView totalText;
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_list);

        totalText = findViewById(R.id.totalPriceText);
        listView = findViewById(R.id.shoppingListView);

        // Adapter initialization
        adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                displayList
        );
        listView.setAdapter(adapter);

        refreshList();

        // LONG PRESS: decrease quantity by 1
        listView.setOnItemLongClickListener((parent, view, position, id) -> {
            ShoppingItem item = items.get(position);

            // Decrease quantity by 1
            ShoppingRepository.decreaseQuantity(item.getName());

            // Feedback
            if (item.getQuantity() > 0) {
                Toast.makeText(
                        this,
                        item.getName() + " quantity decreased to " + item.getQuantity(),
                        Toast.LENGTH_SHORT
                ).show();
            } else {
                Toast.makeText(
                        this,
                        item.getName() + " removed from list",
                        Toast.LENGTH_SHORT
                ).show();
            }

            refreshList(); // updates UI immediately
            return true;
        });
    }

    // Refresh displayList and update adapter
    private void refreshList() {
        items = ShoppingRepository.getItems();
        displayList.clear();

        for (ShoppingItem item : items) {
            displayList.add(
                    item.getName() + " x" + item.getQuantity() +
                            " - RM " + String.format("%.2f", item.getPrice() * item.getQuantity())
            );
        }

        adapter.notifyDataSetChanged(); // update UI
        totalText.setText(
                "Total: RM " + String.format("%.2f", ShoppingRepository.getTotalPrice())
        );
    }
}
