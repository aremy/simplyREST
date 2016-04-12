package com.aremy.simplyREST;

import com.sun.javafx.scene.control.skin.TextAreaSkin;
import com.sun.javafx.scene.control.skin.TextFieldSkin;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Controller {
    @FXML private TextField url;
    @FXML private TextArea httpHeader;
    @FXML private ChoiceBox httpMethod;

    @FXML private TextArea httpAnswer;

    public void exitSuccess() {
        System.exit(0);
    }

    public void triggerApiCall() {
        String result = url.getText() + " " + httpHeader.getText() + " " + httpMethod.getValue();
        httpAnswer.setText(result);

        String targetUrl = url.getText();
        String targetHttpHeader = httpHeader.getText();
        String targetHttpMethod = (String) httpMethod.getValue();


        String targetHttpAnswer = "";
        switch (targetHttpMethod) {
            case "GET":
                targetHttpAnswer = triggerHttpGet(targetUrl, targetHttpHeader);
                break;
            case "POST":
                break;
            default:
                break;

        }
        httpAnswer.setText(targetHttpAnswer);
/*
        /// POST
        HttpPost httpPost = new HttpPost(targetUrl);
        List<NameValuePair> nvps = new ArrayList<NameValuePair>();
        nvps.add(new BasicNameValuePair("username", "vip"));
        nvps.add(new BasicNameValuePair("password", "secret"));
        try {
            httpPost.setEntity(new UrlEncodedFormEntity(nvps));

            CloseableHttpResponse response2 = null;

            response2 = httpclient.execute(httpPost);
        }
        catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            System.out.println(response2.getStatusLine());
            HttpEntity entity2 = response2.getEntity();
            // do something useful with the response body
            // and ensure it is fully consumed
            EntityUtils.consume(entity2);
        } finally {
            response2.close();
        }
*/


    }

    private String triggerHttpGet(String targetUrl, String targetHttpHeader) {
        String result;
        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet(targetUrl);
        CloseableHttpResponse response1 = null;
        try {
            response1 = httpclient.execute(httpGet);
            System.out.println(response1.getStatusLine());
            HttpEntity entity1 = response1.getEntity();

            StringWriter writer = new StringWriter();
            IOUtils.copy(entity1.getContent(), writer, getCharsetFromContentType(response1.getFirstHeader("Content-Type").getValue()));
            result = writer.toString();
            EntityUtils.consume(entity1);
        } catch (IOException e) {
            result = "Error:" + e.getMessage();
        } finally {
            try {
                response1.close();
            } catch (IOException e) {
                result = "Error:" + e.getMessage();
            }
        }
        return result;
    }

    private String getCharsetFromContentType(String contentType) {
        Pattern charsetPattern = Pattern.compile("(?i)\\bcharset=\\s*\"?([^\\s;\"]*)");
        String result = "UTF-8";
        if (contentType == null)
            return null;

        Matcher m = charsetPattern.matcher(contentType);
        if (m.find()) {
            result = m.group(1).trim().toUpperCase();
        }
        return result;
    }

    public void filterTab(KeyEvent event) {
        if (event.getCode().equals(KeyCode.TAB)) {
            Node node = (Node) event.getSource();
            if (node instanceof TextField) {
                TextFieldSkin skin = (TextFieldSkin) ((TextField)node).getSkin();
                if (event.isShiftDown()) {
                    skin.getBehavior().traversePrevious();
                }
                else {
                    skin.getBehavior().traverseNext();
                }
            }
            else if (node instanceof TextArea) {
                TextAreaSkin skin = (TextAreaSkin) ((TextArea)node).getSkin();
                if (event.isShiftDown()) {
                    skin.getBehavior().traversePrevious();
                }
                else {
                    skin.getBehavior().traverseNext();
                }
            }

            event.consume();
        }
    }
}
