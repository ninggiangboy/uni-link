package dev.ngb.app.support;

import org.springframework.http.HttpHeaders;

import java.net.HttpCookie;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Thread-safe cookie jar for integration HTTP clients: merges stored cookies into outbound
 * requests and updates from {@code Set-Cookie} response headers.
 */
public final class SessionCookieStore {

    private final ConcurrentHashMap<String, String> cookies = new ConcurrentHashMap<>();

    public String get(String cookieName) {
        return cookies.get(cookieName);
    }

    public void apply(HttpHeaders requestHeaders) {
        if (cookies.isEmpty() || requestHeaders.getFirst(HttpHeaders.COOKIE) != null) {
            return;
        }
        String cookieValue = cookies.entrySet().stream()
                .map(entry -> entry.getKey() + "=" + entry.getValue())
                .reduce((left, right) -> left + "; " + right)
                .orElse("");
        if (!cookieValue.isEmpty()) {
            requestHeaders.add(HttpHeaders.COOKIE, cookieValue);
        }
    }

    public void captureFrom(HttpHeaders responseHeaders) {
        List<String> setCookies = responseHeaders.get(HttpHeaders.SET_COOKIE);
        if (setCookies == null || setCookies.isEmpty()) {
            return;
        }
        for (String setCookie : setCookies) {
            if (setCookie == null || setCookie.isBlank()) {
                continue;
            }
            try {
                for (HttpCookie cookie : HttpCookie.parse(setCookie)) {
                    applyCookie(cookie);
                }
            } catch (IllegalArgumentException ignored) {
                // malformed Set-Cookie line
            }
        }
    }

    private void applyCookie(HttpCookie cookie) {
        String name = cookie.getName();
        if (name == null || name.isBlank()) {
            return;
        }
        if (cookie.getMaxAge() == 0 || cookie.hasExpired()) {
            cookies.remove(name);
            return;
        }
        String value = cookie.getValue();
        if (value == null || value.isEmpty()) {
            cookies.remove(name);
        } else {
            cookies.put(name, value);
        }
    }
}
