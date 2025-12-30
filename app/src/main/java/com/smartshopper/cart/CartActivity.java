package com.smartshopper.cart;

import static android.content.ContentValues.TAG;

import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.smartshopper.R;

import java.util.Locale;

public class CartActivity extends AppCompatActivity implements OnCartUpdateListener {

    private RecyclerView rvCartItems;
    private CartAdapter cartAdapter;
    private CartManager cartManager;
    private TextView tvTotalPrice;
    private LinearLayout emptyCartState;
    private TextToSpeech textToSpeech;
    private boolean ttsInitialized = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        // Initialize views
        rvCartItems = findViewById(R.id.rv_cart_items);
        tvTotalPrice = findViewById(R.id.tv_total_price);
        emptyCartState = findViewById(R.id.empty_cart_state);
        View totalCartContainer = findViewById(R.id.total_cart_container);
        ImageButton btnBack = findViewById(R.id.btn_back_cart);
        ImageButton btnTtsToggle = findViewById(R.id.btn_tts_toggle_cart);
        Button btnStartShopping = findViewById(R.id.btn_start_shopping);

        // Get CartManager instance
        cartManager = CartManager.getInstance();

        // Set up RecyclerView
        rvCartItems.setLayoutManager(new LinearLayoutManager(this));

        // Set up adapter
        cartAdapter = new CartAdapter(cartManager.getCartItems(), this);
        rvCartItems.setAdapter(cartAdapter);

        // Initialize TTS
        textToSpeech = new TextToSpeech(this, status -> ttsInitialized = true);

        // Set initial total price
        updateTotalPrice();
        toggleEmptyState();

        // Set click listeners
        btnBack.setOnClickListener(v -> finish());
        btnTtsToggle.setOnClickListener(v -> Toast.makeText(this, "tts button clicked", Toast.LENGTH_SHORT).show());
        totalCartContainer.setOnClickListener(v -> {speakTotalPrice(String.format("Your total is: %s", speakProductPrice(cartManager.getTotalPrice())));});
        btnStartShopping.setOnClickListener(v -> finish());
    }

    private void speakTotalPrice(String totalPrice) {
        if (!ttsInitialized) {
            return;
        }
        textToSpeech.speak(totalPrice, TextToSpeech.QUEUE_FLUSH, null, null);

    }

    private String speakProductPrice(double price) {
        // Split the price into whole numbers and decimals
        int ringgit = (int) price;
        int cents = (int) Math.round((price - ringgit) * 100);

        StringBuilder priceString = new StringBuilder();

        // Handle Ringgit part
        if (ringgit > 0) {
            priceString.append(ringgit).append(" Ringgit");
        }

        // Handle Cents part
        if (cents > 0) {
            if (ringgit > 0) {
                priceString.append(" and ");
            }
            priceString.append(cents).append(" cents");
        }

        // Handle cases where price might be 0.00
        if (ringgit == 0 && cents == 0) {
            return "Zero Ringgit";
        }

        return priceString.toString();
    }

    @Override
    public void onCartUpdated() {
        updateTotalPrice();
        toggleEmptyState();
    }

    @Override
    public void onSpeakItemInCart(String text) {
        if (!ttsInitialized) {
            return;
        }
        textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null, null);
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
