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
    private AnchorPane manageView;
    private ProductsController productsController;
    private CartController cartController;
    private ManageProductsController manageController;

    @FXML
    public void initialize() {
        try {
            FXMLLoader productsLoader = new FXMLLoader(getClass().getResource("ProductsView.fxml"));
            productsView = productsLoader.load();
            productsController = productsLoader.getController();

            FXMLLoader cartLoader = new FXMLLoader(getClass().getResource("CartView.fxml"));
            cartView = cartLoader.load();
            cartController = cartLoader.getController();
            cartController.refreshCart();  // <-- TU DODALIŚMY

            FXMLLoader manageLoader = new FXMLLoader(getClass().getResource("ManageProductView.fxml"));
            manageView = manageLoader.load();
            manageController = manageLoader.getController();

        } catch (IOException e) {
            e.printStackTrace();
        }

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