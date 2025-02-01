package com.example.checkdulu.client.response;

import com.example.checkdulu.client.response.FoodId;

public record BarcodeResponse(FoodId food_id) {
    public BarcodeResponse(int id){
        this(new FoodId(id));
    }
}
