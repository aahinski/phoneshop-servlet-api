package com.es.phoneshop.web;

import com.es.phoneshop.model.cart.Cart;
import com.es.phoneshop.model.cart.CartService;
import com.es.phoneshop.model.cart.DefaultCartService;
import com.es.phoneshop.model.cart.OutOfStockException;
import com.es.phoneshop.model.product.ArrayListProductDao;
import com.es.phoneshop.model.product.ProductDao;
import com.es.phoneshop.model.recently_viewed_products.DefaultRecentlyViewedProductsService;
import com.es.phoneshop.model.recently_viewed_products.RecentlyViewedProducts;
import com.es.phoneshop.model.recently_viewed_products.RecentlyViewedProductsService;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;

public class ProductDetailsPageServlet extends HttpServlet {
    private ProductDao productDao;

    private CartService cartService;

    private RecentlyViewedProductsService recentlyViewedProductsService;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        productDao = ArrayListProductDao.getInstance();
        cartService = DefaultCartService.getInstance();
        recentlyViewedProductsService = DefaultRecentlyViewedProductsService.getInstance();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String productIdString = request.getPathInfo().substring(1);
        Long productId = Long.valueOf(productIdString);
        request.setAttribute("product", productDao.getProduct(productId));
        request.setAttribute("cart", cartService.getCart(request));

        RecentlyViewedProducts recentlyViewedProducts = recentlyViewedProductsService.getProducts(request);
        recentlyViewedProductsService.add(recentlyViewedProducts.getProducts(), productId);
        request.setAttribute("recently_viewed", recentlyViewedProducts);

        request.getRequestDispatcher("/WEB-INF/pages/product.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Long productId = parseProductId(request);
        String quantityString = request.getParameter("quantity");

        int quantity;
        try {
            Locale locale = request.getLocale();
            NumberFormat format = NumberFormat.getInstance(locale);
            Double doubleQuantity = format.parse(quantityString).doubleValue();
            quantity = doubleQuantity.intValue();
            if(quantity - doubleQuantity != 0.0) {
                request.setAttribute("error", "Number should be integer");
                doGet(request, response);
                return;
            }
        } catch (ParseException e) {
            request.setAttribute("error", "Not a number");
            doGet(request, response);
            return;
        }

        if (quantity <= 0) {
            request.setAttribute("error", "Number should be greater than zero");
            doGet(request, response);
            return;
        }

        Cart cart = cartService.getCart(request);
        try {
            cartService.add(cart, productId, quantity);
        } catch (OutOfStockException e) {
            request.setAttribute("error", "Out of stock, available " + e.getStock());
            doGet(request, response);
            return;
        }
        request.setAttribute("message", "Product added to cart");

        response.sendRedirect(request.getContextPath() + "/products/" + productId + "?message=Product added to cart");
    }

    private Long parseProductId(HttpServletRequest request) {
        String productInfo = request.getPathInfo().substring(1);
        return Long.valueOf(productInfo);
    }
}
