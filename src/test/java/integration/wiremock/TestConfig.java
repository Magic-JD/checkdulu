package integration.wiremock;

import com.example.checkdulu.client.response.BarcodeResponse;
import com.example.checkdulu.client.response.TokenResponse;
import com.example.checkdulu.data.InfoResponse;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.google.gson.Gson;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

@TestConfiguration
public class TestConfig {

    @Bean
    public WireMockServer fatSecretWiremock(Gson gson){
        var wireMockServer = new WireMockServer(8081);
        configureFor(8081);
        wireMockServer.stubFor(get(urlMatching("/rest/food/barcode/find-by-id/v1"))
                .willReturn(aResponse().withBody(gson.toJson(
                        new BarcodeResponse(1)
                ))));
        wireMockServer.stubFor(get(urlMatching("/rest/food/v4"))
                .willReturn(aResponse().withBody(gson.toJson(
                        new InfoResponse(
                                "Test food",
                                10d,
                                100d,
                                "g")
                ))));
        wireMockServer.stubFor(post(urlMatching("/connect/token"))
                .willReturn(aResponse().withBody(gson.toJson(new TokenResponse("TOKEN", 10000)))));
        return wireMockServer;
    }

}
