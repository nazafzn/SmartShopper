package com.smartshopper;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.smartshopper.data.repository.ProductRepository;
import com.smartshopper.home.HomeActivity;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    private static final int SPLASH_DELAY = 1000; // 1 seconds

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ProductRepository repository = new ProductRepository(this);

//        repository.clearDatabase();
        Executor executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            if (repository.getProductCount() == 0) {
                repository.insertAllProducts();
            }
        });



        new Handler().postDelayed(() -> {
            // Start the main activity after the splash delay
            Intent intent = new Intent(MainActivity.this, HomeActivity.class);
            startActivity(intent);
            finish();
        }, SPLASH_DELAY);
    }
}
