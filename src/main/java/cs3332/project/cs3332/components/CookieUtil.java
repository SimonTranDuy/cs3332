package cs3332.project.cs3332.components;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

public class CookieUtil {

    private static final String COOKIE_NAME = "registrationList";

    public static List<String> getRegistrationListFromCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        List<String> registrationList = new ArrayList<>();

        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (COOKIE_NAME.equals(cookie.getName())) {
                    String[] encodedValues = cookie.getValue().split(",");
                    for (String encodedValue : encodedValues) {
                        String decodedValue = new String(Base64.getDecoder().decode(encodedValue)); // Giải mã
                        registrationList.add(decodedValue);
                    }
                }
            }
        }

        return registrationList;
    }

    public static void setRegistrationListCookie(List<String> registrationList, HttpServletResponse response) {
        StringBuilder encodedValue = new StringBuilder();

        for (String value : registrationList) {
            if (encodedValue.length() > 0) {
                encodedValue.append(","); // Ngăn cách giữa các giá trị đã mã hóa
            }
            encodedValue.append(Base64.getEncoder().encodeToString(value.getBytes())); // Mã hóa giá trị
        }

        Cookie cookie = new Cookie(COOKIE_NAME, encodedValue.toString());
        cookie.setPath("/");
        cookie.setMaxAge(7 * 24 * 60 * 60); // 7 ngày
        response.addCookie(cookie);
    }

    public static void clearRegistrationListCookie(HttpServletResponse response) {
        Cookie cookie = new Cookie(COOKIE_NAME, null);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        response.addCookie(cookie);
    }
}