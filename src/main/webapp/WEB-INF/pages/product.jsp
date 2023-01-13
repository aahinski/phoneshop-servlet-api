<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<jsp:useBean id="product" type="com.es.phoneshop.model.product.Product" scope="request"/>
<tags:master pageTitle="Product Details">
    <tags:cart cart="${cart}"/>
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
    <p>
        ${product.description}
    </p>
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
              <fmt:formatNumber value="${product.price}" type="currency" currencySymbol="${product.currency.symbol}"/>
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
                  <c:if test="${not empty error}" >
                      <div class="error">
                          ${error}
                      </div>
                  </c:if>
              </td>
          </tr>
      </table>
        <button>Add to cart</button>
      </form>
    <tags:recentlyViewedProducts recently_viewed="${recently_viewed}"/>
</tags:master>