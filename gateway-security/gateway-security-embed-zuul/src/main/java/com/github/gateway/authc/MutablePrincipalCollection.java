package com.github.gateway.authc;

import java.util.Collection;

/**
 * Created by chongdi.yang on 2016/8/6.
 */
public interface MutablePrincipalCollection extends PrincipalCollection {
    /**
     * Adds the given principal to this collection.
     *
     * @param principal the principal to be added.
     * @param providerName the provider this principal came from.
     */
    void add(Object principal, String providerName);

    /**
     * Adds all of the principals in the given collection to this collection.
     *
     * @param principals the principals to be added.
     * @param providerName the provider this principal came from.
     */
    void addAll(Collection principals, String providerName);

    /**
     * Adds all of the principals from the given principal collection to this collection.
     *
     * @param principals the principals to add.
     */
    void addAll(PrincipalCollection principals);

    /**
     * Removes all Principals in this collection.
     */
    void clear();
}
