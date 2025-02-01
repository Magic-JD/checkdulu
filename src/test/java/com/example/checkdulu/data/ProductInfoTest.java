package com.example.checkdulu.data;

import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ProductInfoTest {

    public static final String NAME = "PRODUCT";
    public static final InfoResponse INFO_RESPONSE = new InfoResponse(new Food(NAME, new Servings(List.of(new Serving(10d, 100d, "g")))));
    public static final ProductInfo PRODUCT_INFO = new ProductInfo(NAME, 10.0);

    @Test
    void sugarPerXgShouldReturnCorrectValueFor100g() {
        assertEquals(10, PRODUCT_INFO.sugarPerXg(100));
    }

    @Test
    void sugarPerXgShouldReturnCorrectValueFor10g() {
        assertEquals(10, PRODUCT_INFO.sugarPerXg(10));
    }

    @Test
    void sugarPerXgShouldReturnCorrectValueForDoubleMaths() {
        assertEquals(1.05, PRODUCT_INFO.sugarPerXg(10.5));
    }

    @Test
    void fromInfoResponseShouldCreateValidProductInfoIfThereIsServingInGram() {
        assertEquals(PRODUCT_INFO, ProductInfo.fromInfoResponse(INFO_RESPONSE).orElseThrow());
    }

    @Test
    void fromInfoResponseShouldCreateEmptyProductInfoIfThereIsNoServingInGram() {
        var infoResponseWithoutGrams = new InfoResponse(new Food("", new Servings(Collections.emptyList())));
        assertTrue(ProductInfo.fromInfoResponse(infoResponseWithoutGrams).isEmpty());
    }
}