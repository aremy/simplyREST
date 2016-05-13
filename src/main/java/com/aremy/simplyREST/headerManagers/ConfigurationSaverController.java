package com.aremy.simplyREST.headerManagers;

import com.aremy.simplyREST.generated.Savedpresets;
import com.aremy.simplyREST.objects.PresetsManager;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.util.Callback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class ConfigurationSaverController extends HeaderManagerController {

    @FXML private TextField presetLabel;
    @FXML private Button savePresetButton;

    private final Logger slf4jLogger = LoggerFactory.getLogger(ConfigurationSaverController.class);

    @FXML
    private void initialize() {
        checkEmptyLabel();
        //String method = methodField.getSelectionModel().getSelectedItem().toString();
        //presetLabel.setText(method + " " + urlField.getText());
    }

    @FXML
    private void savePreset() {
        slf4jLogger.info("Saving " + presetLabel.getText());
        PresetsManager presetsManager = PresetsManager.instance();
        List<Savedpresets.Session> savedSessions = presetsManager.sessionList;
        Savedpresets.Session session = new Savedpresets.Session();

        session.setName(presetLabel.getText());
        session.setUrl(urlField.getText());
        session.setHeaders(headerField.getText());
        session.setBody(bodyField.getText());
        session.setMethod(methodField.getValue().toString());

        savedSessions.add(session);
        presetsManager.save();

        dialogStage.close();
    }

    @FXML
    private void closeDialog() {
        dialogStage.close();
    }

    @FXML
    private void checkEmptyLabel() {
        if (presetLabel.getText().isEmpty()) {
            savePresetButton.setDisable(true);
        } else {
            savePresetButton.setDisable(false);
        }
    }

}
