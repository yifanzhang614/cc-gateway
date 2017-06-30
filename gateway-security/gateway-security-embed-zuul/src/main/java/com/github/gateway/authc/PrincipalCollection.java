package com.github.gateway.authc;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * Created by chongdi.yang on 2016/8/6.
 */
public interface PrincipalCollection extends Iterable<Object>, Serializable {

    /**
     * 主要的凭证
     * @return
     */
    Object getPrimaryPrincipal();


    /**
     * 把凭证转换为List
     * @return
     */
    List<?> asList();


    /**
     * As set
     * @return
     */
    Set<?> asSet();


    Collection<?> fromProvider(String providerName);


    Set<String> getProviderNames();

    /**
     * Returns {@code true} if this collection is empty, {@code false} otherwise.
     *
     * @return {@code true} if this collection is empty, {@code false} otherwise.
     */
    boolean isEmpty();
}
