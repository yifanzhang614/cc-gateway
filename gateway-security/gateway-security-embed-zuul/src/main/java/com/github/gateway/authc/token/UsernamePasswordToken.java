package com.github.gateway.authc.token;

import com.github.gateway.authc.HostAuthenticationToken;

/**
 * Created by chongdi.yang on 2016/8/6.
 */
public class UsernamePasswordToken implements HostAuthenticationToken {
    /**
     * The username
     */
    private String username;

    /**
     * The password, in char[] format
     */
    private char[] password;


    /**
     * The location from where the login attempt occurs, or <code>null</code> if not known or explicitly
     * omitted.
     */
    private String host;

    /*--------------------------------------------
    |         C O N S T R U C T O R S           |
    ============================================*/

    /**
     * JavaBeans compatible no-arg constructor.
     */
    public UsernamePasswordToken() {
    }

    /**
     * Constructs a new UsernamePasswordToken encapsulating the username and password submitted
     * during an authentication attempt, with a <tt>null</tt> {@link #getHost() host} and a
     * <tt>rememberMe</tt> default of <tt>false</tt>.
     *
     * @param username the username submitted for authentication
     * @param password the password character array submitted for authentication
     */
    public UsernamePasswordToken(final String username, final char[] password) {
        this(username, password, null);
    }

    /**
     * Constructs a new UsernamePasswordToken encapsulating the username and password submitted
     * during an authentication attempt, with a <tt>null</tt> {@link #getHost() host} and
     * a <tt>rememberMe</tt> default of <tt>false</tt>
     * <p/>
     * <p>This is a convience constructor and maintains the password internally via a character
     * array, i.e. <tt>password.toCharArray();</tt>.  Note that storing a password as a String
     * in your code could have possible security implications as noted in the class JavaDoc.</p>
     *
     * @param username the username submitted for authentication
     * @param password the password string submitted for authentication
     */
    public UsernamePasswordToken(final String username, final String password) {
        this(username, password != null ? password.toCharArray() : null, null);
    }


    /**
     * Constructs a new UsernamePasswordToken encapsulating the username and password submitted, the
     * inetAddress from where the attempt is occurring, and a default <tt>rememberMe</tt> value of <tt>false</tt>
     * <p/>
     * <p>This is a convience constructor and maintains the password internally via a character
     * array, i.e. <tt>password.toCharArray();</tt>.  Note that storing a password as a String
     * in your code could have possible security implications as noted in the class JavaDoc.</p>
     *
     * @param username the username submitted for authentication
     * @param password the password string submitted for authentication
     * @param host     the host name or IP string from where the attempt is occuring
     * @since 1.0
     */
    public UsernamePasswordToken(final String username, final String password, final String host) {
        this(username, password != null ? password.toCharArray() : null, host);
    }



    /**
     * Constructs a new UsernamePasswordToken encapsulating the username and password submitted, if the user
     * wishes their identity to be remembered across sessions, and the inetAddress from where the attempt is ocurring.
     *
     * @param username   the username submitted for authentication
     * @param password   the password character array submitted for authentication
     * @param host       the host name or IP string from where the attempt is occuring
     * @since 1.0
     */
    public UsernamePasswordToken(final String username, final char[] password, final String host) {
        this.username = username;
        this.password = password;
        this.host = host;
    }


    /*--------------------------------------------
    |  A C C E S S O R S / M O D I F I E R S    |
    ============================================*/

    /**
     * Returns the username submitted during an authentication attempt.
     *
     * @return the username submitted during an authentication attempt.
     */
    public String getUsername() {
        return username;
    }

    /**
     * Sets the username for submission during an authentication attempt.
     *
     * @param username the username to be used for submission during an authentication attempt.
     */
    public void setUsername(String username) {
        this.username = username;
    }


    /**
     * Returns the password submitted during an authentication attempt as a character array.
     *
     * @return the password submitted during an authentication attempt as a character array.
     */
    public char[] getPassword() {
        return password;
    }

    /**
     * Sets the password for submission during an authentication attempt.
     *
     * @param password the password to be used for submission during an authentication attemp.
     */
    public void setPassword(char[] password) {
        this.password = password;
    }

    /**
     * Simply returns {@link #getUsername() getUsername()}.
     *
     * @return the {@link #getUsername() username}.

     */
    public Object getPrincipal() {
        return getUsername();
    }

    /**
     * Returns the {@link #getPassword() password} char array.
     *
     * @return the {@link #getPassword() password} char array.
     */
    public Object getCredentials() {
        return getPassword();
    }

    /**
     * Returns the host name or IP string from where the authentication attempt occurs.  May be <tt>null</tt> if the
     * host name/IP is unknown or explicitly omitted.  It is up to the Authenticator implementation processing this
     * token if an authentication attempt without a host is valid or not.
     * <p/>
     * <p>(Shiro's default Authenticator allows <tt>null</tt> hosts to support localhost and proxy server environments).</p>
     *
     * @return the host from where the authentication attempt occurs, or <tt>null</tt> if it is unknown or
     *         explicitly omitted.
     * @since 1.0
     */
    public String getHost() {
        return host;
    }

    /**
     * Sets the host name or IP string from where the authentication attempt occurs.  It is up to the Authenticator
     * implementation processing this token if an authentication attempt without a host is valid or not.
     * <p/>
     * <p>(Shiro's default Authenticator
     * allows <tt>null</tt> hosts to allow localhost and proxy server environments).</p>
     *
     * @param host the host name or IP string from where the attempt is occuring
     * @since 1.0
     */
    public void setHost(String host) {
        this.host = host;
    }


    /*--------------------------------------------
    |               M E T H O D S               |
    ============================================*/

    /**
     * Clears out (nulls) the username, password, rememberMe, and inetAddress.  The password bytes are explicitly set to
     * <tt>0x00</tt> before nulling to eliminate the possibility of memory access at a later time.
     */
    public void clear() {
        this.username = null;
        this.host = null;
        if (this.password != null) {
            for (int i = 0; i < password.length; i++) {
                this.password[i] = 0x00;
            }
            this.password = null;
        }

    }

    /**
     * Returns the String representation.  It does not include the password in the resulting
     * string for security reasons to prevent accidentially printing out a password
     * that might be widely viewable).
     *
     * @return the String representation of the <tt>UsernamePasswordToken</tt>, omitting
     *         the password.
     */
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getName());
        sb.append(" - ");
        sb.append(username);
        if (host != null) {
            sb.append(" (").append(host).append(")");
        }
        return sb.toString();
    }
}
