package com.es.phoneshop.model.recently_viewed_products;

import com.es.phoneshop.model.product.Product;

import java.util.Deque;
import java.util.LinkedList;


public class RecentlyViewedProducts {
    private Deque<Product> products;

    public RecentlyViewedProducts() {
        this.products = new LinkedList<>();
    }

    public Deque<Product> getProducts() {
        return products;
    }

    @Override
    public String toString() {
        return "Recently viewed products:" + products;
    }
}
