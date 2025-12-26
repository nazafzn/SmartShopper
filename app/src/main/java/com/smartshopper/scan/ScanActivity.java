package com.smartshopper.scan;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.TextureView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.smartshopper.R;
import com.smartshopper.cart.CartActivity;

import java.util.List;

public class ScanActivity extends AppCompatActivity implements OnDetectionListener {

    private static final int CAMERA_PERMISSION_REQUEST_CODE = 100;
    private CameraManager cameraManager;
    private TextureView textureView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);

        textureView = findViewById(R.id.camera_preview);
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
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            cameraManager.start();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    protected void onPause() {
        cameraManager.stop();
        super.onPause();
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

    @Override
    public void onDetections(List<DetectionResult> results) {
        // TODO: Handle detection results. For now, let's just show a toast.
        if (results != null && !results.isEmpty()) {
            runOnUiThread(() -> {
                DetectionResult bestResult = results.get(0); // Assuming the first is the best
                String message = "Detected: " + bestResult.getLabel() + " with confidence " + bestResult.getConfidence();
                Toast.makeText(ScanActivity.this, message, Toast.LENGTH_SHORT).show();
            });
        }
    }
}
