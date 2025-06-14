package com.example.javaproject;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.lang.reflect.Type;
import java.util.List;

public class JsonProductService {

    private static List<Categories> categoriesCache;

    private static final Path JSON_PATH = Paths.get(
            "src", "main", "resources",
            "com", "example", "javaproject",
            "products.json"
    );

    public static synchronized List<Categories> getAllCategories() {
        if (categoriesCache == null) {
            try (Reader reader = Files.newBufferedReader(JSON_PATH)) {
                Type listType = new TypeToken<List<Categories>>(){}.getType();
                categoriesCache = new Gson().fromJson(reader, listType);
            } catch (Exception e) {
                throw new RuntimeException("Nie udało się wczytać pliku: " + e.getMessage(), e);
            }
        }
        return categoriesCache;
    }

    public static synchronized void saveAllCategories(List<Categories> newCategories) {
        try {
            String json = new GsonBuilder().setPrettyPrinting().create().toJson(newCategories);
            Files.writeString(JSON_PATH, json);
            categoriesCache = newCategories;
        } catch (Exception e) {
            throw new RuntimeException("Nie udało się zapisać pliku: " + e.getMessage(), e);
        }
    }
}
