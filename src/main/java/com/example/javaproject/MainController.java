package com.example.javaproject;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;

import java.io.IOException;

public class MainController {

    @FXML
    private StackPane contentPane;

    private AnchorPane productsView;
    private AnchorPane cartView;
    private AnchorPane manageView;               // ← nowy widok
    private ProductsController productsController;
    private CartController cartController;
    private ManageProductsController manageController;  // ← kontroler do zarządzania

    @FXML
    public void initialize() {
        try {
            // 1) Załaduj ProductsView
            FXMLLoader productsLoader = new FXMLLoader(getClass().getResource("ProductsView.fxml"));
            productsView = productsLoader.load();
            productsController = productsLoader.getController();

            // 2) Załaduj CartView
            FXMLLoader cartLoader = new FXMLLoader(getClass().getResource("CartView.fxml"));
            cartView = cartLoader.load();
            cartController = cartLoader.getController();

            // ===== 3) Załaduj ManageProductsView =====
            FXMLLoader manageLoader = new FXMLLoader(getClass().getResource("ManageProductView.fxml"));
            manageView = manageLoader.load();
            manageController = manageLoader.getController();

        } catch (IOException e) {
            e.printStackTrace();
        }

        // Na start pokazujemy zakładkę Products
        if (productsView != null) {
            contentPane.getChildren().setAll(productsView);
        }
    }

    @FXML
    private void showProducts() {
        if (productsView != null && productsController != null) {
            productsController.refreshProducts();
            contentPane.getChildren().setAll(productsView);
        }
    }

    @FXML
    private void showCart() {
        if (cartView != null && cartController != null) {
            cartController.refreshCart();
            contentPane.getChildren().setAll(cartView);
        }
    }

    @FXML
    private void showManage() {
        if (manageView != null && manageController != null) {
            contentPane.getChildren().setAll(manageView);
        }
    }
}