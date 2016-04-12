package com.aremy.simplyREST;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/sample.fxml"));
        Parent root = loader.load();

        //Parent root = FXMLLoader.load(getClass().getResource("/sample.fxml"));

//        HttpMethodChoiceController httpMethodChoiceController = loader.getController();


        primaryStage.setTitle("simplyREST");
        primaryStage.setScene(new Scene(root, 800, 600));
        primaryStage.show();

 //       httpMethodChoiceController.setData();

    }


    public static void main(String[] args) {
        launch(args);
    }
}
