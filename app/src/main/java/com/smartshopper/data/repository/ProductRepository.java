package com.smartshopper.data.repository;

import android.content.Context;

import com.smartshopper.data.database.AppDatabase;
import com.smartshopper.data.dao.ProductDao;
import com.smartshopper.data.entity.ProductEntity;

import java.util.Arrays;
import java.util.List;

public class ProductRepository {

    private final ProductDao productDao;

    public ProductRepository(Context context) {
        AppDatabase db = AppDatabase.getInstance(context);
        productDao = db.productDao();
    }

    public void insertProducts(List<ProductEntity> products) {
        new Thread(() -> productDao.insertProducts(products)).start();
    }

    public void insertAllProducts() {
        List<ProductEntity> products = Arrays.asList( // TODO put accurate prices later
                new ProductEntity("instant_noodles_maggi_kari", "Instant Noodles", 5.0, "Maggi", "Quick and tasty Maggi curry noodles."),
                new ProductEntity("biscuits_quaker", "Biscuits", 4.0, "Quaker", "Crispy Quaker biscuits, perfect with tea."),
                new ProductEntity("olive_oil_naturel", "Olive Oil", 20.0, "Naturel", "Pure Naturel olive oil for cooking and salads."),
                new ProductEntity("cereal_kokokrunch", "Cereal", 15.0, "Kokokrunch", "Crunchy Kokokrunch cereal for breakfast."),
                new ProductEntity("Biscuit Chips More", "Biscuits", 15.0, "Chips More", "Chips More biscuits, a chocolate treat."),
                new ProductEntity("Biscuit Munchy's", "Biscuits", 10.0, "Munchy's", "Munchy's biscuits, light and tasty."),
                new ProductEntity("Bread Sunshine", "Bread", 5.0, "Sunshine", "Soft Sunshine bread, great for sandwiches."),
                new ProductEntity("Instant Noodles Chef", "Instant Noodles", 6.0, "Chef", "Chef brand instant noodles, quick meal."),
                new ProductEntity("Shampoo Sunsilk", "Shampoo Sunsilk Clean & Fresh", 10.0, "Sunsilk", "Sunsilk shampoo for hair care.")
                // Add remaining 8 products here; didn't add because not sure
        );

        insertProducts(products);
    }

    public ProductEntity getProductByLabel(String label) {
        return productDao.getProductByLabel(label);
    }

    public List<ProductEntity> getAllProducts() {return productDao.getAllProducts();}

    public int getProductCount() {
        return productDao.getProductCount();
    }

    public void clearDatabase() {
        new Thread(() -> productDao.clearDatabase()).start();
    }
}
