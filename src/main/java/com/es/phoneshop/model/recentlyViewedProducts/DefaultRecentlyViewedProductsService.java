package com.es.phoneshop.model.recentlyViewedProducts;

import com.es.phoneshop.model.product.ArrayListProductDao;
import com.es.phoneshop.model.product.Product;
import com.es.phoneshop.model.product.ProductDao;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Deque;

public class DefaultRecentlyViewedProductsService implements RecentlyViewedProductsService {
    private static final int RECENTLY_VIEWED_PRODUCTS_QUANTITY = 3;

    private static final String RECENTLY_VIEWED_PRODUCTS_SESSION_ATTRIBUTE =
            DefaultRecentlyViewedProductsService.class.getName() + ".recently_viewed_products";

    private ProductDao productDao;

    private DefaultRecentlyViewedProductsService() {
        productDao = ArrayListProductDao.getInstance();
    }

    private static RecentlyViewedProductsService instance;

    public static synchronized RecentlyViewedProductsService getInstance() {
        if (instance == null) {
            instance = new DefaultRecentlyViewedProductsService();
        }
        return instance;
    }

    @Override
    public RecentlyViewedProducts getProducts(HttpServletRequest request) {
        HttpSession session = request.getSession();

        synchronized (session) {
            RecentlyViewedProducts recentlyViewedProducts =
                    (RecentlyViewedProducts) request.getSession()
                            .getAttribute(RECENTLY_VIEWED_PRODUCTS_SESSION_ATTRIBUTE);

            if (recentlyViewedProducts == null) {
                recentlyViewedProducts = new RecentlyViewedProducts();
                request.getSession()
                        .setAttribute(RECENTLY_VIEWED_PRODUCTS_SESSION_ATTRIBUTE, recentlyViewedProducts);
            }

            return recentlyViewedProducts;
        }
    }

    @Override
    public void add(Deque<Product> products, Long productId, HttpServletRequest request) {
        HttpSession session = request.getSession();

        synchronized (session) {
            Product product = productDao.getProduct(productId);

            products.remove(product);
            if (products.size() == RECENTLY_VIEWED_PRODUCTS_QUANTITY) {
                products.pollLast();
            }
            products.push(product);
        }
    }
}
