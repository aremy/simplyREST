package com.aremy.simplyREST;

import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.entity.StringEntity;
import org.apache.http.localserver.LocalTestServer;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpRequestHandler;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.*;

public class ControllerTest {
    private LocalTestServer server = null;
    private HttpRequestHandler handler = null;

    @Before
    public void setUp() throws Exception {
        handler = new HttpRequestHandler() {
            public void handle(
                    HttpRequest request,
                    HttpResponse response,
                    HttpContext context) throws HttpException, IOException {
                String uri = request.getRequestLine().getUri();
                response.setEntity(new StringEntity(uri));
            }};

        server = new LocalTestServer(null, null);
        server.register("/someUrl/*", handler);
        server.start();

        String serverUrl = "http://" + server.getServiceHostName() + ":"
                + server.getServicePort();
        System.out.println("LocalTestServer available at " + serverUrl);
    }

    @Test
    public void exitSuccess() throws Exception {

    }

    @Test
    public void openBase64EncodeDecode() throws Exception {

    }

    @Test
    public void triggerApiCall() throws Exception {

    }

    @Test
    public void filterTab() throws Exception {

    }

    @Test
    public void filterEnter() throws Exception {

    }

    @After
    public void tearDown() throws Exception {
        server.stop();
    }
}