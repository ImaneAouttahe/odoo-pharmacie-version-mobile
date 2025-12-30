package com.example.pharmacie;
public class Medicament {
    private int id;
    private String name;
    private String packaging;
    private double price;
    private int quantity;
    private boolean available;
    private boolean has_image;

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getPackaging() {
        return packaging;
    }

    public double getPrice() {
        return price;
    }
    public int getQuantity() {
        return quantity;
    }

    public boolean isAvailable() {
        return available;
    }

    public boolean hasImage() {
        return has_image;
    }

}
