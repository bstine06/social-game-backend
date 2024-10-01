package com.brettstine.social_game_backend.utils;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class CookieUtil {

  public static void setHttpCookie(HttpServletResponse response, String key, String value, int maxAgeInSeconds) {
    Cookie cookie = new Cookie(key, value);
    cookie.setHttpOnly(true);
    cookie.setPath("/");
    cookie.setMaxAge(maxAgeInSeconds); // 1 hour
    cookie.setSecure(false); // Use this line for HTTP requests only
    response.addCookie(cookie); 
  }

  public static String getDataFromCookie(HttpServletRequest request, String key) {
    Cookie[] cookies = request.getCookies();
    if (cookies != null) {
        for (Cookie cookie : cookies) {
            if (key.equals(cookie.getName())) {
                return cookie.getValue();
            }
        }
    }
    return null;
  }

  public static void deleteCookie(HttpServletResponse response, String cookieName) {
        Cookie cookie = new Cookie(cookieName, null); // Create a cookie with the same name
        cookie.setPath("/"); // Set the path to the same as when the cookie was created
        cookie.setMaxAge(0); // Set the max age to 0 to delete it
        response.addCookie(cookie); // Add the cookie to the response
  }

}