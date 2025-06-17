package com.example.javaproject;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.ListCell;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.util.*;
import java.util.stream.Collectors;

public class ProductsController {

    @FXML private ComboBox<String> categoryFilterBox;
    @FXML private ListView<Products> productsListView;

    private final ObservableList<Products> allProducts = FXCollections.observableArrayList();
    private final ObservableList<Products> filteredProducts = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        loadProducts();

        var categories = JsonProductService.getAllCategories().stream()
                .map(Categories::getCategory)
                .collect(Collectors.toCollection(TreeSet::new));

        List<String> comboOptions = new ArrayList<>();
        comboOptions.add("All Categories");
        comboOptions.addAll(categories);
        categoryFilterBox.setItems(FXCollections.observableArrayList(comboOptions));
        categoryFilterBox.getSelectionModel().selectFirst();

        filteredProducts.setAll(allProducts);
        productsListView.setItems(filteredProducts);
        setupCellFactory();

        categoryFilterBox.getSelectionModel().selectedItemProperty()
                .addListener((obs, oldVal, newVal) -> applyFilter(newVal));
    }

    private void loadProducts() {
        allProducts.clear();
        JsonProductService.getAllCategories().forEach(cat -> {
            cat.getProducts().forEach(allProducts::add);
        });
    }

    private void setupCellFactory() {
        productsListView.setCellFactory(lv -> new ListCell<>() {
            private final HBox hbox = new HBox(10);
            private final VBox vbox = new VBox(2);
            private final Text nameText = new Text();
            private final Text unitText = new Text();
            private final Button addButton = new Button("+");

            {
                nameText.getStyleClass().add("product-name");
                unitText.getStyleClass().add("product-quantity");
                addButton.getStyleClass().add("add-row-button");
                addButton.setFocusTraversable(false);

                vbox.getChildren().addAll(nameText, unitText);
                HBox.setHgrow(vbox, Priority.ALWAYS);
                hbox.getChildren().addAll(vbox, addButton);
            }

            @Override
            protected void updateItem(Products item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                } else {
                    nameText.setText(item.getName());
                    unitText.setText("Unit: " + item.getUnit());

                    addButton.setOnAction(evt -> handleAddToCart(item));
                    setGraphic(hbox);
                }
            }
        });
    }

    private void applyFilter(String category) {
        if (category == null || category.equals("All Categories")) {
            filteredProducts.setAll(allProducts);
        } else {
            filteredProducts.setAll(
                    allProducts.stream()
                            .filter(p -> p.getCategory().equals(category))
                            .collect(Collectors.toList())
            );
        }
    }

    private void handleAddToCart(Products product) {
        TextInputDialog dialog = new TextInputDialog("1");
        dialog.setTitle("Add to cart");
        dialog.setHeaderText("Add quantity for " + product.getName());
        dialog.setContentText("Enter quantity (" + product.getUnit() + "):");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(input -> {
            try {
                double quantity = Double.parseDouble(input);
                if (quantity <= 0) {
                    showError("Quantity must be greater than zero.");
                    return;
                }

                CartItem item = new CartItem(product.getName(), quantity, product.getUnit());
                CartService.getInstance().addToCart(item);
                showInfo("Added to cart.");
            } catch (NumberFormatException e) {
                showError("Invalid number format.");
            }
        });
    }

    private void showError(String msg) {
        Alert a = new Alert(Alert.AlertType.ERROR, msg, ButtonType.OK);
        a.setHeaderText("Error");
        a.showAndWait();
    }

    private void showInfo(String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION, msg, ButtonType.OK);
        a.setHeaderText("Info");
        a.showAndWait();
    }

    public void refreshProducts() {
        loadProducts();
        applyFilter(categoryFilterBox.getSelectionModel().getSelectedItem());
    }
}