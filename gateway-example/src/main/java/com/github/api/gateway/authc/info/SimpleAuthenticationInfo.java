package com.github.api.gateway.authc.info;

import com.github.api.gateway.authc.*;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by chongdi.yang on 2016/8/6.
 */


public class SimpleAuthenticationInfo implements MergableAuthenticationInfo {

    /**
     * The principals identifying the account associated with this AuthenticationInfo instance.
     */
    protected PrincipalCollection principals;
    /**
     * The credentials verifying the account principals.
     */
    protected Object credentials;


    /**
     * Default no-argument constructor.
     */
    public SimpleAuthenticationInfo() {
    }

    /**
     * Constructor that takes in a single 'primary' principal of the account and its corresponding credentials,
     * associated with the specified realm.
     * <p/>
     * This is a convenience constructor and will construct a {@link PrincipalCollection PrincipalCollection} based
     * on the {@code principal} and {@code realmName} argument.
     *
     * @param principal   the 'primary' principal associated with the specified realm.
     * @param credentials the credentials that verify the given principal.
     * @param providerName
     */
    public SimpleAuthenticationInfo(Object principal, Object credentials, String providerName) {
        if(principal instanceof PrincipalCollection) {
            this.principals = (PrincipalCollection) principal;
        } else {
            this.principals = new SimplePrincipalCollection(principal, providerName);
        }
        this.credentials = credentials;
    }


    /**
     * Constructor that takes in an account's identifying principal(s) and its corresponding credentials that verify
     * the principals.
     *
     * @param principals  a Realm's account's identifying principal(s)
     * @param credentials the accounts corresponding principals that verify the principals.
     */
    public SimpleAuthenticationInfo(PrincipalCollection principals, Object credentials) {
        this.principals = new SimplePrincipalCollection(principals);
        this.credentials = credentials;
    }



    public PrincipalCollection getPrincipals() {
        return principals;
    }

    /**
     * Sets the identifying principal(s) represented by this instance.
     *
     * @param principals the indentifying attributes of the corresponding Realm account.
     */
    public void setPrincipals(PrincipalCollection principals) {
        this.principals = principals;
    }

    public Object getCredentials() {
        return credentials;
    }

    /**
     * Sets the credentials that verify the principals/identity of the associated Realm account.
     *
     * @param credentials attribute(s) that verify the account's identity/principals, such as a password or private key.
     */
    public void setCredentials(Object credentials) {
        this.credentials = credentials;
    }



    /**
     * Takes the specified <code>info</code> argument and adds its principals and credentials into this instance.
     *
     * @param info the <code>AuthenticationInfo</code> to add into this instance.
     */
    @SuppressWarnings("unchecked")
    public void merge(AuthenticationInfo info) {
        if (info == null || info.getPrincipals() == null || info.getPrincipals().isEmpty()) {
            return;
        }

        if (this.principals == null) {
            this.principals = info.getPrincipals();
        } else {
            if (!(this.principals instanceof MutablePrincipalCollection)) {
                this.principals = new SimplePrincipalCollection(this.principals);
            }
            ((MutablePrincipalCollection) this.principals).addAll(info.getPrincipals());
        }

        Object thisCredentials = getCredentials();
        Object otherCredentials = info.getCredentials();

        if (otherCredentials == null) {
            return;
        }

        if (thisCredentials == null) {
            this.credentials = otherCredentials;
            return;
        }

        if (!(thisCredentials instanceof Collection)) {
            Set newSet = new HashSet();
            newSet.add(thisCredentials);
            setCredentials(newSet);
        }

        // At this point, the credentials should be a collection
        Collection credentialCollection = (Collection) getCredentials();
        if (otherCredentials instanceof Collection) {
            credentialCollection.addAll((Collection) otherCredentials);
        } else {
            credentialCollection.add(otherCredentials);
        }
    }

    /**
     * Returns <code>true</code> if the Object argument is an <code>instanceof SimpleAuthenticationInfo</code> and
     * its {@link #getPrincipals() principals} are equal to this instance's principals, <code>false</code> otherwise.
     *
     * @param o the object to compare for equality.
     * @return <code>true</code> if the Object argument is an <code>instanceof SimpleAuthenticationInfo</code> and
     *         its {@link #getPrincipals() principals} are equal to this instance's principals, <code>false</code> otherwise.
     */
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SimpleAuthenticationInfo)) return false;

        SimpleAuthenticationInfo that = (SimpleAuthenticationInfo) o;

        //noinspection RedundantIfStatement
        if (principals != null ? !principals.equals(that.principals) : that.principals != null) return false;

        return true;
    }

    /**
     * Returns the hashcode of the internal {@link #getPrincipals() principals} instance.
     *
     * @return the hashcode of the internal {@link #getPrincipals() principals} instance.
     */
    public int hashCode() {
        return (principals != null ? principals.hashCode() : 0);
    }

    /**
     * Simple implementation that merely returns <code>{@link #getPrincipals() principals}.toString()</code>
     *
     * @return <code>{@link #getPrincipals() principals}.toString()</code>
     */
    public String toString() {
        return principals != null ? principals.toString() : null;
    }

}
