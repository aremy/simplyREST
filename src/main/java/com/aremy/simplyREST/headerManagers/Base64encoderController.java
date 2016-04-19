package com.aremy.simplyREST.headerManagers;

import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Base64encoderController extends HeaderManagerController {

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
        String header = "Authorization: Basic " + base64encodedstringToDecode.getText();
        setHeaderForm(header);
    }


}
