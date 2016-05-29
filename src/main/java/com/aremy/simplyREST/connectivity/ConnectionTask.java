package com.aremy.simplyREST.connectivity;

import javafx.concurrent.Task;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.concurrent.FutureCallback;
import org.apache.http.entity.BasicHttpEntity;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.nio.IOControl;
import org.apache.http.nio.client.methods.AsyncByteConsumer;
import org.apache.http.nio.client.methods.HttpAsyncMethods;
import org.apache.http.nio.protocol.HttpAsyncRequestProducer;
import org.apache.http.protocol.HttpContext;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Future;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ConnectionTask extends Task<HttpResponse> {
    private CloseableHttpAsyncClient client;
    private HttpContext context;
    private HttpRequestBase request;

    public ConnectionTask(CloseableHttpAsyncClient client, HttpRequestBase req) {
        this.client = client;
        context = HttpClientContext.create();
        this.request = req;
    }

    @Override
    protected HttpResponse call() throws Exception {
        Future<HttpResponse> future = null;
        try {
            final CountDownLatch latch2 = new CountDownLatch(1);
            HttpAsyncRequestProducer producer3 = HttpAsyncMethods.create(request);
            AsyncByteConsumer<HttpResponse> consumer3 = new AsyncByteConsumer<HttpResponse>() {

                private HttpResponse response;
                //StringWriter contentPart = new StringWriter();
                //String encoding = null;
                double contentSize = -1;
                double consumed = 0;
                private byte[] contentPart ;

                @Override
                protected void onResponseReceived(final HttpResponse response) {
                    System.out.println("Received");
                    this.response = response;
                    //this.encoding = getCharsetFromContentType(response.getFirstHeader("Content-Type").getValue());
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
                protected void onByteReceived(ByteBuffer buf, IOControl ioControl) throws IOException {

                    //TODO: save in a tmp file - for now we'll just put in memory
                    double total = contentSize;
                    if (total == -1) {
                        consumed = 0;
                        total = buf.limit();
                    }

                    byte[] array = new byte[buf.limit()];
                    buf.get(array);
                    byte[] combined;
                    if (contentPart == null) {
                        contentPart = new byte[0];
                    }
                    combined = new byte[contentPart.length + array.length];
                    for (int i = 0; i < combined.length; ++i)
                    {
                        combined[i] = i < contentPart.length ? contentPart[i] : array[i - contentPart.length];
                    }
                    contentPart = combined;


                    consumed += buf.limit();
                    //System.out.println(consumed + "/" + total);
                    updateProgress(consumed, total);
                }

                @Override
                protected void releaseResources() {
                    super.releaseResources();
                    this.response = null;
                }

                @Override
                protected HttpResponse buildResult(final HttpContext context) {
                    BasicHttpEntity myEntity = (BasicHttpEntity) response.getEntity();
                    //myEntity.setContent(contentPart.toString());
                    /*try {
                        myEntity.setContent(new ByteArrayInputStream(contentPart.toString().getBytes(encoding)));
                    } catch (UnsupportedEncodingException e) {
                        //slf4jLogger.error("Encoding " + encoding + " not supported ; content cannot be read");
                        e.printStackTrace();
                    }*/
                    // todo: will be a fileinputstream from the tmp file.
                    // do it only if content > certain size?
                    myEntity.setContent(new ByteArrayInputStream(contentPart));
                    updateProgress(1,1);
                    return this.response;
                }

            };
            future = client.execute(producer3, consumer3, new FutureCallback<HttpResponse>() {
                public void completed(final HttpResponse response3) {
                    latch2.countDown();
                    System.out.println(request.getRequestLine() + "->" + response3.getStatusLine());
                    // request is completed, will be handled by the Task handler
                }

                public void failed(final Exception ex) {
                    latch2.countDown();
                    System.out.println(request.getRequestLine() + " failed");
                    ex.printStackTrace();
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
        return future.get();
    }

    public class ByteBufferInputStream extends InputStream {

        private int bbisInitPos;
        private int bbisLimit;
        private ByteBuffer bbisBuffer;

        public ByteBufferInputStream(ByteBuffer buffer) {
            this(buffer, buffer.limit() - buffer.position());
        }

        public ByteBufferInputStream(ByteBuffer buffer, int limit) {
            bbisBuffer = buffer;
            bbisLimit = limit;
            bbisInitPos = bbisBuffer.position();
        }

        @Override
        public int read() throws IOException {
            if (bbisBuffer.position() - bbisInitPos > bbisLimit)
                return -1;
            return bbisBuffer.get();
        }
    }

    /**
     * Extracts the character encoding (UTF-8, isoXXX) from the "Content-Type" http header
     *
     * @param contentType The value of a "Content-Type" http header
     * @return The character encoding if available, null otherwise
     */
    public static String getCharsetFromContentType(String contentType) {
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
