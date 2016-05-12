package com.aremy.simplyREST.headerManagers;

import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputControl;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class HeaderManagerController {
    @FXML protected Stage dialogStage;

    private TextArea headerField;

    private TextField urlField;

    private TextArea bodyField;


    private final Logger slf4jLogger = LoggerFactory.getLogger(HeaderManagerController.class);

    public void setHeaderField(TextArea headerField) {
        this.headerField = headerField;
    }
    public void setUrlField(TextField urlField) {
        this.urlField = urlField;
    }
    public void setBodyField(TextArea bodyField) {
        this.bodyField = bodyField;
    }

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    public void appendHeaderForm(String inputHeader) {
        setTextInputControl(headerField, inputHeader, true);
    }

    public void setHeaderForm(String inputHeader) {
        setTextInputControl(headerField, inputHeader, false);
    }

    public void setBodyForm(String inputBody) {
        setTextInputControl(bodyField, inputBody, false);
    }

    public void setUrlForm(String inputUrl) {
        setTextInputControl(urlField, inputUrl, false);
    }

    public void setTextInputControl(TextInputControl textInputControl, String input, boolean append) {
        if (textInputControl != null) {
            String formText;
            if (append) {
                formText = textInputControl.getText();
                formText += (formText.isEmpty() ? "" : "\n") + input;
            } else {
                formText = input;
            }
            textInputControl.setText(formText);
        } else {
            slf4jLogger.error("Target form element does not exist " + TextInputControl.class);
        }
    }

}
