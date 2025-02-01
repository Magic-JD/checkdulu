package com.example.checkdulu.client.response;

public record BarcodeResponse(FoodId food_id) {
    public BarcodeResponse(int id){
        this(new FoodId(id));
    }
}
