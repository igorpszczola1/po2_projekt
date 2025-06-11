package com.example.javaproject;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;


public class JsonProductService {

    // trzymamy jednorazowo wczytane dane z jsona
    private static List<Categories> categoriesCache;

    private static final Path JSON_PATH = Paths.get(
            "src","main","resources",
            "com","example","javaproject",
            "products.json"
    );

    // synchronized - tylko jeden watek moze wejsc do tej metody
    public static synchronized List<Categories> getAllCategories() {
        if (categoriesCache == null) {
            try (var reader = Files.newBufferedReader(JSON_PATH)) {
                Type listType = new TypeToken<List<Categories>>(){}.getType();
                categoriesCache = new Gson().fromJson(reader, listType);
                for (Categories cat : categoriesCache) {
                    for (Products p : cat.getProducts()) {
                        p.setInitialQuantity(p.getQuantity());
                    }
                }
            } catch (Exception e) {
                throw new RuntimeException("Nie udało się wczytać " + JSON_PATH + ":\n" + e.getMessage(), e);
            }
        }
        return categoriesCache;
    }


    public static synchronized void saveAllCategories(List<Categories> newCategories) {
        String json = new GsonBuilder()
                .setPrettyPrinting()
                .create()
                .toJson(newCategories);

        try {
            Files.writeString(JSON_PATH, json);
        } catch (Exception e) {
            throw new RuntimeException("Nie udało się zapisać " + JSON_PATH + ":\n" + e.getMessage(), e);
        }

        categoriesCache = newCategories;
    }
}