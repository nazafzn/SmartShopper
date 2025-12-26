package com.example.smartshopper.cart;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartshopper.R;

import java.util.List;
import java.util.Locale;

public class CartActivity extends AppCompatActivity implements OnCartUpdateListener {

    private RecyclerView rvCartItems;
    private CartAdapter cartAdapter;
    private CartManager cartManager;
    private TextView tvTotalPrice;
    private LinearLayout emptyCartState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        // Initialize views
        rvCartItems = findViewById(R.id.rv_cart_items);
        tvTotalPrice = findViewById(R.id.tv_total_price);
        emptyCartState = findViewById(R.id.empty_cart_state);
        ImageButton btnBack = findViewById(R.id.btn_back_cart);
        ImageButton btnTtsToggle = findViewById(R.id.btn_tts_toggle_cart);
        Button btnCheckout = findViewById(R.id.btn_checkout);
        Button btnStartShopping = findViewById(R.id.btn_start_shopping);

        // Get CartManager instance
        cartManager = CartManager.getInstance();

        // Set up RecyclerView
        rvCartItems.setLayoutManager(new LinearLayoutManager(this));

        // Set up adapter
        cartAdapter = new CartAdapter(cartManager.getCartItems(), this);
        rvCartItems.setAdapter(cartAdapter);

        // Set initial total price
        updateTotalPrice();
        toggleEmptyState();

        // Set click listeners
        btnBack.setOnClickListener(v -> finish());
        btnTtsToggle.setOnClickListener(v -> Toast.makeText(this, "tts button clicked", Toast.LENGTH_SHORT).show());
        btnCheckout.setOnClickListener(v -> {
            Toast.makeText(CartActivity.this, "Checkout button clicked", Toast.LENGTH_SHORT).show();
        });
        btnStartShopping.setOnClickListener(v -> {
            // TODO: Navigate to shopping/scanning activity
            finish();
        });
    }

    @Override
    public void onCartUpdated() {
        updateTotalPrice();
        toggleEmptyState();
    }

    private void updateTotalPrice() {
        tvTotalPrice.setText(String.format(Locale.getDefault(), "RM%.2f", cartManager.getTotalPrice()));
    }

    private void toggleEmptyState() {
        if (cartManager.getCartItems().isEmpty()) {
            emptyCartState.setVisibility(View.VISIBLE);
            rvCartItems.setVisibility(View.GONE);
        } else {
            emptyCartState.setVisibility(View.GONE);
            rvCartItems.setVisibility(View.VISIBLE);
        }
    }
}
