package com.es.phoneshop.model.cart;

import com.es.phoneshop.model.product.ArrayListProductDao;
import com.es.phoneshop.model.product.Product;
import com.es.phoneshop.model.product.ProductDao;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.Currency;

import static org.junit.Assert.assertEquals;

public class HttpSessionCartServiceTest {
    private ProductDao productDao;
    private CartService cartService;
    private Cart cart;

    @Before
    public void setup() {
        this.productDao = ArrayListProductDao.getInstance();
        Currency usd = Currency.getInstance("USD");
        Product product = new Product("test", "Samsung Galaxy S", new BigDecimal(100), usd, 100, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Samsung/Samsung%20Galaxy%20S.jpg");
        productDao.save(product);
        cart = new Cart();

        this.cartService = HttpSessionCartService.getInstance();
    }

    @Test
    public void testAddNewProduct() throws OutOfStockException {
        Product product = productDao.getProduct(1L);
        CartItem expectedCartItem = new CartItem(product, 1);

        cartService.add(cart, product.getId(), 1);

        assertEquals(expectedCartItem, cart.getItems().get(0));
    }

    @Test
    public void testAddExistedProduct() throws OutOfStockException {
        Product product = productDao.getProduct(1L);

        cartService.add(cart, product.getId(), 1);
        cartService.add(cart, product.getId(), 2);

        CartItem expectedCartItem = new CartItem(product, 3);

        assertEquals(expectedCartItem, cart.getItems().get(0));
    }

    @Test(expected = OutOfStockException.class)
    public void testAddProductWithQuantityGreaterThanStock() throws OutOfStockException {
        Product product = productDao.getProduct(1L);

        cartService.add(cart, product.getId(), product.getStock() + 1);
    }
}