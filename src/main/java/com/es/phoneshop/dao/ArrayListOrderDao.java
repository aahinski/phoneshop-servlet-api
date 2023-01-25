package com.es.phoneshop.dao;

import com.es.phoneshop.enumeration.SortField;
import com.es.phoneshop.enumeration.SortOrder;
import com.es.phoneshop.exception.OrderNotFoundException;
import com.es.phoneshop.exception.ProductNotFoundException;
import com.es.phoneshop.model.order.Order;
import com.es.phoneshop.model.product.PriceHistory;
import com.es.phoneshop.model.product.Product;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
