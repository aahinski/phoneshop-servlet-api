package com.es.phoneshop.web;

import com.es.phoneshop.model.cart.CartService;
import com.es.phoneshop.model.cart.HttpSessionCartService;
import com.es.phoneshop.model.cart.OutOfStockException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;

public class AddCartItemServlet extends HttpServlet {
    private static final long serialVersionUID = 2812860408953942622L;

    private CartService cartService;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        cartService = HttpSessionCartService.getInstance();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String productInfo = request.getPathInfo().substring(1);
        Long productId = Long.valueOf(productInfo);
        String quantityString = request.getParameter("quantity" + productInfo);

        int quantity;
        try {
            Locale locale = request.getLocale();
            NumberFormat format = NumberFormat.getInstance(locale);
            Double doubleQuantity = format.parse(quantityString).doubleValue();
            quantity = doubleQuantity.intValue();
            if (quantity - doubleQuantity != 0.0) {
                incorrectQuantityError(request, response, "Number should be integer", productId, quantityString);
                return;
            }
        } catch (ParseException e) {
            incorrectQuantityError(request, response, "Not a number", productId, quantityString);
            return;
        }

        if (quantity <= 0) {
            incorrectQuantityError(request, response, "Number should be greater than zero", productId, quantityString);
            return;
        }

        try {
            cartService.add(productId, quantity, request);
        } catch (OutOfStockException e) {
            incorrectQuantityError(request, response, "Out of stock, available " + e.getStock(), productId, quantityString);
            return;
        }

        request.setAttribute("message", "Product added to cart");
        response.sendRedirect(request.getContextPath() + "/products?message=Product added to cart");
    }

    private void incorrectQuantityError(HttpServletRequest request, HttpServletResponse response,
                                        String errorMessage, Long productId, String quantityString) throws IOException {
        request.setAttribute("error", errorMessage);
        request.setAttribute("errorProductId", productId);
        request.setAttribute("errorQuantity", quantityString);
        response.sendRedirect(request.getContextPath() + "/products?error=" + errorMessage + "&errorProductId=" + productId + "&errorQuantity=" + quantityString);
    }
}
