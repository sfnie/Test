<%@ page import="java.util.*"  %>
<%@ page import="edu.sjtu.jaccount.*"  %>
<%@ page contentType="text/html; charset=gb2312" %>

<html>
    <head>
        <title>��¼���</title>
        <meta http-equiv="Content-Type" content="text/html; charset=gb2312">
    </head>
    
    <%
    	String ctx = request.getSession().getServletContext().getContextPath();
    %>

    <body bgcolor="#FFFFFF" text="#000000">
        <a href="<%=ctx%>/mixjaccount/login.jsp">JAccount��¼</a>
        <a href="<%=ctx%>/login.jsp">���ݿ��¼</a>
    </body>
</html>
