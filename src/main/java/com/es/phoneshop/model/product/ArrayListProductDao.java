package com.es.phoneshop.model.product;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ArrayListProductDao implements ProductDao {
    private static ProductDao instance;

    public static synchronized ProductDao getInstance() {
        if(instance == null) {
            instance = new ArrayListProductDao();
        }
        return instance;
    }
    private long maxId;
    private List<Product> products;

    private ArrayListProductDao() {
        this.maxId = 1L;
        this.products = new ArrayList<>();
    }

    @Override
    public synchronized Product getProduct(Long id) throws ProductNotFoundException {
        if (id == null) {
            throw new ProductNotFoundException(id);
        }

        return products.stream()
                .filter(product -> id.equals(product.getId()))
                .findAny()
                .orElseThrow(new ProductNotFoundException(id));
    }

    @Override
    public synchronized List<Product> findProducts(String query, SortField sortField, SortOrder sortOrder) {
        Stream<Product> unsortedDesiredProductsStream =
                products.stream()
                        .filter(product -> (
                                query == null || query.trim().isEmpty()
                                        || containsAnyQueryWords(product.getDescription().toLowerCase(), query))
                        )
                        .filter(product -> product.getPrice() != null)
                        .filter(product -> product.getStock() > 0);

        List<Product> desiredProducts;
        if (query != null && !(query.trim().isEmpty()) && sortField == null) {
            String[] queryWords = query.toLowerCase().split("\\s+");
            desiredProducts = unsortedDesiredProductsStream.sorted(
                    (product1, product2) ->
                        Double.compare(
                                productSearchRelevance(product2.getDescription().toLowerCase(), queryWords),
                                productSearchRelevance(product1.getDescription().toLowerCase(), queryWords)
                        )
                    )
                    .collect(Collectors.toList());
        } else {
            Comparator<Product> comparator = Comparator.comparing(product -> {
                if (sortField.description == sortField) {
                    return (Comparable) product.getDescription();
                } else {
                    return (Comparable) product.getPrice();
                }
            });

            Stream<Product> sortedDesiredProductsStream;
            if (sortOrder == SortOrder.asc) {
                sortedDesiredProductsStream = unsortedDesiredProductsStream
                        .sorted(comparator);
            } else {
                sortedDesiredProductsStream = unsortedDesiredProductsStream
                        .sorted(comparator.reversed());
            }

            desiredProducts = sortedDesiredProductsStream.collect(Collectors.toList());
        }

        return desiredProducts;
    }

    public boolean containsAnyQueryWords(String description, String query) {
        return Arrays.stream(query.toLowerCase().split("\\s+"))
                .anyMatch(description::contains);
    }

    public double productSearchRelevance(String description, String[] queryWords) {
        return Arrays.stream(queryWords)
                .filter(description::contains)
                .count()
                /
                Double.valueOf(description.split(" ").length);
    }

    @Override
    public synchronized void save(Product product) {
        try {
            Product productToUpdate = getProduct(product.getId());
            products.remove(productToUpdate);
        } catch (ProductNotFoundException e) {
            product.setId(maxId++);
        } finally {
            products.add(product);
        }
    }

    @Override
    public synchronized void delete(Long id) throws ProductNotFoundException {
        Product product = getProduct(id);
        products.remove(product);
    }

    public long getMaxId() {
        return maxId;
    }
}
