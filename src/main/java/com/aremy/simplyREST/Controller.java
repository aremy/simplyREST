package com.aremy.simplyREST;

import com.sun.javafx.scene.control.skin.TextAreaSkin;
import com.sun.javafx.scene.control.skin.TextFieldSkin;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import org.apache.commons.io.IOUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Controller {
    @FXML private TextField url;
    @FXML private TextArea httpHeader;
    @FXML private ChoiceBox httpMethod;

    @FXML private TextArea httpAnswerBody;
    @FXML private ProgressIndicator downloadProgress;

    @FXML private TextField httpReturnCode;
    @FXML private TextArea httpAnswerHeaders;

    private final Logger slf4jLogger = LoggerFactory.getLogger(Controller.class);

    public void exitSuccess() {
        System.exit(0);
    }

    public void triggerApiCall() {
        //String result = url.getText() + " " + httpHeader.getText() + " " + httpMethod.getValue();
        //httpAnswerBody.setText(result);

        String targetUrl = url.getText();
        String targetHttpHeader = httpHeader.getText();
        String targetHttpMethod = (String) httpMethod.getValue();


        downloadProgress.setVisible(true);
        httpAnswerBody.setText("Downloading...");
        String targetHttpAnswer = "";
        switch (targetHttpMethod) {
            case "GET":
                targetHttpAnswer = triggerHttpGet(targetUrl, targetHttpHeader);
                break;
            case "POST":
                targetHttpAnswer = triggerHttpPost(targetUrl, targetHttpHeader);
                break;
            case "PUT":
                //targetHttpAnswer = triggerHttpPost(targetUrl, targetHttpHeader);
                break;
            case "DELETE":
                //targetHttpAnswer = triggerHttpPost(targetUrl, targetHttpHeader);
                break;
            default:
                break;

        }
        httpAnswerBody.setText(targetHttpAnswer);
        downloadProgress.setVisible(false);
    }

    private String triggerHttpPost(String targetUrl, String targetHttpHeader) {
        String result = "";
        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(targetUrl);
        List<NameValuePair> nvps = new ArrayList<>();
        // headers
        nvps.add(new BasicNameValuePair("username", "vip"));
        nvps.add(new BasicNameValuePair("password", "secret"));

        CloseableHttpResponse response2 = null;

        try {
            httpPost.setEntity(new UrlEncodedFormEntity(nvps));

            response2 = httpclient.execute(httpPost);

            httpReturnCode.setText(response2.getStatusLine().getStatusCode() + " " + response2.getStatusLine().getReasonPhrase());

            HttpEntity entity2 = response2.getEntity();
            // do something useful with the response body
            // and ensure it is fully consumed
            EntityUtils.consume(entity2);
        }
        catch (UnsupportedEncodingException e) {
            result = "Error:" + e.getMessage();
        } catch (IOException e) {
            result = "Error:" + e.getMessage();
        } finally {
            try {
                response2.close();
            } catch (IOException e) {
                result = "Error:" + e.getMessage();
            }
        }
        return result;
    }

    private String triggerHttpGet(String targetUrl, String targetHttpHeader) {
        String result;
        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet(targetUrl);
        CloseableHttpResponse response1 = null;
        try {
            response1 = httpclient.execute(httpGet);
            slf4jLogger.info("Return status when reaching {} : {}", targetUrl, response1.getStatusLine());
            httpReturnCode.setText(response1.getStatusLine().getReasonPhrase());

            HttpEntity entity1 = response1.getEntity();

            String headers = "";
            for (Header header: response1.getAllHeaders()) {
                headers += header.getName() + ": " + header.getValue() + "\n";
            }
            httpAnswerHeaders.setText(headers);

            StringWriter writer = new StringWriter();
            IOUtils.copy(entity1.getContent(), writer, getCharsetFromContentType(response1.getFirstHeader("Content-Type").getValue()));
            result = writer.toString();
            EntityUtils.consume(entity1);
        } catch (IOException e) {
            result = e.getCause().getMessage();
            slf4jLogger.error(e.getCause().getMessage());
        } finally {
            try {
                if (response1 != null)
                    response1.close();
            } catch (IOException e) {
                slf4jLogger.error(e.getCause().getMessage());
                result = "Error:" + e.getCause().getMessage();
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
