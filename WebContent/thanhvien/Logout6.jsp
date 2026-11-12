<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="view.ViewSupport6" %>
<%
    response.setHeader("Cache-Control","no-store");
    if(!"POST".equals(request.getMethod())){response.setHeader("Allow","POST");response.sendError(405);return;}
    if(!ViewSupport6.validToken((String)session.getAttribute("csrfToken6"),request.getParameter("csrfToken6"))){response.sendError(403);return;}
    session.invalidate();response.sendRedirect("Login6.jsp");
%>
