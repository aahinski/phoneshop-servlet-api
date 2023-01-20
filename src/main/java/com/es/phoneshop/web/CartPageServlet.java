package com.es.phoneshop.web;

import com.es.phoneshop.model.cart.CartService;
import com.es.phoneshop.model.cart.HttpSessionCartService;
import com.es.phoneshop.model.cart.OutOfStockException;
import com.es.phoneshop.model.product.ArrayListProductDao;
import com.es.phoneshop.model.product.ProductDao;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class CartPageServlet extends HttpServlet {

    private static final long serialVersionUID = 8174769907867467091L;
    private ProductDao productDao;

    private CartService cartService;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        productDao = ArrayListProductDao.getInstance();
        cartService = HttpSessionCartService.getInstance();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setAttribute("cart", cartService.getCart(request));

        request.getRequestDispatcher("/WEB-INF/pages/cart.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        String[] productIds = request.getParameterValues("productId");
        String[] quantities = request.getParameterValues("quantity");

        Map<Long, String> errors = new HashMap<>();
        for (int i = 0; i < productIds.length; i++) {
            Long productId = Long.parseLong(productIds[i]);
            String quantityString = quantities[i];

            int quantity;
            try {
                Locale locale = request.getLocale();
                NumberFormat format = NumberFormat.getInstance(locale);
                Double doubleQuantity = format.parse(quantityString).doubleValue();
                quantity = doubleQuantity.intValue();
                if (quantity - doubleQuantity != 0.0) {
                    errors.put(productId, "Number should be integer");
                }
            } catch (ParseException e) {
                errors.put(productId, "Not a number");
                break;
            }

            if (quantity <= 0) {
                errors.put(productId, "Number should be greater than zero");
                break;
            }

            try {
                cartService.update(productId, quantity, request);
            } catch (OutOfStockException e) {
                errors.put(productId, "Out of stock, available " + e.getStock());
            }
        }

        if (errors.isEmpty()) {
            request.setAttribute("message", "Product added to card");
            response.sendRedirect(request.getContextPath() + "/cart?message=Cart is updated");
        } else {
            request.setAttribute("errors", errors);
            doGet(request, response);
        }
    }
}
