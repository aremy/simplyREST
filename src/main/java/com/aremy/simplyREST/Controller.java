package com.aremy.simplyREST;

import com.aremy.simplyREST.connectivity.ConnectionTask;
import com.aremy.simplyREST.headerManagers.HeaderManagerController;
import com.aremy.simplyREST.objects.PropertiesManager;
import com.sun.javafx.scene.control.skin.TextAreaSkin;
import com.sun.javafx.scene.control.skin.TextFieldSkin;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.http.*;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.*;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class Controller {
    @FXML private TextField url;
    @FXML private TextArea httpHeader;
    @FXML private TextArea httpBody;
    @FXML private ChoiceBox httpMethod;

    @FXML private TextField httpReturnCode;
    @FXML private TextArea httpAnswerHeaders;
    @FXML private TextArea httpAnswerBody;
    @FXML private ProgressBar progressBar;

    @FXML private GridPane rootPane;

    private Thread connectionThread;

    private final Logger slf4jLogger = LoggerFactory.getLogger(Controller.class);

    public void exitSuccess() {
        System.exit(0);
    }

    private Stage openPopup(String title, String fxmlResource) {
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
            controller.setUrlField(url);
            controller.setHeaderField(httpHeader);
            controller.setBodyField(httpBody);
            controller.setMethodField(httpMethod);
            dialogStage.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return dialogStage;
    }

    public void openBase64EncodeDecode() {
        openPopup("Generate Authorization Header", "/fxml/popup_base64encoder.fxml");
    }

    public void openCommonHeaders() {
        openPopup("Common headers description & samples", "/fxml/popup_commonheaders.fxml");
    }

    public void openProxyConfiguration() {
        openPopup("Proxy Configuration", "/fxml/popup_proxyproperties.fxml");
    }

    public void openLoadPresets() {
        openPopup("Load Presets", "/fxml/popup_loadpresets.fxml");
    }

    public void openSavePresets() {
        openPopup("Save Presets", "/fxml/popup_savepresets.fxml");
    }

    /**
     * The main action, trigger the "Go" button
     */
    public void triggerApiCall() {
        // ProgressBar.INDETERMINATE_PROGRESS
        progressBar.progressProperty().unbind();
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
                case "HEAD":
                    triggerHttpHead(targetUrl, headers);
                    break;
                case "TRACE":
                    triggerHttpTrace(targetUrl, headers);
                    break;
                case "OPTIONS":
                    triggerHttpOptions(targetUrl, headers);
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

    private void triggerHttpGet(String targetUrl, List<NameValuePair> headers) throws IOException {
        CloseableHttpAsyncClient httpclient = HttpAsyncClients.createDefault();
        httpclient.start();

        final HttpGet httpGet = new HttpGet(targetUrl);
        RequestConfig config = getProxyConfig();
        if (config!= null)
            httpGet.setConfig(config);
        setProxyAuth(httpclient);
        for (NameValuePair pair : headers) {
            httpGet.setHeader(pair.getName(), pair.getValue());
        }
        initiateThread(httpclient, httpGet);
    }

    // head, trace, option, connect
    private void triggerHttpHead(String targetUrl, List<NameValuePair> headers) throws IOException {
        CloseableHttpAsyncClient httpclient = HttpAsyncClients.createDefault();
        httpclient.start();

        HttpHead httpHead = new HttpHead(targetUrl);
        RequestConfig config = getProxyConfig();
        if (config!= null)
            httpHead.setConfig(config);
        setProxyAuth(httpclient);
        for (NameValuePair pair : headers) {
            httpHead.setHeader(pair.getName(), pair.getValue());
        }
        initiateThread(httpclient, httpHead);
    }

    // head, trace, option, connect
    private void triggerHttpOptions(String targetUrl, List<NameValuePair> headers) throws IOException {
        CloseableHttpAsyncClient httpclient = HttpAsyncClients.createDefault();
        httpclient.start();
        HttpOptions httpOptions = new HttpOptions(targetUrl);
        RequestConfig config = getProxyConfig();
        if (config!= null)
            httpOptions.setConfig(config);
        setProxyAuth(httpclient);
        for (NameValuePair pair : headers) {
            httpOptions.setHeader(pair.getName(), pair.getValue());
        }
        initiateThread(httpclient,httpOptions);
    }

    private void triggerHttpTrace(String targetUrl, List<NameValuePair> headers) throws IOException {
        CloseableHttpAsyncClient httpclient = HttpAsyncClients.createDefault();
        httpclient.start();
        HttpTrace httpTrace = new HttpTrace(targetUrl);
        RequestConfig config = getProxyConfig();
        if (config!= null)
            httpTrace.setConfig(config);
        setProxyAuth(httpclient);
        for (NameValuePair pair : headers) {
            httpTrace.setHeader(pair.getName(), pair.getValue());
        }
        initiateThread(httpclient,httpTrace);
    }

    private void triggerHttpDelete(String targetUrl, List<NameValuePair> headers) throws IOException {
        CloseableHttpAsyncClient httpclient = HttpAsyncClients.createDefault();
        httpclient.start();
        HttpDelete httpDelete = new HttpDelete(targetUrl);
        RequestConfig config = getProxyConfig();
        if (config!= null)
            httpDelete.setConfig(config);
        setProxyAuth(httpclient);
        for (NameValuePair pair: headers) {
            httpDelete.setHeader(pair.getName(), pair.getValue());
        }
        initiateThread(httpclient, httpDelete);
    }

    private void triggerHttpPostPut(HttpEntityEnclosingRequestBase httpPutPost, List<NameValuePair> headers, String targetBody) throws IOException {
        CloseableHttpAsyncClient httpclient = HttpAsyncClients.createDefault();
        httpclient.start();
        for (NameValuePair pair: headers) {
            httpPutPost.setHeader(pair.getName(), pair.getValue());
        }
        RequestConfig config = getProxyConfig();
        if (config!= null)
            httpPutPost.setConfig(config);
        setProxyAuth(httpclient);
        httpPutPost.setEntity(new StringEntity(targetBody));
        httpPutPost.setEntity(new UrlEncodedFormEntity(headers));
        initiateThread(httpclient,httpPutPost);
    }

    private void initiateThread(CloseableHttpAsyncClient httpclient, HttpRequestBase httpRequest) {
        Task<HttpResponse> connectionTask = new ConnectionTask(httpclient, httpRequest);
        //connectionTask.setUrl("http://google.com");
        connectionTask.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent t) {
                handleHttpResponse((HttpResponse)t.getSource().getValue());
            }
        });

        connectionTask.setOnCancelled(new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent t) {
                System.out.println("cancelled");
            }
        });
        connectionTask.setOnFailed(new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent t) {
                System.out.println("failed");
            }
        });

        if (connectionThread != null && !connectionThread.isInterrupted()) {
            connectionThread.interrupt();
        }
        progressBar.progressProperty().bind(connectionTask.progressProperty());
        connectionThread = new Thread(connectionTask);
        connectionThread.start();
    }

    private String getMimeType(HttpEntity entity) {
        ContentType contentType = ContentType.getOrDefault(entity);
        String mimeType = contentType.getMimeType();
        return mimeType;
    }
    /**
     * Sets the response code, header, body in the form
     *
     * @param response The response of the request
     * @return The body of the answer
     * @throws IOException
     */
    private void handleHttpResponse(HttpResponse response) {
        String result = "";
        String contentType = getMimeType(response.getEntity());

        httpReturnCode.setText(response.getStatusLine().toString());
        HttpEntity entity1 = response.getEntity();
        String headers = "";
        for (Header header: response.getAllHeaders()) {
            headers += header.getName() + ": " + header.getValue() + "\n";
        }
        /**
            application
            audio
            example
            image
            message
            model
            multipart
            text
            video
         */
        if (!contentType.startsWith("text") && !contentType.startsWith("message")) {
            PropertiesManager propertiesManager = PropertiesManager.instance();


            FileChooser fileChooser = new FileChooser();
            //fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
            fileChooser.setInitialDirectory(new File(propertiesManager.defaultPath));

            fileChooser.setInitialFileName(FilenameUtils.getBaseName(url.getText()) + "." + FilenameUtils.getExtension(url.getText()));
            File selectedFile = fileChooser.showSaveDialog(rootPane.getScene().getWindow());


            try {
                if (selectedFile != null) {

                    slf4jLogger.info("{}", selectedFile.getParent());
                    Files.write(Paths.get(selectedFile.toURI()), IOUtils.toByteArray(entity1.getContent()));
                    httpAnswerBody.setText("Saved file to " + selectedFile.getParent());
                    propertiesManager.defaultPath = selectedFile.getParent();
                }
            } catch (IOException e) {
                httpAnswerBody.setText("Error while saving file to " + selectedFile.getName());
                e.printStackTrace();
            }
        } else {
            try {
                StringWriter writer = new StringWriter();
                IOUtils.copy(entity1.getContent(), writer, ConnectionTask.getCharsetFromContentType(response.getFirstHeader("Content-Type").getValue()));
                result = writer.toString();
                EntityUtils.consume(entity1);
            } catch (IOException e) {
                headers = "Error";
                result = "Error";
            }
            httpAnswerBody.setText(result);
        }
        httpAnswerHeaders.setText(headers);

    }

    private RequestConfig getProxyConfig() {
        RequestConfig result = null;
        PropertiesManager propertiesManager = PropertiesManager.instance();

        // TODO: customize scheme
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

    private CloseableHttpAsyncClient setProxyAuth(CloseableHttpAsyncClient httpClient) {
        CredentialsProvider basicCredentialsProvider = null;
        PropertiesManager propertiesManager = PropertiesManager.instance();
        if (propertiesManager.proxyLogin != null && !"".equals(propertiesManager.proxyLogin)) {
            try {
                basicCredentialsProvider = new BasicCredentialsProvider();
                basicCredentialsProvider.setCredentials(
                        new AuthScope(propertiesManager.proxyHost, Short.valueOf(propertiesManager.proxyPort)),
                        new UsernamePasswordCredentials(propertiesManager.proxyLogin, propertiesManager.proxyPassword));
                httpClient = HttpAsyncClients.custom().setDefaultCredentialsProvider(basicCredentialsProvider).build();
            } catch (NumberFormatException e) {
                //
            }
        }
        return httpClient;
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
