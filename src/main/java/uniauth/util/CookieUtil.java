package uniauth.util;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;

public class CookieUtil {

    final static private int tokenExpTimeInDay = 10 * 24 * 3600;

    private static void addCookie(HttpServletResponse response, int expiry, Cookie cookie, String domain) {
        cookie.setMaxAge(expiry);
        cookie.setPath("/");
        cookie.setDomain(domain);
        response.addCookie(cookie);
    }

    public static void delJWTCookie(HttpServletResponse response, String domain) {
        Cookie cookie = new Cookie("jwt", null);
        addCookie(response, 0, cookie, domain);
    }

    static public void setJWTCookie(HttpServletResponse response, String token, String domain) {

        var cookie = new Cookie("jwt", token);
        cookie.setHttpOnly(true);
        addCookie(response, tokenExpTimeInDay, cookie, domain);
    }
}
