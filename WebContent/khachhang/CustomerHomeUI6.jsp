<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" session="false" %>
<%@ page import="view.khachhang.CustomerHomeUI6" %>
<%@ include file="/WEB-INF/jspf/request6.jspf" %>
<%
    CustomerHomeUI6 ui=new CustomerHomeUI6(sessionAccount);
    if(request.getParameter("subSearchProduct")!=null){ui.openSearch();response.sendRedirect(ui.getNextView());return;}
%>
<!DOCTYPE html><html lang="vi"><head><meta charset="UTF-8"><title>Trang chính khách hàng</title></head><body>
<h1>ShopMan</h1>
<h2>Trang chính khách hàng</h2>
<p>Tra cứu sản phẩm công khai, không cần đăng nhập.</p>
<form method="get" action="CustomerHomeUI6.jsp"><button name="subSearchProduct" value="1">Tìm mặt hàng</button></form>
</body></html>
