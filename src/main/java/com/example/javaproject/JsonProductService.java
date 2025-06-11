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

/**
 * Serwis zarządzający listą kategorii i produktów.
 * Ładuje JSON raz przy pierwszym wywołaniu, a potem trzyma wynik w pamięci.
 * saveAllCategories() nadpisuje zarówno plik, jak i cache.
 */
public class JsonProductService {

    // trzymamy jednorazowo wczytane dane z jsona
    private static List<Categories> categoriesCache;

    // Ścieżka do pliku JSON w resources
    private static final Path JSON_PATH = Paths.get(
            "src","main","resources",
            "com","example","javaproject",
            "products.json"
    );

    /**
     * Zwraca listę kategorii: jeśli cache jest puste, najpierw ładuje z pliku.
     */
    // synchronized - tylko jeden watek moze wejsc do tej metody
    public static synchronized List<Categories> getAllCategories() {
        if (categoriesCache == null) {
            try (var reader = Files.newBufferedReader(JSON_PATH)) {
                Type listType = new TypeToken<List<Categories>>(){}.getType();
                categoriesCache = new Gson().fromJson(reader, listType);
                // Upewnij się, że każde Products ma initialQuantity ustawione:
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

    /**
     * Nadpisuje plik JSON i cache nową listą kategorii.
     */
    public static synchronized void saveAllCategories(List<Categories> newCategories) {
        // 1) Serializacja do JSON
        String json = new GsonBuilder()
                .setPrettyPrinting()
                .create()
                .toJson(newCategories);

        // 2) Zapis na dysk
        try {
            Files.writeString(JSON_PATH, json);
        } catch (Exception e) {
            throw new RuntimeException("Nie udało się zapisać " + JSON_PATH + ":\n" + e.getMessage(), e);
        }

        // 3) Nadpisanie cache tak, by od teraz operować na tej nowej liście
        categoriesCache = newCategories;
    }
}