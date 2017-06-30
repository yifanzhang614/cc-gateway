package com.github.api.gateway.hystrix;


import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.zuul.context.Debug;
import com.netflix.zuul.context.RequestContext;
import org.apache.http.*;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.message.BasicHttpRequest;
import org.apache.http.message.BasicHttpResponse;
import org.apache.http.message.BasicStatusLine;
import org.codehaus.groovy.runtime.powerassert.SourceText;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.misc.IOUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Arrays;

/**
 * 请求转发
 */
public class HttpRequestRouteCommand extends HystrixCommand<HttpResponse> {

    private static final Logger LOG = LoggerFactory.getLogger(HttpRequestRouteCommand.class);
    private CloseableHttpClient httpclient;
    private String verb;
    private String uri;
    private HttpServletRequest request;
    private Header[] headers;
    private InputStream requestEntity;
    private RequestContext ctx;

    public HttpRequestRouteCommand(RequestContext ctx, CloseableHttpClient httpclient, String verb, String uri, HttpServletRequest request, Header[] headers, InputStream requestEntity) {
        super(Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("request")));
        this.ctx =ctx;
        this.httpclient = httpclient;
        this.verb = verb;
        this.uri = uri;
        this.request = request;
        this.headers = headers;
        this.requestEntity = requestEntity;

    }



    @Override
    protected HttpResponse run() throws Exception {
        System.out.println("Current Thread : " + Thread.currentThread().getName());
        HttpResponse httpResponse = null;
        try {
            httpResponse = forward(httpclient, verb, uri, request, headers, requestEntity);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return httpResponse;
    }



    @Override
    protected HttpResponse getFallback() {
        ProtocolVersion pv = new ProtocolVersion("http", 1, 2);
        return new BasicHttpResponse(new BasicStatusLine(pv, 401, "NONO"));
    }

    InputStream debug(String verb, String uri, HttpServletRequest request, Header[] headers, InputStream requestEntity) {

        return requestEntity;
    }


    String getQueryString() throws UnsupportedEncodingException, URISyntaxException {
        String encoding = "UTF-8";
        HttpServletRequest request = ctx.getRequest();
        String currentQueryString = request.getQueryString();
        if (currentQueryString == null || currentQueryString.equals("")) {
            return "";
        }

        String rebuiltQueryString = "";
        for (String keyPair : currentQueryString.split("&")) {
            if (rebuiltQueryString.length() > 0) {
                rebuiltQueryString = rebuiltQueryString + "&";
            }

            if (keyPair.contains("=")) {
                String[] kv = keyPair.split("=", 2);
                String name = kv[0];
                String value = kv[1];
                value = URLDecoder.decode(value, encoding);
                value = new URI(null, null, null, value, null).toString().substring(1);
                value = value.replaceAll("&", "%26");
                rebuiltQueryString = rebuiltQueryString + name + "=" + value;
            } else {
                String value = URLDecoder.decode(keyPair, encoding);
                value = new URI(null, null, null, value, null).toString().substring(1);
                rebuiltQueryString = rebuiltQueryString + value;
            }
        }
        return "?" + rebuiltQueryString;
    }


    InputStream debugRequestEntity(InputStream inputStream) {
        if (Debug.debugRequestHeadersOnly()) {
            return inputStream;
        }

        if (inputStream == null) {
            return null;
        }

        byte[] bytes = new byte[2048];
        int len = 0;
        String entity = "";
        try {
            while((len = inputStream.read(bytes)) != - 1) {
                entity += new String(bytes, 0, len);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        Debug.addRequestDebug("ZUUL:: Entity > ${entity}");
        return new ByteArrayInputStream(entity.getBytes());
    }


    HttpHost getHttpHost() {
        HttpHost httpHost;
        URL host = ctx.getRouteHost();
        httpHost = new HttpHost(host.getHost(), host.getPort(), host.getProtocol());
        return httpHost;
    }

    HttpResponse forward(CloseableHttpClient httpclient, String verb, String uri, HttpServletRequest request, Header[] headers, InputStream requestEntity) throws IOException, URISyntaxException {

        requestEntity = debug(verb, uri, request, headers, requestEntity);
        HttpHost httpHost = getHttpHost();
        HttpRequest httpRequest;

        switch (verb) {
            case "POST":
                httpRequest = new HttpPost(uri + getQueryString());
                InputStreamEntity entity = new InputStreamEntity(requestEntity);
                HttpPost hp = (HttpPost) httpRequest;
                hp.setEntity(entity);
                break;
            case "PUT":
                httpRequest = new HttpPut(uri + getQueryString());
                InputStreamEntity entity1 = new InputStreamEntity(requestEntity, request.getContentLength());
                HttpPut hp2 = (HttpPut) httpRequest;
                hp2.setEntity(entity1);
                break;
            default:
                httpRequest = new BasicHttpRequest(verb, uri + getQueryString());
        }

        try {
            httpRequest.setHeaders(headers);
            return forwardRequest(httpclient, httpHost, httpRequest);
        } finally {
            //httpclient.close();
        }
    }

    HttpResponse forwardRequest(HttpClient httpclient, HttpHost httpHost, HttpRequest httpRequest) throws IOException {
        return httpclient.execute(httpHost, httpRequest);
    }

}
