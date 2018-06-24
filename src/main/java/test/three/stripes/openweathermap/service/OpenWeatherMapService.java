package test.three.stripes.openweathermap.service;

import test.three.stripes.openweathermap.data.GeographicCoordinates;
import test.three.stripes.openweathermap.model.CurrentWeatherResponse;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;

public class OpenWeatherMapService extends OpenWeatherMapSpecification{

    private CurrentWeatherResponse getCurrentWeather(Map<String, String> queryParams) {
        return given()
                .spec(this.requestSpecification())
                .queryParams(queryParams)
                .when()
                .get("weather")
                .then()
                .statusCode(200)
                .extract().body().as(CurrentWeatherResponse.class);
    }


    public static void main(String[] args) {
        OpenWeatherMapService openWeatherMapService = new OpenWeatherMapService();
        openWeatherMapService.getWeatherByCityName("Rio de Janeiro");
        openWeatherMapService.getWeatherByCityId(2172797);
        openWeatherMapService.getWeatherByGeographicCoordinates(new GeographicCoordinates(35, 139));
        openWeatherMapService.getWeatherByZipCode(94040, "us");
        openWeatherMapService.getWeatherFromCitiesInARectangleZone(new GeographicCoordinates(12,32,15,37,10));
        openWeatherMapService.getWeatherFromCitiesInACycleZone(new GeographicCoordinates(55.5,37.5),10);
        openWeatherMapService.getWeatherFromCitiesByIds("524901,703448,2643743");
    }


    public CurrentWeatherResponse getWeatherByCityName(String city) {
        Map<String, String> params = new HashMap<>();
        params.put("q", city);
        return getCurrentWeather(params);
    }

    public CurrentWeatherResponse getWeatherByCityId(int cityId) {
        Map<String, String> params = new HashMap<>();
        params.put("id", String.valueOf(cityId));
        return getCurrentWeather(params);
    }

    public CurrentWeatherResponse getWeatherByGeographicCoordinates(GeographicCoordinates latLon) {

        Map<String, String> params = new HashMap<>();
        params.put("lat", latLon.getLat());
        params.put("lon", latLon.getLon());
        return getCurrentWeather(params);
    }

    public CurrentWeatherResponse getWeatherByZipCode(int zipCode, String countryCode) {

        Map<String, String> params = new HashMap<>();
        params.put("zip", zipCode+","+countryCode);
        return getCurrentWeather(params);
    }

    public List<CurrentWeatherResponse> getWeatherFromCitiesInARectangleZone(GeographicCoordinates coordinates) {

        Map<String, String> params = new HashMap<>();
        params.put("bbox", coordinates.toString());

        return Arrays.asList(given()
                .spec(this.requestSpecification())
                .queryParams(params)
                .when()
                .get("box/city")
                .then()
                .statusCode(200)
                .extract().body()
                .jsonPath()
                .getObject("list", CurrentWeatherResponse[].class));
    }

    public List<CurrentWeatherResponse> getWeatherFromCitiesInACycleZone(GeographicCoordinates latLon, int numberOfCities) {

        Map<String, String> params = new HashMap<>();
        params.put("lat", latLon.getLat());
        params.put("lon", latLon.getLon());
        params.put("cnt", String.valueOf(numberOfCities));
        params.put("lang", "en");

        return Arrays.asList(given()
                .spec(this.requestSpecification())
                .queryParams(params)
                .when()
                .get("find")
                .then()
                .statusCode(200)
                .extract().body()
                .jsonPath()
                .getObject("list", CurrentWeatherResponse[].class));
    }


    public List<CurrentWeatherResponse> getWeatherFromCitiesByIds(String idsWithComma) {

        Map<String, String> params = new HashMap<>();
        params.put("id", idsWithComma);
        params.put("units", "metric");

        return Arrays.asList(given()
                .spec(this.requestSpecification())
                .queryParams(params)
                .when()
                .get("group")
                .then()
                .statusCode(200)
                .extract().body()
                .jsonPath()
                .getObject("list", CurrentWeatherResponse[].class));
    }

}
