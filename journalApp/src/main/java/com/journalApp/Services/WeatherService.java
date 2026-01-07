package com.journalApp.Services;

import com.journalApp.ApiResponse.WeatherResponse;
import com.journalApp.Cache.AppCache;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class WeatherService {

    @Value("${weather_api_key}")
    private String apiKey;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private AppCache appCache;

    public WeatherResponse getWeather(String city) {

        String template = appCache.get("weather_api");

//        System.out.println(template);

        if (template == null) {
            throw new RuntimeException(
                    "weather_api not found in AppCache"
            );
        }

        String finalApi = template
                .replace("<CITY>", city)
                .replace("<API_KEY>", apiKey);

        ResponseEntity<WeatherResponse> response =
                restTemplate.exchange(
                        finalApi,
                        HttpMethod.GET,
                        null,
                        WeatherResponse.class
                );

        return response.getBody();
    }
}
