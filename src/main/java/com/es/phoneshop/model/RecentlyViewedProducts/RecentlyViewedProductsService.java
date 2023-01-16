package com.es.phoneshop.model.RecentlyViewedProducts;

import com.es.phoneshop.model.product.Product;

import javax.servlet.http.HttpServletRequest;
import java.util.Deque;

public interface RecentlyViewedProductsService {
    RecentlyViewedProducts getProducts(HttpServletRequest request);
    void add(Deque<Product> products, Long productId);
}