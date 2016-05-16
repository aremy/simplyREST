package com.aremy.simplyREST.connectivity;

import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import org.apache.commons.io.IOUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.concurrent.FutureCallback;
import org.apache.http.entity.BasicHttpEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.reactor.ExceptionEvent;
import org.apache.http.nio.IOControl;
import org.apache.http.nio.client.methods.AsyncCharConsumer;
import org.apache.http.nio.client.methods.HttpAsyncMethods;
import org.apache.http.nio.protocol.HttpAsyncRequestProducer;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.nio.CharBuffer;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Future;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ConnectionThread extends Thread {
    private CloseableHttpAsyncClient client;
    private HttpContext context;
    private HttpRequestBase request;
    private TextField httpReturnCode;
    private TextArea httpAnswerHeaders;
    private TextArea httpAnswerBody;
    private ProgressBar progressBar;


    public ConnectionThread(CloseableHttpAsyncClient client, HttpRequestBase req, TextField httpReturnCode, TextArea httpAnswerHeaders, TextArea httpAnswerBody, ProgressBar progressBar) {
        this.client = client;
        context = HttpClientContext.create();
        this.request = req;
        this.httpReturnCode = httpReturnCode;
        this.httpAnswerHeaders = httpAnswerHeaders;
        this.httpAnswerBody = httpAnswerBody;
        this.progressBar = progressBar;
    }

    @Override
    public void run() {
        try {
            final CountDownLatch latch2 = new CountDownLatch(1);

            HttpAsyncRequestProducer producer3 = HttpAsyncMethods.create(request);
            AsyncCharConsumer<HttpResponse> consumer3 = new AsyncCharConsumer<HttpResponse>() {
                HttpResponse response;
                StringWriter contentPart = new StringWriter();
                String encoding = null;
                double contentSize = -1;
                double consumed = 0;

                @Override
                protected void onResponseReceived(final HttpResponse response) {
                    this.response = response;
                    this.encoding = getCharsetFromContentType(response.getFirstHeader("Content-Type").getValue());

                    Header contentType = response.getFirstHeader("Content-Type");

                    if (ContentType.APPLICATION_OCTET_STREAM.equals(contentType)) {
                        // popup to select save locations
                    }

                    try {
                        if (response.getFirstHeader("Content-Length") == null) {
                            this.contentSize = -1;
                        } else {
                            this.contentSize = Double.valueOf(response.getFirstHeader("Content-Length").getValue());
                        }
                    } catch (Exception e) {
                        this.contentSize = -1;
                        e.printStackTrace();
                    }
                }

                @Override
                protected void onCharReceived(final CharBuffer buf, final IOControl ioctrl) throws IOException {
                    System.out.println("onchar");
                    double total = contentSize;
                    if (total == -1) {
                        consumed = 0;
                        total = buf.length();
                    }

                    while (buf.hasRemaining()) {
                        contentPart.append(buf.get());
                        consumed++;
                        progressBar.setProgress(consumed/total);
                    }
                }

                @Override
                protected void releaseResources() {
                    System.out.println("release");
                }

                @Override
                protected HttpResponse buildResult(final HttpContext context) {
                    System.out.println("---");
                    BasicHttpEntity myEntity = (BasicHttpEntity) response.getEntity();
                    try {
                        myEntity.setContent(new ByteArrayInputStream(contentPart.toString().getBytes(encoding)));
                    } catch (UnsupportedEncodingException e) {
                        //slf4jLogger.error("Encoding " + encoding + " not supported ; content cannot be read");
                        e.printStackTrace();
                    }
                    progressBar.setProgress(1);
                    return this.response;
                }

            };
            Future<HttpResponse> future = client.execute(producer3, consumer3, new FutureCallback<HttpResponse>() {

                public void completed(final HttpResponse response3) {
                    latch2.countDown();
                    System.out.println(request.getRequestLine() + "->" + response3.getStatusLine());
                    try {
                        handleHttpResponse(response3);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                public void failed(final Exception ex) {
                    latch2.countDown();
                    System.out.println(request.getRequestLine() + "->" + ex);
                }

                public void cancelled() {
                    latch2.countDown();
                    System.out.println(request.getRequestLine() + " cancelled");
                }

            });

            latch2.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            try {
                client.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Sets the response code, header, body in the form
     *
     * @param response The response of the request
     * @return The body of the answer
     * @throws IOException
     */
    private String handleHttpResponse(HttpResponse response) throws IOException {
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

}
