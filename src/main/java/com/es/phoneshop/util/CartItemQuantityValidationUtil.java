package com.es.phoneshop.util;

import java.text.NumberFormat;
import javax.servlet.http.HttpServletRequest;
import java.text.ParseException;
import java.util.Locale;

public class CartItemQuantityValidationUtil {
    public static String errorMessageIfPresentElseQuantity(HttpServletRequest request, String quantityString) {
        int quantity;
        try {
            Locale locale = request.getLocale();
            NumberFormat format = NumberFormat.getInstance(locale);
            Double doubleQuantity = format.parse(quantityString).doubleValue();
            quantity = doubleQuantity.intValue();
            if (quantity - doubleQuantity != 0.0) {
                return "Number should be integer";
            }
        } catch (ParseException e) {
            return "Not a number";
        }

        if (quantity <= 0) {
            return "Number should be greater than zero";
        }

        return String.valueOf(quantity);
    }
}
