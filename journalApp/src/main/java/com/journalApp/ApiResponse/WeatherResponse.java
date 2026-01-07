package com.journalApp.ApiResponse;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class WeatherResponse {

    private Current current;






   @Data
    public class Current {
        @JsonProperty("observarion_time")
        private int temperature;

        @JsonProperty("weather_descriptions")
        private List<String> weatherDescriptions;
        private int feelslike;



    }






}
