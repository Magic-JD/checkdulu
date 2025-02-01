package integration;

import com.example.checkdulu.Checkdulu;
import com.github.tomakehurst.wiremock.WireMockServer;
import integration.wiremock.TestConfig;
import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MockMvcBuilder;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest(classes = {Checkdulu.class, TestConfig.class})
class CheckduluIT {

	private final MockMvc mockMvc;

	private final WireMockServer wireMockServer;

	@Autowired
    CheckduluIT(MockMvc mockMvc, WireMockServer wireMockServer) {
        this.mockMvc = mockMvc;
        this.wireMockServer = wireMockServer;
    }

    @Test
	void shouldShowLandingPageWhenLandingPageIsCalled() throws Exception {
		mockMvc.perform(get("/")).andExpect(status().isOk());
	}
	@Test
	void shouldShowDetailsWhenServiceIsCalledWithBarcode() throws Exception {
		wireMockServer.start();
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.set("HX-Request", "true");
		mockMvc.perform(get("/info")
						.param("barcode", "123")
						.headers(httpHeaders)
				).andExpect(status().isOk());
		wireMockServer.stop();
	}
}
