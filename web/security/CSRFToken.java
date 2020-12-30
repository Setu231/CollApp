
package io.collapp.web.security;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public final class CSRFToken {

    private static final String CSRF_TOKEN = CSRFToken.class.getName() + ".CSRF_TOKEN";

    private CSRFToken() {
    }

    public static String getToken(HttpServletRequest req) {
        return (String) req.getSession().getAttribute(CSRFToken.CSRF_TOKEN);
    }

    public static void setToken(HttpServletRequest req, String token) {
        setToken(req.getSession(), token);
    }

    public static void setToken(HttpSession session, String token) {
        session.setAttribute(CSRFToken.CSRF_TOKEN, token);
    }
}
