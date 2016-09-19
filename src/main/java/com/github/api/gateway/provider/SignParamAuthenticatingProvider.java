package com.github.api.gateway.provider;

import com.github.api.gateway.authc.credential.CredentialsMatcher;
import com.github.api.gateway.authc.token.CcAppSignatureToken;
import com.github.api.gateway.provider.producer.AuthenticationTokenProducer;
import com.github.api.gateway.provider.producer.encrypt.SignatureParamTokenProducer;
import org.apache.commons.lang.StringUtils;

import java.util.*;

/**
 * 对请求参数也会进行签名的Provider
 */
public abstract class SignParamAuthenticatingProvider extends CcSignatureAuthenticatingProvider {

    public SignParamAuthenticatingProvider() {
        super(new SignatureParamTokenProducer());
    }

    public SignParamAuthenticatingProvider(AuthenticationTokenProducer producer) {
        super(producer);
    }

    public SignParamAuthenticatingProvider(AuthenticationTokenProducer producer, CredentialsMatcher credentialsMatcher) {
        super(producer, credentialsMatcher);
    }

    @Override
    protected String sign(CcAppSignatureToken token, CcPrincipal principal) {
        List<String> list = Arrays.asList(token.getRandom(), principal.getSecret(), token.getTimestamp().toString(), token.getKey());
        list = new ArrayList<String>(list);
        list.addAll(toNameValuesList(token.getParams(), SignatureParamTokenProducer.SIGNATURE));
        sort(list);
        return digest(list);
    }


    /**
     * 将param转换为name=v1,v2,v3这样的list
     * @param param
     * @param filterNames 需要过滤的name
     */
    protected List<String> toNameValuesList(Map<String, String[]> param, String... filterNames) {
        if(param == null || param.isEmpty()) {
            return Collections.emptyList();
        }
        Collection<String> excludes = Collections.emptyList();
        if(filterNames != null) {
            excludes = Arrays.asList(filterNames);
        }
        List<String> nameValues = new ArrayList<String>(param.size());
        for(Map.Entry<String, String[]> entry : param.entrySet()) {
            String name = entry.getKey();
            if(excludes.contains(name)) {
                continue;
            }
            String[] values = entry.getValue();
            if(values == null || values.length == 0) {
                nameValues.add(name + "=");
                continue;
            }
            List<String> valueList = new ArrayList<String>(values.length);
            for(String value : values) {
                if(value == null) {
                    valueList.add("");
                } else {
                    valueList.add(value);
                }
            }
            sort(valueList);
            nameValues.add(name + "=" + StringUtils.join(valueList, ","));
        }
        return nameValues;
    }

    private <T extends Comparable<? super T>> void sort(List<T> list) {
        if(list != null && list.size() > 1) {
            Collections.sort(list);
        }
    }
}
