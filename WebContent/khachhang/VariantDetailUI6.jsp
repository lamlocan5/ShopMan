<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" session="false" %>
<%@ page import="java.sql.Connection,java.sql.SQLException,dao.DAO6,model.Product6,model.ProductVariant6,view.khachhang.VariantDetailUI6" %>
<%@ include file="/WEB-INF/jspf/request6.jspf" %>
<%
    String keyword=request.getParameter("keyword");int resultPage=ViewSupport6.page(request.getParameter("page"));
    String productId=request.getParameter("productId"),variantId=request.getParameter("variantId");
    VariantDetailUI6 ui=null;String error="";
    try(Connection con=DAO6.getConnection()){
        con.setReadOnly(true);con.setTransactionIsolation(Connection.TRANSACTION_REPEATABLE_READ);con.setAutoCommit(false);
        ui=new VariantDetailUI6(con,sessionAccount);ui.setProductId(productId);ui.setVariantId(variantId);ui.setKeyword(keyword);ui.setPage(resultPage);
        ui.showDetail();con.commit();
        if(ui.getOutVariantDetail()==null){response.setStatus(404);error=ui.getOutMessage();}
    }catch(SQLException e){application.log("ShopMan: lỗi tải chi tiết biến thể",e);ui=null;error="Không tải được dữ liệu. Vui lòng thử lại.";}
    String back="ProductDetailUI6.jsp?productId="+ViewSupport6.q(productId)+"&keyword="+ViewSupport6.q(keyword)+"&page="+resultPage;
%>
<!DOCTYPE html><html lang="vi"><head><meta charset="UTF-8"><title>Chi tiết biến thể</title></head><body>
<h1>Chi tiết biến thể</h1>
<% if(!error.isEmpty()){ %><p role="alert"><%=ViewSupport6.h(error)%></p><p><a href="<%=ViewSupport6.h("VariantDetailUI6.jsp?variantId="+ViewSupport6.q(variantId)+"&productId="+ViewSupport6.q(productId)+"&keyword="+ViewSupport6.q(keyword)+"&page="+resultPage)%>">Thử lại</a></p>
<% }else{ProductVariant6 variant=ui.getOutVariantDetail();Product6 product=variant.getProduct(); %>
<div id="outVariantDetail">
<p>Mã biến thể: <%=ViewSupport6.h(variant.getVariantId())%></p>
<h2><%=ViewSupport6.h(product.getName())%></h2>
<img width="160" height="160" src="<%=ViewSupport6.h(ViewSupport6.imageUrl(request.getContextPath(),product.getImageUrl()))%>" alt="<%=ViewSupport6.h(product.getName())%>" onerror="this.onerror=null;this.src='../assets/images/no-image.svg';">
<p>Thuộc tính: <%=ViewSupport6.h(variant.getAttributeDescription())%></p>
<p>Đơn vị: <%=ViewSupport6.h(variant.getSalesUnit())%></p>
<p>Giá hiện tại: <%=ViewSupport6.money(variant.getCurrentPrice())%> đồng</p>
<p>Tồn khả dụng: <%=ui.getAvailableQty()%></p>
</div><% } %>
<p><a id="subBack" href="<%=ViewSupport6.h(back)%>">Quay lại chi tiết mặt hàng</a> | <a id="subClose" href="CustomerHomeUI6.jsp">Đóng</a></p>
</body></html>
