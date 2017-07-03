package com.github.gateway.authc.token;

import com.github.gateway.authc.HostAuthenticationToken;
import org.apache.commons.lang3.StringUtils;


import java.util.Map;

/**
 * Created by chongdi.yang on 2016/8/7.
 */
public class CcAppSignatureToken implements HostAuthenticationToken {

    private String key;
    private String random;
    private Long timestamp;  //五分钟内有效
    private String signature;
    private String host;
    private Map<String, String[]> params;

    public CcAppSignatureToken() {}

    public CcAppSignatureToken(String key, String random, Long timestamp, String signature, String host) {
        this.key = key;
        this.random = random;
        this.timestamp = timestamp;
        this.signature = signature;
        this.host = host;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getRandom() {
        return random;
    }

    public void setRandom(String random) {
        this.random = random;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public void setHost(String host) {
        this.host = host;
    }

    @Override
    public String getHost() {
        return this.host;
    }

    @Override
    public Object getPrincipal() {
        return getKey();
    }

    @Override
    public Object getCredentials() {
        return getSignature();
    }

    public Map<String, String[]> getParams() {
        return params;
    }

    public void setParams(Map<String, String[]> params) {
        this.params = params;
    }

    public boolean needAuthenticate() {
        return StringUtils.isEmpty(getRandom()) || StringUtils.isEmpty(getSignature()) || StringUtils.isEmpty(getKey()) || getTimestamp() == null;
    }
}
