package com.aremy.simplyREST.headerManagers;

import com.aremy.simplyREST.objects.PropertiesManager;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PropertiesController extends HeaderManagerController {

    @FXML TextField proxyHost;
    @FXML TextField proxyPort;
    @FXML TextField proxyLogin;
    @FXML TextField proxyPassword;

    private final Logger slf4jLogger = LoggerFactory.getLogger(PropertiesController.class);

    @FXML
    public void initialize() {
        PropertiesManager propertiesManager = PropertiesManager.instance();
        proxyHost.setText(propertiesManager.proxyHost);
        proxyPort.setText(propertiesManager.proxyPort);
        proxyLogin.setText(propertiesManager.proxyLogin);
        proxyPassword.setText(propertiesManager.proxyPassword);
    }

    @FXML
    public void saveProxyConfiguration() {
        try {
            String port = proxyPort.getText();
            if (!"".equals(port))
                Short.valueOf(port);
        } catch(NumberFormatException e){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Invalid port value");
            alert.setContentText("Please set a numeric value in the Port field.");

            alert.showAndWait();
            return;
        }
        PropertiesManager.proxyHost = proxyHost.getText();
        PropertiesManager.proxyPort = proxyPort.getText();
        PropertiesManager.proxyLogin = proxyLogin.getText();
        PropertiesManager.proxyPassword = proxyPassword.getText();
        PropertiesManager.save();

        dialogStage.close();
    }
}
