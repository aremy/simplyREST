package com.aremy.simplyREST;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import org.apache.commons.codec.binary.Base64;

public class Base64encoderController {
    @FXML   TextField usernameToEncode;
    @FXML  TextField passwordToEncode;
    @FXML  TextField base64encodedstringToDecode;

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

    }
}
