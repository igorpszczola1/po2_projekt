package com.example.javaproject;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class ShoppingList extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/com/example/javaproject/MainScene.fxml"));
        Scene scene = new Scene(root, 700, 900);
        setStage(stage);
        stage.setScene(scene);
        stage.show();
    }

    private void setStage(Stage stage) {
        stage.setTitle("Shopping List");
        stage.setWidth(700);
        stage.setHeight(900);
        stage.setResizable(false);
    }

    public static void main(String[] args) {
        launch(args);
    }
}