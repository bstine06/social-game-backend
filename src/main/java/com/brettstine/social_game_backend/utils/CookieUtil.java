package com.brettstine.social_game_backend.utils;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class CookieUtil {

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