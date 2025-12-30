package com.smartshopper.data.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.smartshopper.data.entity.ProductEntity;

import java.util.List;

@Dao
public interface ProductDao {

    @Query("SELECT * FROM products WHERE label = :label LIMIT 1")
    ProductEntity getProductByLabel(String label);

    @Query("SELECT * FROM products")
    List<ProductEntity> getAllProducts();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertProducts(List<ProductEntity> products);

    @Query("SELECT COUNT(*) FROM products")
    int getProductCount();

    @Query("DELETE FROM products")
    void clearDatabase();
}
