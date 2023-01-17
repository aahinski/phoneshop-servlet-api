package com.es.phoneshop.model.recentlyViewedProducts;

import com.es.phoneshop.model.product.ArrayListProductDao;
import com.es.phoneshop.model.product.Product;
import com.es.phoneshop.model.product.ProductDao;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Deque;

import static org.junit.Assert.assertEquals;

public class DefaultRecentlyViewedProductsServiceTest {
    private ProductDao productDao;
    private RecentlyViewedProductsService recentlyViewedProductsService;
    private RecentlyViewedProducts recentlyViewedProducts;

    @Before
    public void setup() {
        this.productDao = ArrayListProductDao.getInstance();
        Currency usd = Currency.getInstance("USD");

        Product product1 = new Product("test1", "Samsung Galaxy S", new BigDecimal(100), usd, 100, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Samsung/Samsung%20Galaxy%20S.jpg");
        productDao.save(product1);
        Product product2 = new Product("test2", "Samsung Galaxy S", new BigDecimal(100), usd, 100, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Samsung/Samsung%20Galaxy%20S.jpg");
        productDao.save(product2);
        Product product3 = new Product("test3", "Samsung Galaxy S", new BigDecimal(100), usd, 100, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Samsung/Samsung%20Galaxy%20S.jpg");
        productDao.save(product3);
        Product product4 = new Product("test4", "Samsung Galaxy S", new BigDecimal(100), usd, 100, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Samsung/Samsung%20Galaxy%20S.jpg");
        productDao.save(product4);

        recentlyViewedProducts = new RecentlyViewedProducts();

        this.recentlyViewedProductsService = DefaultRecentlyViewedProductsService.getInstance();

        Deque<Product> recentlyViewedProductsDeque = recentlyViewedProducts.getProducts();

        recentlyViewedProductsService.add(recentlyViewedProductsDeque, 1L);
        recentlyViewedProductsService.add(recentlyViewedProductsDeque, 2L);
        recentlyViewedProductsService.add(recentlyViewedProductsDeque, 3L);
    }

    @Test
    public void testAddExistedProduct() {
        Product product = productDao.getProduct(1L);

        Deque<Product> recentlyViewedProductsDeque = recentlyViewedProducts.getProducts();
        recentlyViewedProductsService.add(recentlyViewedProductsDeque, 1L);

        assertEquals(product, recentlyViewedProductsDeque.peek());
    }

    @Test
    public void testAddFourthProduct() {
        Product fourthProduct = productDao.getProduct(4L);

        Deque<Product> recentlyViewedProductsDeque = recentlyViewedProducts.getProducts();
        recentlyViewedProductsService.add(recentlyViewedProductsDeque, 4L);

        assertEquals(fourthProduct, recentlyViewedProductsDeque.peek());
        assertEquals(3, recentlyViewedProductsDeque.size());
    }
}
