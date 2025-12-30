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
        //will put theses items in database everytime app is installed, prices based on lotus
        List<ProductEntity> products = Arrays.asList(
                //Rosie's items
                new ProductEntity("Bread Sunshine", "Bread Sunshine Australian Oat", 4.2, "Sunshine", "Soft Sunshine bread, great for sandwiches."),
                new ProductEntity("Instant Noodles Chef", "Instant Noodles Chef Laksa Nyonya", 8.9, "Chef", "Chef brand instant noodles, quick meal."),
                new ProductEntity("Biscuit Chips More", "Biscuits Chips More Mini", 12.8, "Chips More", "Chips More biscuits, a chocolate treat."),
                new ProductEntity("Biscuit Munchy's", "Biscuits Oat Krunch Breakfast", 8.3, "Munchy's", "Munchy's biscuits, light and tasty."),

                //Sheryl's items
                new ProductEntity("instant_noodles_maggi_kari", "Instant Noodles Maggi Kari", 6.2, "Maggi", "Quick and tasty Maggi curry noodles."),
                new ProductEntity("biscuits_quaker", "Biscuits Quaker Chocolate Chips", 8.6, "Quaker", "Crispy Quaker biscuits, perfect with tea."),
                new ProductEntity("olive_oil_naturel", "Olive Oil Naturel Extra Virgin", 53.4, "Naturel", "Pure Naturel olive oil for cooking and salads."),
                new ProductEntity("cereal_kokokrunch", "Cereal Kokokrunch", 13.9, "Nestle", "Crunchy Kokokrunch cereal for breakfast."),

                //Afzan's items
                new ProductEntity("Shampoo Sunsilk", "Shampoo Sunsilk Clean & Fresh", 15.9, "Sunsilk", "Sunsilk shampoo for hair care."),
                new ProductEntity("Toothpaste Colgate Triple Action", "Toothpaste Colgate Triple Action", 18.8, "Colgate", "Colgate toothpaste for oral care"),
                new ProductEntity("Toothpaste Darlie Double Action", "Toothpaste Darlie Double Action", 19.9, "Darlie", "Darlie toothpaste for oral"),
                new ProductEntity("Bread Gardenia Jumbo", "Bread Gardenia Jumbo", 4.3, "Gardenia", "Gardenia bread for sandwiches."),

                //Kee En's Items
                new ProductEntity("Dove Conditioner", "Dove Conditioner Intense Repair", 16.4, "Dove", "Dove conditioner for hair care."),
                new ProductEntity("milk_dutch_lady", "Milk Dutch Lady Full Cream", 5.6, "Dutch Lady", "Dutch Lady milk for breakfast."),
                new ProductEntity("Pokka Green Tea", "Pokka Green Tea", 4.5, "Pokka", "Pokka green tea for tea."),
                new ProductEntity("Mamalov Rice", "Mamalov Rice Triple A", 73.9, "Mamalov", "Mamalov rice for cooking."),
                new ProductEntity("Popo Light Soy Sauce", "Popo Light Soy Sauce", 9.8, "Popo", "Popo light soy sauce for cooking.")
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
