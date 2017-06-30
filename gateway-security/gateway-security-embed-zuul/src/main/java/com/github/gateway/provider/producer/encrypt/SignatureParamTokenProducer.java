package com.github.gateway.provider.producer.encrypt;

import com.github.gateway.authc.AuthenticationToken;
import com.github.gateway.authc.token.CcAppSignatureToken;
import com.github.gateway.env.ContentType;
import com.github.gateway.provider.producer.CcSignatureTokenProducer;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;


/**
 * 对所有提交参数进行签名
 */
public class SignatureParamTokenProducer extends CcSignatureTokenProducer {


    @Override
    public AuthenticationToken produce(Object any) {
        HttpServletRequest request = (HttpServletRequest) any;
        CcAppSignatureToken token = (CcAppSignatureToken) super.produce(any);
        String contentType = request.getContentType();
        //对不同类型的提交方式取提交参数内容
        ContentType type = ContentType.of(contentType, null);
        if (contentType != null) {
            // Form表单 提交
            if (type.isFormUrlencoded()) {
                token.setParams(request.getParameterMap());
                // json格式提交
            } else if (type.isApplicationJson()) {

                //文件类型提交
            } else if (type.isFormData()) {

                // xml方法提交
            } else if (type.isTextXml()) {

            }
        } else {
            token.setParams(Collections.<String, String[]>emptyMap());
        }
        return token;
    }

}
