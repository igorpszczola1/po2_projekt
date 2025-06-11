package com.example.javaproject;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;

import java.util.*;
import java.util.stream.Collectors;

public class ProductsController {

    @FXML private ComboBox<String> categoryFilterBox;
    @FXML private ListView<ProductRow> productsListView;

    private final Map<String, Double> defaultDeltas = Map.of(
            "Milk", 1.0,
            "Yogurt", 1.0,
            "Cheddar Cheese", 0.25,
            "Apples", 0.5,
            "Bananas", 0.5,
            "Tomatoes", 0.5,
            "Rice", 1.0,
            "Olive Oil", 1.0,
            "Bread", 1.0
    );


    // ObservableList - informuje powiązane widoki np. ListView o kazdej zmianie zawartosci
    // nie trzeba wywolywac refresh, automatycznie sie odswiezy
    private final ObservableList<ProductRow> allProductRows = FXCollections.observableArrayList();
    private final ObservableList<ProductRow> filteredRows = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        buildAllProductRows();

        var cats = JsonProductService.getAllCategories().stream()
                .map(Categories::getCategory)
                .collect(Collectors.toCollection(TreeSet::new));
        var combo = new ArrayList<String>();
        combo.add("All Categories");
        combo.addAll(cats);
        categoryFilterBox.setItems(FXCollections.observableArrayList(combo));
        categoryFilterBox.getSelectionModel().selectFirst();

        filteredRows.setAll(allProductRows);
        productsListView.setItems(filteredRows);
        setupCellFactory();

        categoryFilterBox.getSelectionModel().selectedItemProperty()
                .addListener((obs, o, n) -> applyFilter(n));
    }

    private void buildAllProductRows() {
        allProductRows.clear();
        JsonProductService.getAllCategories().forEach(cat -> {
            String catName = cat.getCategory();
            cat.getProducts().forEach(p -> {
                double cur = p.getQuantity();
                double init = p.getInitialQuantity();
                allProductRows.add(new ProductRow(catName, p.getName(), cur, init, p.getUnit()));
            });
        });
    }

    private void setupCellFactory() {
        productsListView.setCellFactory(lv -> new ListCell<>() {
            HBox hbox = new HBox(10);
            Circle dot = new Circle(6);
            VBox vboxText = new VBox(2);
            Text nameText = new Text();
            Text quantityText = new Text();
            Button addButton = new Button("+");

            {
                dot.getStyleClass().add("product-dot");
                nameText.getStyleClass().add("product-name");
                quantityText.getStyleClass().add("product-quantity");
                addButton.getStyleClass().add("add-row-button");
                addButton.setFocusTraversable(false);

                vboxText.getChildren().add(nameText);
                HBox.setHgrow(vboxText, Priority.ALWAYS);
                hbox.getChildren().addAll(dot, vboxText, quantityText, addButton);
            }

            @Override
            protected void updateItem(ProductRow item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item==null) {
                    setGraphic(null);
                } else {
                    nameText.setText(item.getName());
                    quantityText.setText(String.format("%.2f %s",
                            item.getCurrentQuantity(), item.getUnit()));

                    double cur = item.getCurrentQuantity();
                    double init = item.getInitialQuantity();
                    if (cur<=0)
                        dot.setFill(Paint.valueOf("#E53935"));
                    else if (cur<init/2)
                        dot.setFill(Paint.valueOf("#FF8F00"));
                    else
                        dot.setFill(Paint.valueOf("#00C853"));

                    addButton.setOnAction(evt -> handleAddToCart(item));
                    setGraphic(hbox);
                }
            }
        });
    }

    private void applyFilter(String category) {
        if (category==null || category.equals("All Categories")) {
            filteredRows.setAll(allProductRows);
        } else {
            filteredRows.setAll(
                    allProductRows.stream()
                            .filter(r->r.getCategory().equals(category))
                            .collect(Collectors.toList())
            );
        }
    }

    private void handleAddToCart(ProductRow item) {
        Products p = findProductInInventory(item.getName());
        if (p==null)
            return;

        double cur = p.getQuantity();
        if (cur<=0)
            return;

        double delta = getDelta(item);
        double take  = Math.min(cur, delta);
        p.setQuantity(cur - take);

        CartService.getInstance().addToCart(item.getName(), take);
        updateAllProductRows(item.getName(), p.getQuantity());
    }

    private double getDelta(ProductRow item) {
        return defaultDeltas.getOrDefault(
                item.getName(),
                switch(item.getUnit().toLowerCase()) {
                    case "pcs" -> 1.0;
                    case "kg","l" -> 1.0;
                    default -> 1.0;
                }
        );
    }

    private Products findProductInInventory(String name) {
        return JsonProductService.getAllCategories().stream()
                .flatMap(c->c.getProducts().stream())
                .filter(p->p.getName().equals(name))
                .findFirst()
                .orElse(null);
    }


    private void updateAllProductRows(String name, double newQty) {
        for (int i=0;i<allProductRows.size();i++) {
            var row = allProductRows.get(i);
            if (row.getName().equals(name)) {
                allProductRows.set(i, new ProductRow(
                        row.getCategory(),
                        row.getName(),
                        newQty,
                        row.getInitialQuantity(),
                        row.getUnit()
                ));
                break;
            }
        }
        applyFilter(categoryFilterBox.getSelectionModel().getSelectedItem());
    }

    public void refreshProducts() {
        buildAllProductRows();
        applyFilter(categoryFilterBox.getSelectionModel().getSelectedItem());
    }
}