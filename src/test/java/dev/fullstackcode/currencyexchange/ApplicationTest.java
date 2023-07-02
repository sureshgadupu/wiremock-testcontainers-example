package dev.fullstackcode.currencyexchange;

import com.github.tomakehurst.wiremock.client.WireMock;
import dev.fullstackcode.currencyexchange.controller.CurrencyConverterController;
import lombok.extern.slf4j.Slf4j;
import nl.thecheerfuldev.testcontainers.wiremock.WireMockContainer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.web.client.RestTemplate;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.MountableFile;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@Slf4j
class ApplicationTest {

	@Autowired
	private  TestRestTemplate testRestTemplate ;

    @LocalServerPort
    private int serverPort;
	@Container
	private static final WireMockContainer WIRE_MOCK_CONTAINER = new WireMockContainer("wiremock/wiremock:2.35.0-alpine")
																		.withCopyFileToContainer(MountableFile.forHostPath(new File("../test-wiremock-extension").getPath()), "/var/wiremock/extensions")
																		.withStubMappingForClasspathResource("stubs"); // loads all *.json files in resources/stubs/

    private static final String ROOT_URL = "http://localhost:";

    @Test
	void contextLoads() {
	}

	@Test
	public void testConvertByCurrencyCodeWhenConversionFoundWithGivenCurrencyCode() throws Exception {
		ResponseEntity<String> response = testRestTemplate.getForEntity( ROOT_URL+serverPort+"/currencyCodeVersion/currencyCode/{currencyCode}",String.class,"JPY");
		assertEquals(200,response.getStatusCode().value());
        assertEquals("{date=2023-05-19, jpy=138.544529}",response.getBody());
	}


	@Test
	public void testConvertByCurrencyCodeWhenConversionFoundWithGivenCurrencyCode2() throws Exception {
		ResponseEntity<String> response = testRestTemplate.getForEntity( ROOT_URL+serverPort+"/currencyCodeVersion/country/{countryCode}",String.class,"japan");
		assertEquals(200,response.getStatusCode().value());
		assertEquals("{date=2023-05-19, jpy=138.544529}",response.getBody());
	}


	@DynamicPropertySource
	public static void properties(DynamicPropertyRegistry registry) {
		System.out.println("url" + WIRE_MOCK_CONTAINER.getHttpUrl());
		registry.add("country.url",()-> String.format("%s/v3.1/name/%s",WIRE_MOCK_CONTAINER.getHttpUrl(),"%s"));
		registry.add("currencyconverter.url",()->String.format("%s/gh/fawazahmed0/currency-api@1/latest/currencies/usd/%s.json", WIRE_MOCK_CONTAINER.getHttpUrl(),"%s"));
	}

}




