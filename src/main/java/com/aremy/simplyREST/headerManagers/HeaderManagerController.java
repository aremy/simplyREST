package com.aremy.simplyREST.headerManagers;

import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class HeaderManagerController {
    @FXML protected Stage dialogStage;
    @FXML
    private TextArea headerField;

    private final Logger slf4jLogger = LoggerFactory.getLogger(HeaderManagerController.class);

    public void setHeaderField(TextArea headerField) {
        this.headerField = headerField;
    }

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    public void setHeaderForm(String inputHeader) {
        if (headerField != null) {
            String header = headerField.getText();
            header += (header.isEmpty()?"":"\n") + inputHeader;
            headerField.setText(header);
            //slf4jLogger.error("Header field is not empty but cannot be set");
        } else {
            slf4jLogger.error("Header field is empty");
        }
    }

}
