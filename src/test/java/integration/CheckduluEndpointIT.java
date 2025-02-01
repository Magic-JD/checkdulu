package integration;

import com.example.checkdulu.Checkdulu;
import com.github.tomakehurst.wiremock.WireMockServer;
import constants.TestConstants;
import integration.wiremock.TestConfig;
import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Duration;
import java.util.Objects;

import static constants.TestConstants.PRODUCT_NAME;
import static constants.TestConstants.SUGAR;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest(classes = {Checkdulu.class, TestConfig.class})
class CheckduluEndpointIT {

	private final MockMvc mockMvc;

	private final WireMockServer wireMockServer;

	@Autowired
	CheckduluEndpointIT(MockMvc mockMvc, WireMockServer wireMockServer) {
        this.mockMvc = mockMvc;
        this.wireMockServer = wireMockServer;
    }

	@BeforeEach
    void before(){
		wireMockServer.start();
	}

	@AfterEach
	void after(){
		wireMockServer.stop();
	}

    @Test
	void shouldShowLandingPageWhenLandingPageIsCalled() throws Exception {
		mockMvc.perform(get("/")).andExpect(status().isOk());
	}
	@Test
	void shouldShowDetailsWhenServiceIsCalledWithBarcode() throws Exception {
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.set("HX-Request", "true");
		MockHttpServletResponse response = mockMvc.perform(get("/info")
				.param("barcode", "123")
				.headers(httpHeaders)
		).andExpect(status().isOk()).andReturn().getResponse();
		Document doc = Jsoup.parse(response.getContentAsString());
		assertEquals(STR."Product name: \{PRODUCT_NAME}", Objects.requireNonNull(doc.getElementById("productName")).text());
		assertEquals(STR."Sugar per given amount: \{SUGAR}", Objects.requireNonNull(doc.getElementById("sugarPerGivenAmount")).text());
	}
}
