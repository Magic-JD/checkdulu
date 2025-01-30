package com.example.checkdulu.service;

import com.example.checkdulu.client.FatSecretClient;
import com.example.checkdulu.data.ProductInfo;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class InfoService {

    private final FatSecretClient fatSecretClient;

    public InfoService(FatSecretClient fatSecretClient) {
        this.fatSecretClient = fatSecretClient;
    }

    public Optional<ProductInfo> getInfo(String barcode){
        return fatSecretClient.callExternalInfoService(barcode)
                .flatMap(ProductInfo::fromInfoResponse);
    }

}
