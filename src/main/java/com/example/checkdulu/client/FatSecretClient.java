package com.example.checkdulu.client;

import com.example.checkdulu.client.response.BarcodeResponse;
import com.example.checkdulu.client.response.TokenResponse;
import com.example.checkdulu.data.AccessToken;
import com.example.checkdulu.data.InfoResponse;
import com.example.checkdulu.database.TokenStore;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.util.Base64;
import java.util.Optional;

import static java.net.http.HttpResponse.BodyHandlers.ofString;
import static java.time.Duration.ofSeconds;

@Component
public class FatSecretClient {
    public static final String APPLICATION_JSON = "application/json";
    public static final String BEARER_PREFIX = "Bearer ";
    public static final String ACCEPT = "Accept";
    public static final String CONTENT_TYPE = "Content-Type";
    public static final String AUTHORIZATION = "Authorization";


    private final HttpClient client;
    private final Gson gson;
    private final TokenStore tokenStore;

    private final String clientId;
    private final String clientSecret;
    private final String infoUrl;
    private final String tokenUrl;

    public FatSecretClient(
            HttpClient client,
            Gson gson,
            TokenStore tokenStore,
            @Value("${fatsecret.id}") String clientId,
            @Value("${fatsecret.secret}")String clientSecret,
            @Value("${fatsecret.info-url}") String infoUrl,
            @Value("${fatsecret.token-url}") String tokenUrl
    ) {
        this.client = client;
        this.gson = gson;
        this.tokenStore = tokenStore;
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.infoUrl = infoUrl;
        this.tokenUrl = tokenUrl;
    }


    public Optional<InfoResponse> callExternalInfoService(String barcode){
        return callService(
                STR."\{infoUrl}/rest/food/barcode/find-by-id/v1?barcode=\{barcode}&format=json",
                BarcodeResponse.class,
                BEARER_PREFIX + getAccessToken()
        ).filter(id -> id.food_id().value() != 0).flatMap(
                bc -> callService(
                        STR."\{infoUrl}/rest/food/v4?food_id=\{bc.food_id().value()}&format=json",
                        InfoResponse.class,
                        BEARER_PREFIX + getAccessToken()
                )
        );
    }

    private String getAccessToken(){
        return tokenStore
                .loadToken()
                .filter(accessToken -> accessToken.timeStamp() > System.currentTimeMillis())
                .orElseGet(this::updateToken).token();
    }

    private AccessToken updateToken() {
        var tokenResponse = callServicePost(STR."\{tokenUrl}/connect/token",
                TokenResponse.class,
                STR."\{clientId}:\{clientSecret}")
                .orElseThrow();
        AccessToken accessToken = new AccessToken(
                System.currentTimeMillis() + (tokenResponse.expires_in() * 1_000L),
                tokenResponse.access_token());
        tokenStore.saveToken(accessToken);
        return accessToken;
    }

    private <T> Optional<T> callServicePost(String call, Class<T> responseType, String authToken) {
        String auth = Base64.getEncoder().encodeToString(authToken.getBytes());

        // Form data
        String requestBody = "grant_type=client_credentials";

        // Build the request
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(call))
                .header("Authorization", STR."Basic \{auth}")
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();
        try {
            var response = client.send(request, ofString());
            if (response.statusCode() == 200) {
                return Optional.of(gson.fromJson(response.body(), responseType));
            }
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
        return Optional.empty();
    }

        private <T> Optional<T> callService(String STR, Class<T> responseType, String auth) {
        var request = HttpRequest.newBuilder()
                .uri(URI.create(STR))
                .header(ACCEPT, APPLICATION_JSON)
                .header(CONTENT_TYPE, APPLICATION_JSON)
                .header(AUTHORIZATION, auth)
                .timeout(ofSeconds(5))
                .GET()
                .build();
        try {
            var response = client.send(request, ofString());
            if(response.statusCode() == 200){
                return Optional.of(gson.fromJson(response.body(), responseType));
            }
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
        return Optional.empty();
    }
}