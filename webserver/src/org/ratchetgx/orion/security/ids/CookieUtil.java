package org.ratchetgx.orion.security.ids;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author <a href="mailto:ljw79618@hotmail.com">L.J.W</a>
 */
public class CookieUtil {
    private static Logger log = LoggerFactory.getLogger(CookieUtil.class);

    public static void invalidateCookie(HttpServletResponse response, String cookieName, String domain, String path) {
        setCookie(response, cookieName, null, domain, 0, path);
    }

    public static void invalidateCookie(HttpServletResponse response, String cookieName, String domain) {
        invalidateCookie(response, cookieName, domain, "/");
    }

    public static Cookie getCookie(HttpServletRequest request, String name) {
        Cookie cookies[] = request.getCookies();
        if (cookies == null || name == null || name.length() == 0) {
            return null;
        }
        for (int i = 0; i < cookies.length; i++) {
            if (cookies[i].getName().equals(name)) {
                return cookies[i];
            }
        }

        return null;
    }

    public static String getCookieValue(HttpServletRequest request, String name) {
        Cookie cookie = getCookie(request, name);
        if (cookie != null) {
            return cookie.getValue();
        } else {
            return null;
        }
    }

    public static Cookie setCookie(HttpServletResponse response, String name, String value, String domain,
            int maxAge, String path) {
        Cookie cookie = new Cookie(name, value);
        cookie.setDomain(domain);
        cookie.setMaxAge(maxAge);
        cookie.setPath(path);
        response.addCookie(cookie);
        return cookie;
    }

    public static String getDefaultCookiePath(HttpServletRequest request) {
        String path = request.getContextPath();
        if (path == null || path.equals("")) {
            return "/";
        }
        if (!path.startsWith("/")) {
            return "/" + path;
        } else {
            return path;
        }
    }

    public static String getDefaultCookieDomain(HttpServletRequest request) {
        String serverName = request.getServerName();
        log.info("serverName=" + serverName);
        if (serverName.indexOf(".") >= 0) {
            return serverName.substring(serverName.indexOf('.'));
        } else {
            return serverName;
        }
    }
}
