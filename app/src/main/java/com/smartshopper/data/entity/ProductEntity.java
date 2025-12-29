package com.smartshopper.data.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "products")
public class ProductEntity {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private String label;
    private String name;
    private double price;
    private String brand;
    private String description;

    public ProductEntity(String label, String name, double price, String brand, String description) {
        this.label = label;
        this.name = name;
        this.price = price;
        this.brand = brand;
        this.description = description;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getLabel() { return label; }
    public void setLabel(String label) { this.label = label; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }
    public String getBrand() { return brand; }
    public void setBrand(String brand) { this.brand = brand; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String toString() {return String.format("%s, %s, RM%.2f, %s, %s", label, name, price, brand, description);}
}
