package dev.fullstackcode.currencyexchange.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestOperations;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/currencyCodeVersion")
@Slf4j
public class CurrencyConverterController {

    @Autowired
    private RestOperations restTemplate;

    @Value("${currencyconverter.url}")
    private String currencyConverterUrl;


    @Value("${country.url}")
    private String countryUrl;

    @Value("${json.mock.api}")
    private String jsonApi;



    @GetMapping( "/currencyCode/{currencyCode}")
    public String convertByCurrencyCode(@PathVariable String currencyCode) {

        try {
            String currencyConversionUrl = String.format(currencyConverterUrl,currencyCode.toLowerCase());
            Object obj = restTemplate.getForObject(currencyConversionUrl,Object.class);

            if ( obj == null) {
                throw new RestClientException("no information found");
            }
            return obj.toString();
        } catch (HttpClientErrorException e) {
            throw new ResponseStatusException(e.getStatusCode(),e.getMessage());
        }

    }

    @GetMapping( "/country/{countryCode}")
    public String convertCurrencyByCountryCode(@PathVariable String countryCode) {

        String countryurl = String.format(countryUrl,countryCode.toLowerCase());
        Object obj = null;
        try {
            obj = restTemplate.getForObject(countryurl,Object.class);
        } catch (HttpClientErrorException e) {
            throw new ResponseStatusException(e.getStatusCode(),e.getMessage());
        }

        if ( obj == null) {
            throw new RestClientException("no information found");
        }
        int index = obj.toString().indexOf("currencies");
        int index2 = obj.toString().indexOf("idd");

        if (index == -1 || index2 == -1 ) {
            Map<String,String> responseMap = (Map<String,String>)obj;
            throw new ResponseStatusException(HttpStatus.valueOf(404),responseMap.get("message"));
        }
        String currency = obj.toString().substring(index,index2);
        Pattern p = Pattern.compile("([A-Z])\\w+=" );
        log.info("currencies :{}", currency);
        Matcher m = p.matcher(currency);
        String currencyCode = "";
        if (m.find()) {
            currencyCode = m.group().substring(0,m.group().length()-1);
        }
        if ("".equals(currencyCode)) {
            throw new ResponseStatusException(HttpStatus.valueOf(404),"CurrencyCode not found");
        }
        try {
            String currencyConversionUrl = String.format(currencyConverterUrl,currencyCode.toLowerCase());
            Object obj2 = restTemplate.getForObject(currencyConversionUrl,Object.class);

            if ( obj2 == null) {
                throw new RestClientException("no information found");
            }
            return obj2.toString();
        } catch (HttpClientErrorException e) {
            throw new ResponseStatusException(e.getStatusCode(),e.getMessage());
        }

    }


}
