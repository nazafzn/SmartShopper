package com.smartshopper.home;

import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.smartshopper.R;
import com.smartshopper.cart.CartActivity;
import com.smartshopper.cart.CartManager;
import com.smartshopper.scan.ScanActivity;

import java.util.Locale;

public class HomeActivity extends AppCompatActivity {

    private CartManager cartManager;
    private TextView tvCartItemCount;
    private TextView tvCartTotal;
    private TextToSpeech textToSpeech;
    private boolean ttsInitialized = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Initialize TTS
        textToSpeech = new TextToSpeech(this, status -> ttsInitialized = true);

        // Get CartManager instance
        cartManager = CartManager.getInstance();

        // Initialize views
        CardView btnScan = findViewById(R.id.btn_scan);
        CardView btnViewCart = findViewById(R.id.btn_view_cart);
        tvCartItemCount = findViewById(R.id.tv_cart_item_count);
        tvCartTotal = findViewById(R.id.tv_cart_total);
        ImageButton btnTtsToggle = findViewById(R.id.btn_tts_toggle);
        BottomNavigationView bottomNav = findViewById(R.id.bottom_nav);

        // Set up click listeners
        btnScan.setOnClickListener(v -> startActivity(new Intent(this, ScanActivity.class)));
        btnViewCart.setOnClickListener(v -> startActivity(new Intent(this, CartActivity.class)));
        btnTtsToggle.setOnClickListener(v -> speakAppInfo("Welcome to ShopMate! Start by scanning a product to add it to your cart."));

        bottomNav.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_scan) {
                startActivity(new Intent(HomeActivity.this, ScanActivity.class));
                return true;
            } else if (itemId == R.id.nav_cart) {
                startActivity(new Intent(HomeActivity.this, CartActivity.class));
                return true;
            } else if (itemId == R.id.nav_home) {
                return true;
            }
            return false;
        });

        // Update cart info
        updateCartInfo();
    }

    private void speakAppInfo(String text) {
        if (!ttsInitialized) {
            return;
        }
        textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null, null);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Update cart info when returning to the activity
        updateCartInfo();
    }

    private void updateCartInfo() {
        tvCartItemCount.setText(String.format(Locale.getDefault(), "%d items", cartManager.getItemCount()));
        tvCartTotal.setText(String.format(Locale.getDefault(), "RM%.2f", cartManager.getTotalPrice()));
    }
}
