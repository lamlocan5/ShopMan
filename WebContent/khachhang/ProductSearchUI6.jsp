<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" session="false" %>
<%@ page import="java.sql.Connection,java.sql.SQLException,dao.DAO6,dao.ProductDAO6,model.Product6,view.khachhang.ProductSearchUI6" %>
<%@ include file="/WEB-INF/jspf/request6.jspf" %>
<%
    String newKeyword=request.getParameter("inKeyword");
    String currentKeyword=newKeyword!=null?newKeyword:request.getParameter("keyword");
    int currentPage=newKeyword!=null?1:ViewSupport6.page(request.getParameter("page"));
    ProductSearchUI6 ui=new ProductSearchUI6(null,sessionAccount);
    ui.setInKeyword(currentKeyword);ui.setInPage(currentPage);
    if(currentKeyword!=null){
        try{
            ProductDAO6.normalizeKeyword(currentKeyword);
            try(Connection con=DAO6.getConnection()){
                con.setReadOnly(true);con.setTransactionIsolation(Connection.TRANSACTION_REPEATABLE_READ);con.setAutoCommit(false);
                ui=new ProductSearchUI6(con,sessionAccount);ui.setInKeyword(currentKeyword);ui.setInPage(currentPage);
                ui.showResults();con.commit();
            }
        }catch(IllegalArgumentException e){ui.setOutMessage(e.getMessage());ui.showMessage();}
        catch(SQLException e){
            application.log("ShopMan: lỗi tải kết quả tìm kiếm",e);
            ui=new ProductSearchUI6(null,sessionAccount);ui.setInKeyword(currentKeyword);ui.setInPage(currentPage);
            ui.setOutMessage("Không tải được dữ liệu. Vui lòng thử lại.");ui.showMessage();
        }
    }
%>
<!DOCTYPE html><html lang="vi"><head><meta charset="UTF-8"><title>Tìm kiếm mặt hàng</title></head><body>
<h1>Tìm kiếm mặt hàng</h1>
<form method="get" action="ProductSearchUI6.jsp">
    <label>Tên mặt hàng: <input id="inKeyword" name="inKeyword" value="<%=ViewSupport6.h(ui.getInKeyword())%>" maxlength="100" required autofocus></label>
    <button type="submit" name="subSearch">Tìm kiếm</button>
</form>
<% if(ui.isMessageVisible()){ %><p id="outMessage" role="alert"><%=ViewSupport6.h(ui.getOutMessage())%></p><% } %>
<% if(!ui.getOutProductList().isEmpty()){ %>
<table id="outProductList" border="1" cellpadding="6">
<thead><tr><th>Mã</th><th>Tên mặt hàng</th><th>Ảnh</th><th>Loại</th><th>Giá từ (đồng)</th><th>Tình trạng</th></tr></thead>
<tbody>
<% for(Product6 product:ui.getOutProductList()){
    String detail="ProductDetailUI6.jsp?productId="+ViewSupport6.q(product.getProductId())+"&keyword="+ViewSupport6.q(ui.getKeyword())+"&page="+ui.getPage();
%>
<tr>
<td><%=ViewSupport6.h(product.getProductId())%></td>
<td><a name="subSelectProduct" href="<%=ViewSupport6.h(detail)%>"><%=ViewSupport6.h(product.getName())%></a></td>
<td><img width="80" height="80" src="<%=ViewSupport6.h(ViewSupport6.imageUrl(request.getContextPath(),product.getImageUrl()))%>" alt="<%=ViewSupport6.h(product.getName())%>" onerror="this.onerror=null;this.src='../assets/images/no-image.svg';"></td>
<td><%=product.getCategory()==null?"":ViewSupport6.h(product.getCategory().getCategoryId()+" — "+product.getCategory().getName())%></td>
<td><%=ViewSupport6.money(ui.getMinimumPrice(product.getProductId()))%></td>
<td><%=ui.isInStock(product.getProductId())?"Còn hàng":"Hết hàng"%></td>
</tr><% } %>
</tbody></table><% } %>
<% if(currentKeyword!=null&&!ui.getOutPagination().isEmpty()){ %>
<p id="outPagination"><%=ViewSupport6.h(ui.getOutPagination())%></p>
<p>
<% if(ui.getPage()>1){ %><a href="ProductSearchUI6.jsp?keyword=<%=ViewSupport6.q(ui.getKeyword())%>&amp;page=<%=ui.getPage()-1%>">Trang trước</a><% } %>
<% if(ui.getPage()<ui.getTotalPages()){ %><a href="ProductSearchUI6.jsp?keyword=<%=ViewSupport6.q(ui.getKeyword())%>&amp;page=<%=ui.getPage()+1%>">Trang sau</a><% } %>
</p><% } %>
<% if(currentKeyword!=null&&ui.isMessageVisible()){ %><p><a href="ProductSearchUI6.jsp?keyword=<%=ViewSupport6.q(ui.getInKeyword())%>&amp;page=<%=currentPage%>">Thử lại</a></p><% } %>
<p><a id="subBack" href="CustomerHomeUI6.jsp">Quay lại trang chính</a></p>
</body></html>
