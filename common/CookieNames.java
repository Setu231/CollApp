
package io.collapp.common;

public class CookieNames {

    private static String SESSION_COOKIE_NAME = "collapp_SESSION_ID";
    private static String REMEMBER_ME_COOKIE = "collapp_REMEMBER_ME";

    public static final String PROPERTY_NAME = "cookiePrefix";

    public static void updatePrefix(String prefix) {
        SESSION_COOKIE_NAME = prefix + "-collapp_SESSION_ID";
        REMEMBER_ME_COOKIE = prefix + "-collapp_REMEMBER_ME";
    }

    public static String getSessionCookieName() {
        return SESSION_COOKIE_NAME;
    }

    public static String getRememberMeCookieName() {
        return REMEMBER_ME_COOKIE;
    }
}
