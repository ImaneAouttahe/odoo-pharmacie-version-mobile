package com.example.pharmacie;

import java.util.List;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
public interface PharmacieApi {

    @GET("/odoo_pharmacie/static/src/maroc_villes.csv")
    Call<ResponseBody> getCitiesCSV();

    @GET("/api/pharmacie/search_geo")
    Call<List<Pharmacy>> searchPharmacies(
            @Query("lat") Double lat,
            @Query("lon") Double lon,
            @Query("city") String city,
            @Query("name") String name
    );

    @GET("/api/pharmacie/search_permanence")
    Call<List<Pharmacy>> searchPermanence(@Query("city") String city);

    // 🔍 Recherche manuelle par ville / nom
    @GET("/api/pharmacie/search_manual")
    Call<List<Pharmacy>> searchManual(
            @Query("city") String city,
            @Query("name") String name
    );

    // Récupérer les médicaments d'une pharmacie (mobile)
    @GET("/api/pharmacie/medicaments")
    Call<List<Medicament>> getMedicaments(
            @Query("pharmacy_id") int pharmacyId,
            @Query("name") String name // facultatif, pour filtrer par nom
    );

}




