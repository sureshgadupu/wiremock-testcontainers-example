package dev.fullstackcode.currencyexchange;

import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Configuration
public class TestConfig {

    @Bean
    public TestRestTemplate getTestRestTemplate() {
        List<HttpMessageConverter<?>> messageConverters = new ArrayList<HttpMessageConverter<?>>();

        //Add the Jackson Message converter
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();

        // Note: here we are making this converter to process any kind of response,
        // not only application/*json, which is the default behaviour
        converter.setSupportedMediaTypes(Collections.singletonList(MediaType.ALL));
        messageConverters.add(converter);
        TestRestTemplate restTemplate =new TestRestTemplate();
       // restTemplate.s
        // restTemplate.setMessageConverters(messageConverters);
//        restTemplate.
        return restTemplate;
    }
}
