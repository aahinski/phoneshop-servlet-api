package com.es.phoneshop.dao;

import com.es.phoneshop.exception.OrderNotFoundException;
import com.es.phoneshop.exception.ProductNotFoundException;
import com.es.phoneshop.model.order.Order;

import java.util.ArrayList;
import java.util.List;

public class ArrayListOrderDao implements OrderDao {
    private static OrderDao instance;

    public static synchronized OrderDao getInstance() {
        if (instance == null) {
            instance = new ArrayListOrderDao();
        }
        return instance;
    }

    private long maxId;
    private List<Order> orders;

    private ArrayListOrderDao() {
        this.maxId = 1L;
        this.orders = new ArrayList<>();
    }

    @Override
    public synchronized Order getOrder(Long id) throws OrderNotFoundException {
        if (id == null) {
            throw new ProductNotFoundException(null);
        }

        return orders.stream()
                .filter(order -> id.equals(order.getId()))
                .findAny()
                .orElseThrow(new OrderNotFoundException(id));
    }

    @Override
    public Order getOrderBySecureId(String id) throws OrderNotFoundException {
        if (id == null) {
            throw new OrderNotFoundException(null);
        }

        return orders.stream()
                .filter(order -> id.equals(order.getSecureId()))
                .findAny()
                .orElseThrow(new OrderNotFoundException());
    }

    @Override
    public synchronized void save(Order order) {
        try {
            Order productToOrder = getOrder(order.getId());
            orders.remove(productToOrder);
        } catch (ProductNotFoundException e) {
            order.setId(maxId++);
        } finally {
            orders.add(order);
        }
    }

    public void setMaxId(long maxId) {
        this.maxId = maxId;
    }
}
