package com.example.pharmacie;

import com.google.gson.annotations.SerializedName;

// Pharmacy.java
public class Pharmacy {
    private int id;
    private String name;
    private String street;
    private String city;     // addr:city ou fallback
    private String state;    // state_id.name
    private double latitude;
    private double longitude;
    private String category;

    @SerializedName("distance")
    private Object distanceRaw;

    public Double getDistance() {
        if (distanceRaw == null) return null;

        try {
            if (distanceRaw instanceof Number) {
                return ((Number) distanceRaw).doubleValue();
            }
            if (distanceRaw instanceof String) {
                String s = (String) distanceRaw;
                if (s.equalsIgnoreCase("false") || s.trim().isEmpty()) return null;
                return Double.parseDouble(s);
            }
        } catch (Exception e) {
            return null;
        }
        return null;
    }



    // Getters
    public int getId(){return id;}
    public String getName() { return name; }
    public String getStreet() { return street; }
    public String getCity() { return city; }
    public String getState() { return state; }
    public double getLatitude() { return latitude; }
    public double getLongitude() { return longitude; }
    public String getCategory() { return category; }
}
