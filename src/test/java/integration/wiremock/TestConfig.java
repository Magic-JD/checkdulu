package integration.wiremock;

import com.example.checkdulu.client.response.BarcodeResponse;
import com.example.checkdulu.client.response.TokenResponse;
import com.example.checkdulu.data.InfoResponse;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.google.gson.Gson;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static constants.TestConstants.*;

@TestConfiguration
public class TestConfig {




    @Bean
    public WireMockServer fatSecretWiremock(Gson gson){
        var wireMockServer = new WireMockServer(8081);
        configureFor(8081);
        wireMockServer.stubFor(get(urlEqualTo("/rest/food/barcode/find-by-id/v1?barcode=123&format=json"))
                .willReturn(aResponse().withBody(gson.toJson(
                        new BarcodeResponse(PRODUCT_ID)
                ))));
        wireMockServer.stubFor(get(urlEqualTo("/rest/food/v4?food_id=1&format=json"))
                .willReturn(aResponse().withBody(gson.toJson(
                        new InfoResponse(
                                PRODUCT_NAME,
                                SUGAR,
                                METRIC_SERVING_AMOUNT,
                                SERVING_UNIT_GRAM)
                ))));
        wireMockServer.stubFor(post(urlMatching("/connect/token"))
                .willReturn(aResponse().withBody(gson.toJson(new TokenResponse("TOKEN", 10000)))));
        return wireMockServer;
    }

}
