package com.aremy.simplyREST;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ChoiceBox;

import java.net.URL;
import java.util.ResourceBundle;

public class HttpMethodChoiceController {

    @FXML
    private ChoiceBox httpMethodChoice;

    public void initialize(URL url, ResourceBundle rb) {
    }

    public void setData(){

        httpMethodChoice.getItems().clear();

        httpMethodChoice.getItems().addAll(
                "GET",
                "POST",
                "PUT",
                "DELETE");
    }

}
