package com.example.checkdulu.data;

import java.util.List;

public record InfoResponse(Food food) { }
record Food(String food_name, Servings servings){}

record Servings(List<Serving> serving){}
record Serving(Double sugar, Double metric_serving_amount, String metric_serving_unit){}
