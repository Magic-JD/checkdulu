package com.example.checkdulu.data;

import java.util.List;

public record InfoResponse(Food food) {
    public InfoResponse(String name, double sugar, double metricServingAmount, String metricServingUnit){
        this(new Food(name, new Servings(List.of(new Serving(sugar, metricServingAmount, metricServingUnit)))));
    }
}
record Food(String food_name, Servings servings){}

record Servings(List<Serving> serving){}
record Serving(Double sugar, Double metric_serving_amount, String metric_serving_unit){}
