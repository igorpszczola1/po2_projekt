package com.example.javaproject;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.ListCell;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.text.Text;

import java.util.List;

public class CartController {

    @FXML
    private ListView<CartItem> cartListView;

    @FXML
    private Button saveButton;

    private final ObservableList<CartItem> cartRows = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        setupCellFactory();
        refreshCart();

        saveButton.setOnAction(e -> {
            try {
                CartService.getInstance().saveCartToFile();
                showInfo("Cart has been saved to file.");
            } catch (Exception ex) {
                showError("Failed to save cart:\n" + ex.getMessage());
            }
        });
    }

    private void setupCellFactory() {
        cartListView.setCellFactory(lv -> new ListCell<>() {
            private final HBox hbox = new HBox(10);
            private final Text nameText = new Text();
            private final Text qtyText = new Text();
            private final Button removeButton = new Button("Remove");
            private final Region spacer = new Region();

            {
                nameText.getStyleClass().add("product-name");
                qtyText.getStyleClass().add("product-quantity");
                removeButton.getStyleClass().add("remove-row-button");
                removeButton.setFocusTraversable(false);
                HBox.setHgrow(spacer, Priority.ALWAYS);
                hbox.getChildren().addAll(nameText, spacer, qtyText, removeButton);
            }

            @Override
            protected void updateItem(CartItem item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                } else {
                    nameText.setText(item.getName());
                    qtyText.setText(String.format("%.2f %s", item.getQuantity(), item.getUnit()));
                    removeButton.setOnAction(evt -> handleRemoveFromCart(item));
                    setGraphic(hbox);
                }
            }
        });
    }

    private void handleRemoveFromCart(CartItem item) {
        CartService.getInstance().removeFromCart(item.getName());
        CartService.getInstance().saveCartToFile();
        refreshCart();
    }

    public void refreshCart() {
        List<CartItem> cartItems = CartService.getInstance().getCartItems();
        cartRows.setAll(cartItems);
        cartListView.setItems(cartRows);
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
}