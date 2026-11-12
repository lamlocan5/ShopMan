<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.sql.Connection,java.sql.SQLException,dao.DAO6,dao.AccountDAO6,model.Account6,view.ViewSupport6" %>
<%
    request.setCharacterEncoding("UTF-8");
    response.setHeader("Cache-Control","no-store");
    response.setHeader("X-Content-Type-Options","nosniff");
    Object signedIn=session.getAttribute("sessionAccount");
    if(signedIn instanceof Account6&&((Account6)signedIn).isActiveCustomer()){
        String logoutToken=(String)session.getAttribute("csrfToken6");
        if(logoutToken==null){logoutToken=ViewSupport6.newToken();session.setAttribute("csrfToken6",logoutToken);}
%>
<!DOCTYPE html><html lang="vi"><head><meta charset="UTF-8"><title>Tài khoản ShopMan</title></head><body>
<h1>Tài khoản ShopMan</h1><p>Đang đăng nhập: <%=ViewSupport6.h(((Account6)signedIn).getUsername())%></p>
<p>Tra cứu sản phẩm không yêu cầu đăng nhập.</p>
<p><a href="../khachhang/CustomerHomeUI6.jsp">Mở trang tra cứu công khai</a></p>
<form method="post" action="Logout6.jsp"><input type="hidden" name="csrfToken6" value="<%=ViewSupport6.h(logoutToken)%>"><button type="submit">Đăng xuất</button></form>
</body></html>
<%
        return;
    }
    String token=(String)session.getAttribute("csrfToken6");
    if(token==null){token=ViewSupport6.newToken();session.setAttribute("csrfToken6",token);}
    String outMessage="";
    if("POST".equals(request.getMethod())){
        if(!ViewSupport6.validToken(token,request.getParameter("csrfToken6"))){
            response.setStatus(403);outMessage="Phiên đăng nhập đã thay đổi. Vui lòng gửi lại biểu mẫu.";
        }else{
            try(Connection con=DAO6.getConnection()){
                Account6 account=new AccountDAO6(con,null).checkLogin(
                        request.getParameter("username"),request.getParameter("password"));
                if(account==null){outMessage="Tên đăng nhập hoặc mật khẩu không đúng, hoặc tài khoản không có quyền Khách hàng.";}
                else{
                    session.invalidate();
                    javax.servlet.http.HttpSession freshSession=request.getSession(true);
                    freshSession.setAttribute("sessionAccount",account);
                    freshSession.setAttribute("csrfToken6",ViewSupport6.newToken());
                    response.sendRedirect(request.getContextPath()+"/khachhang/CustomerHomeUI6.jsp");return;
                }
            }catch(SQLException e){application.log("ShopMan: lỗi kết nối khi đăng nhập",e);outMessage="Không kết nối được cơ sở dữ liệu. Vui lòng thử lại.";}
        }
    }else if(!"GET".equals(request.getMethod())){response.setHeader("Allow","GET, POST");response.sendError(405);return;}
%>
<!DOCTYPE html>
<html lang="vi">
<head><meta charset="UTF-8"><title>Đăng nhập ShopMan</title></head>
<body>
<h1>ShopMan</h1>
<h2>Đăng nhập khách hàng</h2>
<% if(!outMessage.isEmpty()){ %><p role="alert"><%=ViewSupport6.h(outMessage)%></p><% } %>
<form method="post" action="Login6.jsp">
    <input type="hidden" name="csrfToken6" value="<%=ViewSupport6.h(token)%>">
    <p><label>Tên đăng nhập: <input name="username" maxlength="255" autocomplete="username" required value="<%=ViewSupport6.h(request.getParameter("username"))%>"></label></p>
    <p><label>Mật khẩu: <input type="password" name="password" maxlength="1024" autocomplete="current-password" required></label></p>
    <button type="submit" name="subLogin">Đăng nhập</button>
</form>
<p><a href="../khachhang/CustomerHomeUI6.jsp">Tra cứu sản phẩm không cần đăng nhập</a></p>
</body></html>
