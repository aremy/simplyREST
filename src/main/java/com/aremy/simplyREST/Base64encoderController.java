package com.aremy.simplyREST;

import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Base64encoderController {
    @FXML Stage dialogStage;
    @FXML TextField usernameToEncode;
    @FXML TextField passwordToEncode;
    @FXML TextField base64encodedstringToDecode;
    @FXML private TextArea headerField;

    private final Logger slf4jLogger = LoggerFactory.getLogger(Base64encoderController.class);

    public void base64encode() {
        String stringToEncode = usernameToEncode.getText() + ":" + passwordToEncode.getText();
        byte[] encodedBytes = Base64.encodeBase64(stringToEncode.getBytes());
        base64encodedstringToDecode.setText(new String(encodedBytes));
    }

    public void base64decode() {
        String stringToDecode = base64encodedstringToDecode.getText();
        byte[] decodedBytes = Base64.decodeBase64(stringToDecode);
        String[] usernamePassword = new String(decodedBytes).split(":");
        usernameToEncode.setText(usernamePassword[0]);
        if (usernamePassword.length >= 2) {
            passwordToEncode.setText(usernamePassword[1]);
        }
    }

    public void setBase64encodedstring() {
        if (headerField != null) {
            String header = headerField.getText();
            header += header + (header.isEmpty()?"":"\n") + "Authorization: Basic " + base64encodedstringToDecode.getText();
            headerField.setText(header);
            slf4jLogger.error("Header field is not empty but cannot be set");
        } else {
            slf4jLogger.error("Header field is empty");
        }
    }

    public void setHeaderField(TextArea headerField) {
        this.headerField = headerField;
    }

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }
}
