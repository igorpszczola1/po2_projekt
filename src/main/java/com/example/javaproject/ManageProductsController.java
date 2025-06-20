package com.example.javaproject;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ManageProductsController {

    @FXML private TableView<Products> productsTable;
    @FXML private TableColumn<Products, String> categoryCol;
    @FXML private TableColumn<Products, String> nameCol;
    @FXML private TableColumn<Products, String> unitCol;

    private final ObservableList<Products> tableData = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        productsTable.setEditable(true);

        categoryCol.setCellValueFactory(new PropertyValueFactory<>("category"));
        categoryCol.setCellFactory(TextFieldTableCell.forTableColumn());
        categoryCol.setOnEditCommit(e -> {
            Products p = e.getRowValue();
            p.setCategory(e.getNewValue());
            syncToCache();
        });

        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        nameCol.setCellFactory(TextFieldTableCell.forTableColumn());
        nameCol.setOnEditCommit(e -> {
            Products p = e.getRowValue();
            p.setName(e.getNewValue());
            syncToCache();
        });

        unitCol.setCellValueFactory(new PropertyValueFactory<>("unit"));
        unitCol.setCellFactory(TextFieldTableCell.forTableColumn());
        unitCol.setOnEditCommit(e -> {
            Products p = e.getRowValue();
            p.setUnit(e.getNewValue());
            syncToCache();
        });

        loadFromJson();
        productsTable.setItems(tableData);
    }

    private void loadFromJson() {
        try {
            List<Categories> cats = JsonProductService.getAllCategories();
            tableData.clear();
            for (Categories cat : cats) {
                for (Products p : cat.getProducts()) {
                    p.setCategory(cat.getCategory());
                    tableData.add(p);
                }
            }
        } catch (Exception ex) {
            showError("Błąd wczytywania z JSON:\n" + ex.getMessage());
        }
    }

    @FXML
    private void onAdd() {
        Products np = new Products();
        np.setCategory("Uncategorized");
        np.setName("Nowy produkt");
        np.setUnit("pcs");

        tableData.add(np);
        List<Categories> cats = JsonProductService.getAllCategories();
        Categories cat = cats.stream()
                .filter(c -> c.getCategory().equals(np.getCategory()))
                .findFirst()
                .orElse(null);
        if (cat != null) {
            cat.getProducts().add(np);
        } else {
            List<Products> list = new ArrayList<>();
            list.add(np);
            cats.add(new Categories(np.getCategory(), list));
        }
    }

    @FXML
    private void onDelete() {
        Products sel = productsTable.getSelectionModel().getSelectedItem();
        if (sel != null) {
            tableData.remove(sel);
            JsonProductService.getAllCategories().forEach(cat ->
                    cat.getProducts().removeIf(p -> p.getName().equals(sel.getName()))
            );
        } else {
            showError("Proszę wybrać produkt do usunięcia.");
        }
    }

    @FXML
    private void onSave() {
        try {
            Map<String, List<Products>> grouped = tableData.stream()
                    .collect(Collectors.groupingBy(Products::getCategory));
            List<Categories> toSave = grouped.entrySet().stream()
                    .map(e -> new Categories(e.getKey(), e.getValue()))
                    .collect(Collectors.toList());

            JsonProductService.saveAllCategories(toSave);
            showInfo("Zapisano zmiany do JSON.");
        } catch (Exception ex) {
            showError("Błąd przy zapisie do JSON:\n" + ex.getMessage());
        }
    }

    @FXML
    private void onEdit() {
        int idx = productsTable.getSelectionModel().getSelectedIndex();
        if (idx >= 0) {
            productsTable.edit(idx, nameCol);
        } else {
            showError("Proszę wybrać produkt do edycji.");
        }
    }

    private void syncToCache() {
        List<Categories> cats = JsonProductService.getAllCategories();
        cats.forEach(cat -> cat.getProducts().clear());
        for (Products p : tableData) {
            Categories cat = cats.stream()
                    .filter(c -> c.getCategory().equals(p.getCategory()))
                    .findFirst()
                    .orElse(null);
            if (cat != null) {
                cat.getProducts().add(p);
            } else {
                List<Products> list = new ArrayList<>();
                list.add(p);
                cats.add(new Categories(p.getCategory(), list));
            }
        }
    }

    private void showError(String msg) {
        Alert a = new Alert(Alert.AlertType.ERROR, msg, ButtonType.OK);
        a.setHeaderText("Błąd");
        a.showAndWait();
    }

    private void showInfo(String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION, msg, ButtonType.OK);
        a.setHeaderText("Informacja");
        a.showAndWait();
    }
}
