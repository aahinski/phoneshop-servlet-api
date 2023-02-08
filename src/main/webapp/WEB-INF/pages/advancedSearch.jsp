<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<script src="${pageContext.servletContext.contextPath}/js/main.js"></script>

<tags:master pageTitle="Advanced product search">
    <h1>
        Products
    </h1>
    <form action="${pageContext.servletContext.contextPath}/advancedSearch" method="post" >
        Description
        <input name="description" value="${param.description}">

        Min price
        <c:set var="error" value="${errors[minPrice]}" />
        <c:if test="${not empty error}">
            <div class="error">
                    ${error}
            </div>
        </c:if>
        <input name="minPrice" value="${param.minPrice}">

        Max price
        <c:set var="error" value="${errors[maxPrice]}" />
        <c:if test="${not empty error}">
            <div class="error">
                    ${error}
            </div>
        </c:if>
        <input name="maxPrice" value="${param.maxPrice}">

        <button>Search</button>
    </form>

    <table>
        <c:forEach var="product" items="${products}">
            <tr>
                <td>
                    <img class="product-tile" src="https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/${product.imageUrl}">
                </td>
                <td>
                    <a href="${pageContext.servletContext.contextPath}/products/${product.id}">
                            ${product.description}
                    </a>
                </td>
                <td class="price">
                    <a href="${pageContext.servletContext.contextPath}/products/price-history/${product.id}">
                        <fmt:formatNumber value="${product.price}" type="currency" currencySymbol="${product.currency.symbol}"/>
                    </a>
                </td>
            </tr>
        </c:forEach>
    </table>
</tags:master>
