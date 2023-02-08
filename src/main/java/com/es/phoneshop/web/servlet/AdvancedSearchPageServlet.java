package com.es.phoneshop.web.servlet;

import com.es.phoneshop.dao.ArrayListProductDao;
import com.es.phoneshop.dao.ProductDao;
import com.es.phoneshop.enumeration.SearchOption;
import com.es.phoneshop.model.product.Product;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class AdvancedSearchPageServlet extends HttpServlet {
    private ProductDao productDao;
    private static final String JSP_PAGE = "/WEB-INF/pages/advancedSearch.jsp";

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        productDao = ArrayListProductDao.getInstance();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setAttribute("searchOptions", SearchOption.values());
        request.getRequestDispatcher(JSP_PAGE).forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        String description = request.getParameter("description");
        //Hard coding until the checkbox is added
        SearchOption searchOption = SearchOption.ANY_WORD;
        //SearchOption searchOption = SearchOption.valueOf(request.getParameter("searchOption"));

        Map<String, String> errors = new HashMap<>();
        BigDecimal minPrice = setPrice(request, "minPrice", errors);
        BigDecimal maxPrice = setPrice(request, "maxPrice", errors);

        if(errors.isEmpty()) {
            List<Product> products = productDao.findProductsByAdvancedSearch(description, minPrice, maxPrice, searchOption);
            request.setAttribute("products", products);
        } else {
            request.setAttribute("errors", errors);
        }

        request.getRequestDispatcher(JSP_PAGE).forward(request, response);
    }

    private BigDecimal setPrice(HttpServletRequest request, String parameter, Map<String, String> errors) {
        String priceString = request.getParameter(parameter);
        Locale locale = request.getLocale();
        NumberFormat format = NumberFormat.getInstance(locale);
        BigDecimal price;
        try {
            price = BigDecimal.valueOf(format.parse(priceString).doubleValue());
        } catch (ParseException e) {
            if(priceString != null || !priceString.isEmpty()) {
                errors.put(parameter, "Not a number");
            }
            return null;
        }
        return price;
    }
}
