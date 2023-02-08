package com.es.phoneshop.dao;

import com.es.phoneshop.enumeration.SearchOption;
import com.es.phoneshop.enumeration.SortField;
import com.es.phoneshop.enumeration.SortOrder;
import com.es.phoneshop.exception.ProductNotFoundException;
import com.es.phoneshop.model.product.PriceHistory;
import com.es.phoneshop.model.product.Product;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ArrayListProductDao extends GenericDao<Product> implements ProductDao {
    private static ProductDao instance;

    public static synchronized ProductDao getInstance() {
        if (instance == null) {
            instance = new ArrayListProductDao();
        }
        return instance;
    }

    private ArrayListProductDao() {

    }

    @Override
    public synchronized List<Product> findProductsByQueryAndSortParameters(String query, SortField sortField, SortOrder sortOrder) {
        Stream<Product> unsortedDesiredProductsStream =
                items.stream()
                        .filter(product -> (
                                query == null || query.trim().isEmpty()
                                        || containsAnyQueryWords(product.getDescription().toLowerCase(), query))
                        )
                        .filter(product -> product.getPrice() != null)
                        .filter(product -> product.getStock() > 0);

        Comparator<Product> productComparator;
        if (sortField == null) {
            if (query != null && !query.trim().isEmpty()) {
                productComparator = sortByRelevance(query);
            } else {
                /* sorting by default */
                productComparator = sortBySortOrderAndSortField(SortField.price, SortOrder.desc);
            }
        } else {
            productComparator = sortBySortOrderAndSortField(sortField, sortOrder);
        }

        return unsortedDesiredProductsStream.sorted(productComparator).collect(Collectors.toList());
    }

    @Override
    public List<Product> findProductsByAdvancedSearch(String description, BigDecimal minPrice, BigDecimal maxPrice, SearchOption searchOption) {
        List<Product> products = getProductsWithPriceInRange(minPrice, maxPrice);
        if (description == null || description.isEmpty()) {
            return products;
        } else {
            switch (searchOption) {
                case ANY_WORD:
                    return products.stream()
                            .filter(product -> (containsAnyQueryWords(product.getDescription().toLowerCase(), description)))
                            .collect(Collectors.toList());
                case ALL_WORDS:
                    return products.stream()
                            .filter(product -> (containsEveryQueryWord(product.getDescription().toLowerCase(), description)))
                            .collect(Collectors.toList());
            }
        }
        return null;
    }

    private List<Product> getProductsWithPriceInRange(BigDecimal minPrice, BigDecimal maxPrice) {
        return items.stream()
                .filter(item -> item.getPrice().compareTo(minPrice) > 0 && item.getPrice().compareTo(maxPrice) < 0)
                .collect(Collectors.toList());
    }

    private Comparator<Product> sortByRelevance(String query) {
        String[] queryWords = query.toLowerCase().split("\\s+");
        Comparator<Product> comparator = Comparator.comparing(product ->
                productSearchRelevance(product.getDescription().toLowerCase(), queryWords));
        return comparator.reversed();
    }

    private Comparator<Product> sortBySortOrderAndSortField(SortField sortField, SortOrder sortOrder) {
        Comparator<Product> productComparator = Comparator.comparing(product -> {
            switch (sortField) {
                case description:
                    return (Comparable) product.getDescription();
                case price:
                    return (Comparable) product.getPrice();
            }
            return null;
        });

        Comparator<Product> comparator = null;
        switch (sortOrder) {
            case asc:
                comparator = productComparator;
                break;
            case desc:
                comparator = productComparator.reversed();
                break;
        }

        return comparator;
    }

    private boolean containsAnyQueryWords(String description, String query) {
        return Arrays.stream(query.toLowerCase().split("\\s+"))
                .anyMatch(description::contains);
    }

    private boolean containsEveryQueryWord(String description, String query) {
        return Arrays.stream(query.toLowerCase().split("\\s+"))
                .allMatch(description::contains);
    }

    private double productSearchRelevance(String description, String[] queryWords) {
        return Arrays.stream(queryWords)
                .filter(description::contains)
                .count()
                /
                Double.valueOf(description.split(" ").length);
    }

    @Override
    protected Supplier<? extends NoSuchElementException> getItemNotFoundExceptionSupplier(Long id) {
        ProductNotFoundException exception = new ProductNotFoundException(id);
        return exception::get;
    }

    @Override
    public synchronized void save(Product product) {
        try {
            Product productToUpdate = findById(product.getId());
            product.setPriceHistoryList(productToUpdate.getPriceHistoryList());
            if (product.getPriceHistoryList()
                    .stream()
                    .noneMatch(p -> Objects.equals(p.getPrice(), product.getPrice()))) {
                product.getPriceHistoryList()
                        .add(new PriceHistory(LocalDateTime.now(), product.getPrice(), product.getCurrency()));
            }
            items.remove(productToUpdate);
        } catch (ProductNotFoundException e) {
            product.setId(maxId++);
        } finally {
            items.add(product);
        }
    }
}
