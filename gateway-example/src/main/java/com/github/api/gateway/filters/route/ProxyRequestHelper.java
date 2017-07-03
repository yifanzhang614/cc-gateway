package com.github.api.gateway.filters.route;

import com.github.api.gateway.util.RequestUtils;
import com.google.common.collect.Maps;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.util.HTTPRequestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriTemplate;
import org.springframework.web.util.UriUtils;
import org.springframework.web.util.WebUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.*;

/**
 * Created by yifanzhang.
 */
public class ProxyRequestHelper {
    private static final Logger log = LoggerFactory.getLogger(ProxyRequestHelper.class);
    public static final String CONTENT_ENCODING = "Content-Encoding";
    public static final String CONTENT_LENGTH = "Content-Length";
    private static final Set<String> ExcludeHeaders = new HashSet<String>();
    static {
        ExcludeHeaders.add("host");
        ExcludeHeaders.add("connection");
        ExcludeHeaders.add("content-length");
        ExcludeHeaders.add("content-encoding");
        ExcludeHeaders.add("server");
        ExcludeHeaders.add("transfer-encoding");
        ExcludeHeaders.add("x-application-context");
    }

    /**
     * Zuul context key for a collection of ignored headers for the current request.
     * Pre-filters can set this up as a set of lowercase strings.
     */
    public static final String IGNORED_HEADERS = "ignoredHeaders";

    private Set<String> ignoredHeaders = new LinkedHashSet<String>();

    private Set<String> sensitiveHeaders = new LinkedHashSet<String>();

    private Set<String> whitelistHosts = new LinkedHashSet<String>();

    public void setWhitelistHosts(Set<String> whitelistHosts) {
        this.whitelistHosts.addAll(whitelistHosts);
    }

    public void setSensitiveHeaders(Set<String> sensitiveHeaders) {
        this.sensitiveHeaders.addAll(sensitiveHeaders);
    }

    public void setIgnoredHeaders(Set<String> ignoredHeaders) {
        this.ignoredHeaders.addAll(ignoredHeaders);
    }

    public String buildZuulRequestURI(HttpServletRequest request) {
        RequestContext context = RequestContext.getCurrentContext();
        String uri = request.getRequestURI();
        String contextURI = (String) context.get("requestURI");
        if (contextURI != null) {
            try {
                uri = UriUtils.encodePath(contextURI, WebUtils.DEFAULT_CHARACTER_ENCODING);
            }
            catch (Exception e) {
                log.debug(
                    "unable to encode uri path from context, falling back to uri from request",
                    e);
            }
        }
        return uri;
    }

    public MultiValueMap<String, String> buildZuulRequestQueryParams(
        HttpServletRequest request) {
        Map<String, List<String>> map = HTTPRequestUtils.getInstance().getQueryParams();
        MultiValueMap<String, String> params = new LinkedMultiValueMap<String,String>();
        if (map == null) {
            return params;
        }
        for (String key : map.keySet()) {
            for (String value : map.get(key)) {
                params.add(key, value);
            }
        }
        return params;
    }

    public MultiValueMap<String, String> buildZuulRequestHeaders(
        HttpServletRequest request) {
        RequestContext context = RequestContext.getCurrentContext();
        MultiValueMap<String, String> headers = new HttpHeaders();
        Enumeration<String> headerNames = request.getHeaderNames();
        if (headerNames != null) {
            while (headerNames.hasMoreElements()) {
                String name = headerNames.nextElement();
                if (isIncludedHeader(name)) {
                    Enumeration<String> values = request.getHeaders(name);
                    while (values.hasMoreElements()) {
                        String value = values.nextElement();
                        headers.add(name, value);
                    }
                }
            }
        }
        Map<String, String> zuulRequestHeaders = context.getZuulRequestHeaders();
        for (String header : zuulRequestHeaders.keySet()) {
            headers.set(header, zuulRequestHeaders.get(header));
        }
        headers.set(HttpHeaders.ACCEPT_ENCODING, "gzip");
        return headers;
    }

    public void setResponse(int status, InputStream entity,
        MultiValueMap<String, String> headers) throws IOException {
        RequestContext context = RequestContext.getCurrentContext();
        context.setResponseStatusCode(status);
        if (entity != null) {
            context.setResponseDataStream(entity);
        }

        HttpHeaders httpHeaders = new HttpHeaders();
        for (Map.Entry<String, List<String>> header : headers.entrySet()) {
            List<String> values = header.getValue();
            for (String value : values) {
                httpHeaders.add(header.getKey(), value);
            }
        }
        boolean isOriginResponseGzipped = false;
        if (httpHeaders.containsKey(CONTENT_ENCODING)) {
            List<String> collection = httpHeaders.get(CONTENT_ENCODING);
            for (String header : collection) {
                if (HTTPRequestUtils.getInstance().isGzipped(header)) {
                    isOriginResponseGzipped = true;
                    break;
                }
            }
        }
        context.setResponseGZipped(isOriginResponseGzipped);

        for (Map.Entry<String, List<String>> header : headers.entrySet()) {
            String name = header.getKey();
            for (String value : header.getValue()) {
                context.addOriginResponseHeader(name, value);
                if (name.equalsIgnoreCase(CONTENT_LENGTH)) {
                    context.setOriginContentLength(value);
                }
                if (isIncludedHeader(name)) {
                    context.addZuulResponseHeader(name, value);
                }
            }
        }
    }

