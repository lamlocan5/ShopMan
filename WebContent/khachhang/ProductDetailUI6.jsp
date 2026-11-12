<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" session="false" %>
<%@ page import="java.sql.Connection,java.sql.SQLException,dao.DAO6,model.Product6,model.ProductVariant6,view.khachhang.ProductDetailUI6" %>
<%@ include file="/WEB-INF/jspf/request6.jspf" %>
<%
    String keyword=request.getParameter("keyword");int resultPage=ViewSupport6.page(request.getParameter("page"));
    String productId=request.getParameter("productId");ProductDetailUI6 ui=null;String error="";
    try(Connection con=DAO6.getConnection()){
        con.setReadOnly(true);con.setTransactionIsolation(Connection.TRANSACTION_REPEATABLE_READ);con.setAutoCommit(false);
        ui=new ProductDetailUI6(con,sessionAccount);ui.setProductId(productId);ui.setKeyword(keyword);ui.setPage(resultPage);
        ui.showProduct();con.commit();
        if(ui.getOutProductDetail()==null){response.setStatus(404);error=ui.getOutMessage();}
    }catch(SQLException e){application.log("ShopMan: lỗi tải chi tiết mặt hàng",e);ui=null;error="Không tải được dữ liệu. Vui lòng thử lại.";}
    String back="ProductSearchUI6.jsp?keyword="+ViewSupport6.q(keyword)+"&page="+resultPage;
%>
<!DOCTYPE html><html lang="vi"><head><meta charset="UTF-8"><title>Chi tiết mặt hàng</title></head><body>
<h1>Chi tiết mặt hàng</h1>
<% if(!error.isEmpty()){ %><p role="alert"><%=ViewSupport6.h(error)%></p><p><a href="<%=ViewSupport6.h("ProductDetailUI6.jsp?productId="+ViewSupport6.q(productId)+"&keyword="+ViewSupport6.q(keyword)+"&page="+resultPage)%>">Thử lại</a></p>
<% }else{Product6 product=ui.getOutProductDetail(); %>
<div id="outProductDetail">
<p>Mã mặt hàng: <%=ViewSupport6.h(product.getProductId())%></p>
<h2><%=ViewSupport6.h(product.getName())%></h2>
<p>Loại: <%=product.getCategory()==null?"":ViewSupport6.h(product.getCategory().getCategoryId()+" — "+product.getCategory().getName())%></p>
<p><%=ViewSupport6.h(product.getDescription())%></p>
<img width="160" height="160" src="<%=ViewSupport6.h(ViewSupport6.imageUrl(request.getContextPath(),product.getImageUrl()))%>" alt="<%=ViewSupport6.h(product.getName())%>" onerror="this.onerror=null;this.src='../assets/images/no-image.svg';">
</div>
<h2>Biến thể đang kinh doanh</h2>
<% if(ui.getOutVariantList().isEmpty()){ %><p>Chưa có biến thể đang kinh doanh.</p><% }else{ %>
<table id="outVariantList" border="1" cellpadding="6"><thead><tr><th>Mã biến thể</th><th>Thuộc tính</th><th>Đơn vị</th><th>Giá hiện tại (đồng)</th><th>Tồn khả dụng</th></tr></thead><tbody>
<% for(ProductVariant6 variant:ui.getOutVariantList()){
    String detail="VariantDetailUI6.jsp?variantId="+ViewSupport6.q(variant.getVariantId())+"&productId="+ViewSupport6.q(productId)+"&keyword="+ViewSupport6.q(keyword)+"&page="+resultPage;
%><tr><td><a name="subSelectVariant" href="<%=ViewSupport6.h(detail)%>"><%=ViewSupport6.h(variant.getVariantId())%></a></td><td><%=ViewSupport6.h(variant.getAttributeDescription())%></td><td><%=ViewSupport6.h(variant.getSalesUnit())%></td><td><%=ViewSupport6.money(variant.getCurrentPrice())%></td><td><%=ui.getAvailableQty(variant.getVariantId())%></td></tr><% } %>
</tbody></table><% } %><% } %>
<p><a id="subBack" href="<%=ViewSupport6.h(back)%>">Quay lại kết quả</a></p>
</body></html>
