package com.smartshopper.scan;

import static android.content.ContentValues.TAG;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.TextureView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.smartshopper.R;
import com.smartshopper.cart.CartActivity;
import com.smartshopper.data.entity.ProductEntity;
import com.smartshopper.data.repository.ProductRepository;
import com.smartshopper.ml.ProductDetector;
import com.smartshopper.model.DetectionResult;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class ScanActivity extends AppCompatActivity {

    // views
    private OverlayView viewfinder_overlay;
    private TextView tv_product_brand;
    private TextView tv_product_name;
    private TextView tv_product_price;
    private FloatingActionButton btn_add_to_cart;
    private View detection_result_container;

    // camera
    private static final int CAMERA_PERMISSION_REQUEST_CODE = 100;
    private CameraManager cameraManager;
    private TextureView textureView;

    //product detection
    private ProductDetector productDetector;
    private boolean isProcessing = false;
    private final Handler handler = new Handler(Looper.getMainLooper());
    private ProductRepository productRepository;

    // Text-to-Speech
    private TextToSpeech textToSpeech;
    private boolean ttsInitialized = false;
    private String lastSpokenProduct = "";
    private long lastSpeechTime = 0;
    private static final long TTS_COOLDOWN_MS = 5000; // avoid overlapping audio
    private static final long CARD_TIMEOUT_MS = 3000; // make the card stay longer after detection (5secs)


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);

        textureView = findViewById(R.id.camera_preview);
        viewfinder_overlay = findViewById(R.id.overlayView);
        tv_product_brand = findViewById(R.id.tv_product_brand);
        tv_product_name = findViewById(R.id.tv_product_name);
        tv_product_price = findViewById(R.id.tv_product_price);
        btn_add_to_cart = findViewById(R.id.btn_add_to_cart);
        detection_result_container = findViewById(R.id.detection_result_container);

        // Initialize camera manager
        cameraManager = new CameraManager(this, textureView);

        // Find buttons
        ImageButton btnBack = findViewById(R.id.btn_back);
        ImageButton btnTtsToggle = findViewById(R.id.btn_tts_toggle_scan);
        FloatingActionButton btnAddToCart = findViewById(R.id.btn_add_to_cart);
        Button btnViewCart = findViewById(R.id.btn_view_cart_from_scan);

        // Set click listeners
        btnBack.setOnClickListener(v -> finish());
        btnTtsToggle.setOnClickListener(v -> Toast.makeText(ScanActivity.this, "tts button clicked", Toast.LENGTH_SHORT).show());
        btnAddToCart.setOnClickListener(v -> Toast.makeText(ScanActivity.this, "Product added to cart", Toast.LENGTH_SHORT).show());
        btnViewCart.setOnClickListener(v -> startActivity(new Intent(ScanActivity.this, CartActivity.class)));

        // Initialize TFLite model
        try {
            productDetector = new ProductDetector(this);
            Log.d(TAG, "Model loaded successfully");
        } catch (IOException e) {
            Log.e(TAG, "Failed to load model", e);
            Toast.makeText(this, "Failed to load detection model", Toast.LENGTH_LONG).show();
            finish();
        }

        // initialize product repository
        productRepository = new ProductRepository(this);

        // Initialize TTS
        textToSpeech = new TextToSpeech(this, status -> ttsInitialized = true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            cameraManager.start();
            handler.post(detectionRunnable);
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        handler.removeCallbacks(detectionRunnable);
        cameraManager.stop();
        if (textToSpeech != null && textToSpeech.isSpeaking()) {
            textToSpeech.stop();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                cameraManager.start();
            } else {
                Toast.makeText(this, "Camera permission is required to use the scanner.", Toast.LENGTH_LONG).show();
                finish();
            }
        }
    }

    private final Runnable detectionRunnable = new Runnable() {
        @Override
        public void run() {
            // Only process if we aren't already busy with a previous frame
            if (textureView.isAvailable() && !isProcessing) {
                isProcessing = true;

                // Capture the current frame from TextureView
                // might need to change to imageProxy if tak jadi
                Bitmap bitmap = textureView.getBitmap();

                if (bitmap != null) {
                    // Run detection on a background thread to keep UI smooth
                    new Thread(() -> {
                        // This assumes your ProductDetector has a 'detect' method
                        // and it calls onDetections() when finished
                        productDetector.detect(bitmap,results -> {
                            Log.d(TAG, "Detection results: " + results);
                            runOnUiThread(() -> processDetectionResults(results));
                        });

                        // Allow the next frame to be processed
                        isProcessing = false;
                    }).start();
                } else {
                    isProcessing = false;
                }
            }
            // Run this again in 10fps
            handler.postDelayed(this, 100);
        }
    };

    private void processDetectionResults(List<DetectionResult> results) {
        if (results == null || results.isEmpty()) {
            //delay the card abit after detection
            if (detection_result_container.getVisibility() == View.VISIBLE) {
            handler.removeCallbacks(hideCardRunnable);
            handler.postDelayed(hideCardRunnable, CARD_TIMEOUT_MS);
            }

            viewfinder_overlay.setDetections(null);
            return;
        }

        // if theres a new detection
        handler.removeCallbacks(hideCardRunnable);

        // Get highest confidence detection
        DetectionResult topResult = results.get(0);
        viewfinder_overlay.setDetections(results);
        showProductDetailsAndSpeak(topResult.getLabel());
    }

    private final Runnable hideCardRunnable = () -> {
        detection_result_container.setVisibility(View.GONE);
        viewfinder_overlay.setDetections(null);
    };

    private void showProductDetailsAndSpeak(String label) {
        // Query the database in a background thread (Room doesn't allow main-thread queries)
        new Thread(() -> {
            ProductEntity product = productRepository.getProductByLabel(label);
            Log.d(TAG, label);


            if (product != null) {
                // Return to UI thread to show the data
                runOnUiThread(() -> {
                    detection_result_container.setVisibility(View.VISIBLE);
                    tv_product_name.setText(product.getName());
                    tv_product_brand.setText(product.getBrand());
                    tv_product_price.setText(String.format(Locale.US, "RM%.2f", product.getPrice()));

                    // TTS product details
                    speakProductDetails(String.format(Locale.US, "%s, Brand: %s, Price: %s", product.getName(), product.getBrand(), speakProductPrice(product.getPrice())));
                });
            } else {
                runOnUiThread(() -> {
                    // product is not in DB
                    detection_result_container.setVisibility(View.VISIBLE);
                    tv_product_name.setText(label);
                    tv_product_price.setText("Product information not available");

                    speakProductDetails("Product information not available");
                });
            }
        }).start();
    }

    // saje for better price speaking
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

    private void speakProductDetails(String product) {
        if (!ttsInitialized) {
            return;
        }

        long currentTime = System.currentTimeMillis();

        // Check if we should speak (cooldown period and different product)
        if (!product.equals(lastSpokenProduct) ||
                (currentTime - lastSpeechTime) > TTS_COOLDOWN_MS) {

            textToSpeech.speak(product, TextToSpeech.QUEUE_FLUSH, null, null);
            lastSpokenProduct = product;
            lastSpeechTime = currentTime;

            Log.d(TAG, "Speaking: " + product);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // Cleanup resources to prevent memory leaks, jacked my phone bcs this wasnt implemented earlier
        handler.removeCallbacks(detectionRunnable);

        if (cameraManager != null) {
            cameraManager.stop();
        }

        if (productDetector != null) {
            productDetector.close();
        }

        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
    }

}
