<%@ page import="java.util.*"  %>
<%@ page import="edu.sjtu.jaccount.*"  %>
<%@ page contentType="text/html; charset=gb2312" %>

<html>
    <head>
        <title>登录入口</title>
        <meta http-equiv="Content-Type" content="text/html; charset=gb2312">
    </head>
    
    <%
    	String ctx = request.getSession().getServletContext().getContextPath();
    %>

    <body bgcolor="#FFFFFF" text="#000000">
        <a href="<%=ctx%>/mixjaccount/login.jsp">JAccount登录</a>
        <a href="<%=ctx%>/login.jsp">数据库登录</a>
    </body>
</html>
