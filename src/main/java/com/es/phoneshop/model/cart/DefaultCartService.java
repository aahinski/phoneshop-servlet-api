package com.es.phoneshop.model.cart;

import com.es.phoneshop.model.product.ArrayListProductDao;
import com.es.phoneshop.model.product.Product;
import com.es.phoneshop.model.product.ProductDao;

public class DefaultCartService implements CartService {
    private Cart cart = new Cart();

    private ProductDao productDao;

    private DefaultCartService() {
        productDao = ArrayListProductDao.getInstance();
    }

    private static CartService instance;

    public static synchronized CartService getInstance() {
        if(instance == null) {
            instance = new DefaultCartService();
        }
        return instance;
    }
    @Override
    public Cart getCart() {
        return cart;
    }

    @Override
    public void add(Long productId, int quantity) throws OutOfStockException {
        Product product = productDao.getProduct(productId);
        CartItem cartItem = cart.getItems()
                .stream()
                .filter(item -> item.getProduct().equals(product))
                .findAny()
                .orElse(null);

        if (cartItem != null) {
            addExistedInCartProduct(product, cartItem, quantity);
        } else {
            addNonExistedInCartProduct(product, quantity);
        }
    }

    private void addExistedInCartProduct(Product product, CartItem cartItem, int quantity) throws OutOfStockException {
        quantity += cartItem.getQuantity();
        checkIfQuantityGreaterThanStock(product, quantity);
        cartItem.setQuantity(quantity);
    }

    private void addNonExistedInCartProduct(Product product, int quantity) throws OutOfStockException {
        checkIfQuantityGreaterThanStock(product, quantity);
        cart.getItems().add(new CartItem(product, quantity));
    }

    private void checkIfQuantityGreaterThanStock(Product product, int quantity) throws OutOfStockException {
        if (product.getStock() < quantity) {
            throw new OutOfStockException(product, quantity, product.getStock());
        }
    }
}
