package com.javafx.currencyproject.utilities;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.javafx.currencyproject.entities.Currency;
import javafx.scene.image.Image;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DataSource {
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final String API_KEY = "67768f93ca1f5c0b58c333c7";

    public List<Currency> getDataList() throws IOException, InterruptedException {
        String url = String.format("https://v6.exchangerate-api.com/v6/%s/codes", API_KEY);
        HttpClient client = HttpClient.newHttpClient();

        HttpRequest request = HttpRequest.newBuilder().
                uri(URI.create(url)).
                build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(response.body());
        JsonNode array = root.get("supported_codes");

        List<List<String>> allCurrencies = mapper.convertValue(array, new TypeReference<List<List<String>>>() {
        });

        client.close();

        return allCurrencies.
                stream().
                map(e -> {
                    Currency c = new Currency(e.get(0), e.get(1));
                    if(c.getCode().equalsIgnoreCase("xcg")){
                        c.setIconCurrency(new Image(Objects.requireNonNull(getClass().
                                getResource("/images/xg.png")).toString()));
                    }else{
                        String lettersCode = c.getCode().substring(0,2);
                        c.setIconCurrency(new Image(Objects.requireNonNull(getClass().
                                        getResource("/images/" + lettersCode + ".png")).
                                        toString()));
                    }

                    return c;
                }).
                collect(Collectors.toList());
    }

    public static void copyImagesToProyect() throws IOException {
        Path origin = Paths.get("C:\\Users\\Walter\\Downloads\\country-flags-main\\country-flags-main\\png200px");
        Path destination = Paths.get("C:\\Users\\Walter\\Downloads\\javabitwisedemo\\currencyproject\\src\\main\\resources\\images");

        try(Stream<Path> stream = Files.list(origin)){
            stream.filter(Files::isRegularFile).forEach(file -> {
                Path destinationFile = destination.resolve(file.getFileName());
                try {
                    Files.copy(file, destinationFile, StandardCopyOption.REPLACE_EXISTING);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        }
    }

    public static CompletableFuture<Map<String,String>> currencyConversion(String sourceCode,
                                                 String destinyCode,
                                                 String amount){
        Map<String,String> result = new HashMap<>();
        String url = String.format("https://v6.exchangerate-api.com/v6/%s/pair/%s/%s/%s",
                                    API_KEY,
                                    sourceCode,
                                    destinyCode,
                                    amount);
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder().
                                uri(URI.create(url)).
                                GET().
                                build();
        CompletableFuture<Map<String,String>> cfr = client.
                sendAsync(request, HttpResponse.BodyHandlers.ofString()).
                thenApply(HttpResponse::body).
                thenApply(body -> {
                    ObjectMapper objectMapper = new ObjectMapper();
                    try {
                        JsonNode jsonNode = objectMapper.readTree(body);
                        result.put("conversion_rate",jsonNode.path("conversion_rate").asText());
                        result.put("conversion_result",jsonNode.path("conversion_result").asText());
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }
                    return result;
                }).exceptionally(ex -> {
                    System.out.println("Error: " + ex.getMessage());
                    return null;
                });

        return cfr;
    }





}
