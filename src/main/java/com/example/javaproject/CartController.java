package com.example.javaproject;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.text.Text;

import java.util.HashMap;
import java.util.Map;

public class CartController {

    @FXML
    private ListView<CartRow> cartListView;

    // Domyślne ilości „kroków” usuwania, analogicznie jak w ProductsController
    private final Map<String, Double> defaultDeltas = new HashMap<>() {{
        put("Milk", 1.0);
        put("Yogurt", 1.0);
        put("Cheddar Cheese", 0.25);
        put("Apples", 0.5);
        put("Bananas", 0.5);
        put("Tomatoes", 0.5);
        put("Rice", 1.0);
        put("Olive Oil", 1.0);
        put("Bread", 1.0);
    }};

    @FXML
    public void initialize() {
        // Na starcie ustawiamy pusty ObservableList i konfigurujemy cell factory
        cartListView.setItems(FXCollections.observableArrayList());
        setupCellFactory();
    }

    private void setupCellFactory() {
        cartListView.setCellFactory(lv -> new ListCell<>() {
            private final HBox hbox = new HBox(10);
            private final Text nameText = new Text();
            private final Text qtyText = new Text();
            private final Button removeButton = new Button("–");
            private final Region spacer = new Region(); // niewidoczny spacer

            {
                // Nadajemy styl tekstom i przyciskowi:
                nameText.getStyleClass().add("product-name");
                qtyText.getStyleClass().add("product-quantity");
                removeButton.getStyleClass().add("remove-row-button");
                removeButton.setFocusTraversable(false);

                // Ustawiamy, aby spacer rozpychał się na max szerokość:
                HBox.setHgrow(spacer, Priority.ALWAYS);

                // Kolejność: [ nameText ] [ spacer (Priority.ALWAYS) ] [ qtyText ] [ removeButton ]
                hbox.getChildren().addAll(nameText, spacer, qtyText, removeButton);
            }

            @Override
            protected void updateItem(CartRow item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setGraphic(null);
                    setText(null);
                } else {
                    // Ustawiamy tekst nazwy i tekst ilości:
                    nameText.setText(item.getName());
                    qtyText.setText(String.format("%.2f %s", item.getQuantity(), item.getUnit()));

                    // Obsługa kliknięcia przycisku „–”
                    removeButton.setOnAction(evt -> handleRemoveFromCart(item));

                    setGraphic(hbox);
                }
            }
        });
    }

    private void handleRemoveFromCart(CartRow item) {
        String productName = item.getName();
        // fallback na 1.0 zamiast 0.0
        Double delta = defaultDeltas.getOrDefault(productName, null);
        if (delta == null) {
            // prosta reguła: 1.0 dla większości jednostek
            String unit = item.getUnit().toLowerCase();
            delta = switch (unit) {
                case "pcs", "slices" -> 1.0;
                case "kg", "l"       -> 1.0;
                default              -> 1.0;
            };
        }
        // dalej jak zwykle
        if (delta <= 0.0) return;

        CartService.getInstance().removeFromCart(productName, delta);

        Products inv = findProductInInventory(productName);
        if (inv != null) {
            inv.setQuantity(inv.getQuantity() + delta);
        }
        refreshCart();
    }

    public void refreshCart() {
        Map<String, Double> cartContents = CartService.getInstance().getCartContents();
        Map<String, String> unitMap = buildUnitMap();

        ObservableList<CartRow> cartRows = FXCollections.observableArrayList();
        for (Map.Entry<String, Double> entry : cartContents.entrySet()) {
            String productName = entry.getKey();
            double qtyInCart = entry.getValue();
            String unit = unitMap.getOrDefault(productName, "");
            cartRows.add(new CartRow(productName, qtyInCart, unit));
        }
        cartListView.setItems(cartRows);
    }

    private Map<String, String> buildUnitMap() {
        Map<String, String> unitMap = new HashMap<>();
        for (Categories cat : JsonProductService.getAllCategories()) {
            for (Products p : cat.getProducts()) {
                unitMap.put(p.getName(), p.getUnit());
            }
        }
        return unitMap;
    }

    private Products findProductInInventory(String productName) {
        for (Categories cat : JsonProductService.getAllCategories()) {
            for (Products p : cat.getProducts()) {
                if (p.getName().equals(productName)) {
                    return p;
                }
            }
        }
        return null;
    }
}