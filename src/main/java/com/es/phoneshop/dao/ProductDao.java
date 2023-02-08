package com.es.phoneshop.dao;

import com.es.phoneshop.enumeration.SearchOption;
import com.es.phoneshop.enumeration.SortField;
import com.es.phoneshop.enumeration.SortOrder;
import com.es.phoneshop.exception.ProductNotFoundException;
import com.es.phoneshop.model.product.Product;

import java.math.BigDecimal;
import java.util.List;

public interface ProductDao {
    Product findById(Long id) throws ProductNotFoundException;
    List<Product> findAll();
    List<Product> findProductsByQueryAndSortParameters(String query, SortField sortField, SortOrder sortOrder);
    List<Product> findProductsByAdvancedSearch(String description, BigDecimal minPrice, BigDecimal maxPrice, SearchOption searchOption);
    void save(Product product);
    void delete(Long id) throws ProductNotFoundException;
}
