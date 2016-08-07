package com.github.api.gateway.provider.producer;

import com.github.api.gateway.authc.AuthenticationToken;
import com.github.api.gateway.authc.token.CcAppSignatureToken;
import org.apache.commons.lang3.math.NumberUtils;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by chongdi.yang on 2016/8/7.
 */
public class CcSignatureTokenProducer extends AbstractAuthenticationTokenProducer {

    public static  final String RANDOM = "X-CC-Auth-Nonce";
    public static  final String SIGNATURE = "X-CC-Auth-Signature";
    public static  final String TIMESTAMP = "X-CC-Auth-Timestamp";
    public static  final String KEY = "X-CC-Auth-key";

    private String randomParam = RANDOM;
    private String signatureParam = SIGNATURE;
    private String timestampParam = TIMESTAMP;
    private String keyParam = KEY;



    @Override
    public boolean supports(Object any) {
        if(any == null) {
            return false;
        }
        if(any instanceof HttpServletRequest) {
            HttpServletRequest request = (HttpServletRequest) any;
            if(request.getParameter(this.keyParam) != null) {
                return true;
            }
        }
        return false;
    }

    @Override
    public AuthenticationToken produce(Object any) {
        HttpServletRequest request = (HttpServletRequest) any;
        String random = request.getHeader(this.randomParam);
        String signature = request.getHeader(this.signatureParam);
        Long timestamp = NumberUtils.toLong(request.getHeader(this.timestampParam), 0);
        String key = request.getHeader(this.keyParam);
        return new CcAppSignatureToken(key, random, timestamp, signature, request.getRemoteHost());
    }

    public String getRandomParam() {
        return randomParam;
    }

    public void setRandomParam(String randomParam) {
        this.randomParam = randomParam;
    }

    public String getSignatureParam() {
        return signatureParam;
    }

    public void setSignatureParam(String signatureParam) {
        this.signatureParam = signatureParam;
    }

    public String getTimestampParam() {
        return timestampParam;
    }

    public void setTimestampParam(String timestampParam) {
        this.timestampParam = timestampParam;
    }

    public String getKeyParam() {
        return keyParam;
    }

    public void setKeyParam(String keyParam) {
        this.keyParam = keyParam;
    }
}