    public void addIgnoredHeaders(String... names) {
        RequestContext ctx = RequestContext.getCurrentContext();
        if (!ctx.containsKey(IGNORED_HEADERS)) {
            ctx.set(IGNORED_HEADERS, new HashSet<String>());
        }
        @SuppressWarnings("unchecked")
        Set<String> set = (Set<String>) ctx.get(IGNORED_HEADERS);
        for (String name : this.ignoredHeaders) {
            set.add(name.toLowerCase());
        }
        for (String name : names) {
            set.add(name.toLowerCase());
        }
    }

    public boolean isIncludedHeader(String headerName) {
        String name = headerName.toLowerCase();
        RequestContext ctx = RequestContext.getCurrentContext();
        if (ctx.containsKey(IGNORED_HEADERS)) {
            Object object = ctx.get(IGNORED_HEADERS);
            if (object instanceof Collection && ((Collection<?>) object).contains(name)) {
                return false;
            }
        }

        Set<String> hashSet = new HashSet<String>();

        if (ExcludeHeaders.contains(name)){
            return false;
        } else {
            return true;
        }

//        switch (name) {
//            case "host":
//            case "connection":
//            case "content-length":
//            case "content-encoding":
//            case "server":
//            case "transfer-encoding":
//            case "x-application-context":
//                return false;
//            default:
//                return true;
//        }
    }

    public Map<String, Object> debug(String verb, String uri,
        MultiValueMap<String, String> headers, MultiValueMap<String, String> params,
        InputStream requestEntity) throws IOException {
        Map<String, Object> info = Maps.newLinkedHashMap();

//        if (this.traces != null) {
        if (log.isDebugEnabled()) {
            RequestContext context = RequestContext.getCurrentContext();
            info.put("method", verb);
            info.put("path", uri);
            info.put("query", getQueryString(params));
            info.put("remote", true);
            info.put("proxy", context.get("proxy"));
            Map<String, Object> trace = Maps.newLinkedHashMap();
            Map<String, Object> input = Maps.newLinkedHashMap();
            trace.put("request", input);
            info.put("headers", trace);
            for (Map.Entry<String, List<String>> entry : headers.entrySet()) {
                Collection<String> collection = entry.getValue();
                Object value = collection;
                if (collection.size() < 2) {
                    value = collection.isEmpty() ? "" : collection.iterator().next();
                }
                input.put(entry.getKey(), value);
            }
            RequestContext ctx = RequestContext.getCurrentContext();
            if (shouldDebugBody(ctx)) {
                // Prevent input stream from being read if it needs to go downstream
                if (requestEntity != null) {
                    debugRequestEntity(info, ctx.getRequest().getInputStream());
                }
            }
//            this.traces.add(info);
            log.debug("Info :"+info.toString());
            return info;
        }
        return info;
    }

    /* for tests */
    boolean shouldDebugBody(RequestContext ctx) {
        HttpServletRequest request = ctx.getRequest();
        if (ctx.isChunkedRequestBody() || RequestUtils.isZuulServletRequest()) {
            return false;
        }
        if (request == null || request.getContentType() == null) {
            return true;
        }
        return !request.getContentType().toLowerCase().contains("multipart");
    }

    public void appendDebug(Map<String, Object> info, int status,
        MultiValueMap<String, String> headers) {
//        if (this.traces != null) {
        if (log.isDebugEnabled()) {
            @SuppressWarnings("unchecked")
            Map<String, Object> trace = (Map<String, Object>) info.get("headers");
            Map<String, Object> output = new LinkedHashMap<String, Object>();
            trace.put("response", output);
            for (Map.Entry<String, List<String>> key : headers.entrySet()) {
                Collection<String> collection = key.getValue();
                Object value = collection;
                if (collection.size() < 2) {
                    value = collection.isEmpty() ? "" : collection.iterator().next();
                }
                output.put(key.getKey(), value);
            }
            output.put("status", "" + status);
            log.debug("Info: "+info);
        }
    }

    private void debugRequestEntity(Map<String, Object> info, InputStream inputStream)
        throws IOException {
        if (RequestContext.getCurrentContext().isChunkedRequestBody()) {
            info.put("body", "<chunked>");
            return;
        }
        char[] buffer = new char[4096];
        int count = new InputStreamReader(inputStream, Charset.forName("UTF-8"))
            .read(buffer, 0, buffer.length);
        if (count > 0) {
            String entity = new String(buffer).substring(0, count);
            info.put("body", entity.length() < 4096 ? entity : entity + "<truncated>");
        }
    }

    public String getQueryString(MultiValueMap<String, String> params) {
        if (params.isEmpty()) {
            return "";
        }
        StringBuilder query = new StringBuilder();
        Map<String, Object> singles = new HashMap<String,Object>();
        for (String param : params.keySet()) {
            int i = 0;
            for (String value : params.get(param)) {
                query.append("&");
                query.append(param);
                if (!"".equals(value)) {
                    singles.put(param + i, value);
                    query.append("={");
                    query.append(param + i);
                    query.append("}");
                }
                i++;
            }
        }

        UriTemplate template = new UriTemplate("?" + query.toString().substring(1));
        return template.expand(singles).toString();
    }
}
