package com.es.phoneshop.exception;

import java.util.function.Supplier;

public class OrderNotFoundException extends RuntimeException implements Supplier<RuntimeException> {
    private Long id;

    public OrderNotFoundException(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    @Override
    public OrderNotFoundException get() {
        return new OrderNotFoundException(getId());
    }
}
