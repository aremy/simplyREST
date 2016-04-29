package com.aremy.simplyREST;

import com.aremy.simplyREST.headerManagers.HeaderManagerController;
import com.aremy.simplyREST.objects.PropertiesManager;
import com.sun.javafx.scene.control.skin.TextAreaSkin;
import com.sun.javafx.scene.control.skin.TextFieldSkin;
import com.sun.org.apache.xpath.internal.operations.Number;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.apache.commons.io.IOUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.NameValuePair;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.*;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCredentialsProvider;
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
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Controller {
    @FXML private TextField url;
    @FXML private TextArea httpHeader;
    @FXML private TextArea httpBody;
    @FXML private ChoiceBox httpMethod;

    @FXML private TextField httpReturnCode;
    @FXML private TextArea httpAnswerHeaders;
    @FXML private TextArea httpAnswerBody;
    @FXML private ProgressIndicator downloadProgress;

    @FXML private GridPane rootPane;

    private final Logger slf4jLogger = LoggerFactory.getLogger(Controller.class);

    public void exitSuccess() {
        System.exit(0);
    }


    /*RequestConfig config = RequestConfig.custom()
            .setProxy(proxy)
            .build();
    */

    private Stage openPoup(String title, String fxmlResource) {
        final Stage dialogStage = new Stage();
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(Main.class.getResource(fxmlResource));
            GridPane pane = loader.load();
            dialogStage.setTitle(title);
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(rootPane.getScene().getWindow());
            dialogStage.getIcons().add(new Image(Main.class.getResourceAsStream("/img/World_icon.png")));
            Scene scene = new Scene(pane);
            scene.addEventFilter(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
                @Override
                public void handle(KeyEvent t) {
                    if(t.getCode() == KeyCode.ESCAPE)
                    {
                        dialogStage.close();
                    }
                }
            });
            dialogStage.setScene(scene);
            HeaderManagerController controller = loader.getController();
            controller.setDialogStage(dialogStage);
            controller.setHeaderField(httpHeader);
            dialogStage.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return dialogStage;
    }

    public void openBase64EncodeDecode() {
        openPoup("Generate Authorization Header", "/fxml/base64encoder.fxml");
    }

    public void openCommonHeaders() {
        openPoup("Common headers description & samples", "/fxml/headers.fxml");
    }

    public void openProxyConfiguration() {
        openPoup("Proxy Configuration", "/fxml/properties.fxml");
    }

    /**
     * The main action, trigger the "Go" button
     */
    public void triggerApiCall() {
        // ProgressBar.INDETERMINATE_PROGRESS
        httpAnswerBody.setText("");
        String targetUrl = url.getText();
        // prepend https:// if url does not start with http, https or ftp
        if (!targetUrl.matches("^(https?|ftp)://.*$")) {
            targetUrl = "https://" + targetUrl;
            url.setText(targetUrl);
        }
        // build headers if any
        String targetHttpHeader = httpHeader.getText();
        List<NameValuePair> headers = new ArrayList<>();
        if (!targetHttpHeader.isEmpty()) {
            String[] lines = targetHttpHeader.split("\n");
            for (String line: lines) {
                String[] pair = line.split(":");
                if (pair.length == 2) {
                    slf4jLogger.info("{}:{}", pair[0], pair[1]);
                    headers.add(new BasicNameValuePair(pair[0].trim(), pair[1].trim()));
                }
            }
        }
        String targetBody = httpBody.getText();
        //slf4jLogger.info("Request headers:\n{}", headers.toString());
        //downloadProgress.setVisible(true);

        httpAnswerHeaders.setText("Downloading...");
        httpAnswerBody.setText("Downloading...");

        try {
            switch ((String) httpMethod.getValue()) {
                case "GET":
                    triggerHttpGet(targetUrl, headers);
                    break;
                case "POST":
                    triggerHttpPostPut(new HttpPost(targetUrl), headers, targetBody);
                    break;
                case "PUT":
                    triggerHttpPostPut(new HttpPut(targetUrl), headers, targetBody);
                    break;
                case "DELETE":
                    triggerHttpDelete(targetUrl, headers);
                    break;
                default:
                    break;

            }
        } catch (UnsupportedEncodingException e){
            slf4jLogger.error(e.getCause().getMessage());
            httpAnswerHeaders.setText(e.getCause().getMessage());
            httpAnswerBody.setText(e.getCause().getMessage());

        } catch (IOException e) {
            slf4jLogger.error("Could not reach {}", targetUrl);
            httpAnswerHeaders.setText("Could not reach " + targetUrl);
            httpAnswerBody.setText("Could not reach " + targetUrl);

        }
    }

    private String triggerHttpGet(String targetUrl, List<NameValuePair> headers) throws IOException {
        CloseableHttpResponse response = null;
        try {
            String result;
            //CloseableHttpClient httpclient = HttpClients.createDefault();
            CloseableHttpClient httpclient = HttpClients.createDefault();
            HttpGet httpGet = new HttpGet(targetUrl);
            RequestConfig config = getProxyConfig();
            if (config!= null)
                httpGet.setConfig(config);
                setProxyAuth(httpclient);
            for (NameValuePair pair : headers) {
                httpGet.setHeader(pair.getName(), pair.getValue());
            }
            response = httpclient.execute(httpGet);
            result = handleHttpResponse(response);
            return result;
        } finally {
            if (response != null)
                response.close();
        }
    }

    private String triggerHttpDelete(String targetUrl, List<NameValuePair> headers) throws IOException {
        CloseableHttpResponse response = null;
        try {
            String result;
            CloseableHttpClient httpclient = HttpClients.createDefault();
            HttpDelete httpDelete = new HttpDelete(targetUrl);
            RequestConfig config = getProxyConfig();
            if (config!= null)
                httpDelete.setConfig(config);
                setProxyAuth(httpclient);
            for (NameValuePair pair: headers) {
                httpDelete.setHeader(pair.getName(), pair.getValue());
            }
            response = httpclient.execute(httpDelete);
            result = handleHttpResponse(response);
            return result;
        } finally {
            if (response != null)
                response.close();
        }
    }

    private String triggerHttpPostPut(HttpEntityEnclosingRequestBase httpPutPost, List<NameValuePair> headers, String targetBody) throws IOException {
        CloseableHttpResponse response = null;
        try {
            String result;
            CloseableHttpClient httpclient = HttpClients.createDefault();
            for (NameValuePair pair: headers) {
                httpPutPost.setHeader(pair.getName(), pair.getValue());
            }
            RequestConfig config = getProxyConfig();
            if (config!= null)
                httpPutPost.setConfig(config);
                setProxyAuth(httpclient);
            httpPutPost.setEntity(new StringEntity(targetBody));
            httpPutPost.setEntity(new UrlEncodedFormEntity(headers));
            response = httpclient.execute(httpPutPost);
            result = handleHttpResponse(response);
            return result;
        } finally {
            if (response != null)
                response.close();
        }
    }

    private RequestConfig getProxyConfig() {
        RequestConfig result = null;
        PropertiesManager propertiesManager = PropertiesManager.instance();

        // scheme
        try {
            HttpHost proxy = new HttpHost(propertiesManager.proxyHost, Short.valueOf(propertiesManager.proxyPort), "http");
            result = RequestConfig.custom()
                    .setProxy(proxy)
                    .build();
        } catch (NumberFormatException e) {
            //
        }
        return result;
    }

    private CloseableHttpClient setProxyAuth(CloseableHttpClient httpClient) {
        CredentialsProvider basicCredentialsProvider = null;
        PropertiesManager propertiesManager = PropertiesManager.instance();
        if (propertiesManager.proxyLogin != null && !"".equals(propertiesManager.proxyLogin)) {
          try {
              basicCredentialsProvider = new BasicCredentialsProvider();
              basicCredentialsProvider.setCredentials(
                      new AuthScope(propertiesManager.proxyHost, Short.valueOf(propertiesManager.proxyPort)),
                      new UsernamePasswordCredentials(propertiesManager.proxyLogin, propertiesManager.proxyPassword));
              httpClient = HttpClients.custom().setDefaultCredentialsProvider(basicCredentialsProvider).build();
          } catch (NumberFormatException e) {
              //
          }
        }
        return httpClient;
    }

    /**
     * Sets the response code, header, body in the form
     *
     * @param response The response of the request
     * @return The body of the answer
     * @throws IOException
     */
    private String handleHttpResponse(CloseableHttpResponse response) throws IOException {
        String result = "";
        httpReturnCode.setText(response.getStatusLine().toString());
        HttpEntity entity1 = response.getEntity();
        String headers = "";
        for (Header header: response.getAllHeaders()) {
            headers += header.getName() + ": " + header.getValue() + "\n";
        }

        StringWriter writer = new StringWriter();
        IOUtils.copy(entity1.getContent(), writer, getCharsetFromContentType(response.getFirstHeader("Content-Type").getValue()));
        result = writer.toString();
        EntityUtils.consume(entity1);

        httpAnswerHeaders.setText(headers);
        httpAnswerBody.setText(result);

        return result;
    }

    /**
     * Extracts the character encoding (UTF-8, isoXXX) from the "Content-Type" http header
     *
     * @param contentType The value of a "Content-Type" http header
     * @return The character encoding if available, null otherwise
     */
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

    /**
     * Ensures that we can switch to the next form using "tab"
     * Otherwise using "tab" in textarea just adds a tab.
     *
     * @param event
     */
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

    public void filterEnter(KeyEvent event) {
        if (event.getCode().equals(KeyCode.ENTER)) {
            triggerApiCall();
            event.consume();
        }
    }
}
