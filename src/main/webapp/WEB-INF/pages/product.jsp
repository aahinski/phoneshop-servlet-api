<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<jsp:useBean id="product" type="com.es.phoneshop.model.product.Product" scope="request"/>
<jsp:useBean id="cart" type="com.es.phoneshop.model.cart.Cart" scope="request"/>
<jsp:useBean id="recently_viewed" type="com.es.phoneshop.model.recently_viewed_products.RecentlyViewedProducts"
             scope="request"/>

<tags:master pageTitle="Product Details">
    <c:if test="${cart.items.size() > 0}">
        <div>
            <h3>
                Cart
            </h3>
            <tags:cart cart="${cart}"/>
        </div>
    </c:if>

    <c:if test="${not empty param.message}">
        <div class="success">
                ${param.message}
        </div>
    </c:if>
    <c:if test="${not empty error}">
        <div class="error">
            There was an error added to cart
        </div>
    </c:if>
    <h1>
            ${product.description}
    </h1>
    <form method="post">
        <table>
            <tr>
                <td>Image</td>
                <td>
                    <img src="${product.imageUrl}">
                </td>
            </tr>
            <tr>
                <td>Price</td>
                <td>
                    <fmt:formatNumber value="${product.price}" type="currency"
                                      currencySymbol="${product.currency.symbol}"/>
                </td>
            </tr>
            <tr>
                <td>Code</td>
                <td>
                        ${product.code}
                </td>
            </tr>
            <tr>
                <td>Stock</td>
                <td>
                        ${product.stock}
                </td>
            </tr>
            <tr>
                <td>Quantity</td>
                <td class="quantity">
                    <input name="quantity" value="${not empty error ? param.quantity : 1}">
                    <c:if test="${not empty error}">
                        <div class="error">
                                ${error}
                        </div>
                    </c:if>
                </td>
            </tr>
        </table>
        <button>Add to cart</button>
    </form>

    <c:if test="${recently_viewed.products.size() > 0}">
        <div>
            <h3>
                Recently viewed products
            </h3>
            <tags:recentlyViewedProducts recently_viewed="${recently_viewed}"/>
        </div>
    </c:if>
</tags:master>
