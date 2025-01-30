package com.example.checkdulu.data;

import java.util.Optional;

public record ProductInfo(String name, double sugarPer100g){
    public double sugarPerXg(double x){
        return (sugarPer100g/100) * x;
    }

    public static Optional<ProductInfo> fromInfoResponse(InfoResponse info){
        String name = info.food().food_name();
        return  info.food().servings().serving().stream()
                .filter(s -> s.metric_serving_unit().equals("g"))
                .findFirst()
                .map(serving ->
                        new ProductInfo(name, (serving.sugar()/serving.metric_serving_amount()) * 100));
    }
}