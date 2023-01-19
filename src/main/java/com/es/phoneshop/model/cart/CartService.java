package com.es.phoneshop.model.cart;

import javax.servlet.http.HttpServletRequest;

public interface CartService {
    Cart getCart(HttpServletRequest request);
    void add(Long productId, int quantity, HttpServletRequest request) throws OutOfStockException;
    void update(Long productId, int quantity, HttpServletRequest request) throws OutOfStockException;
    void delete(Long productId, HttpServletRequest request);
}
