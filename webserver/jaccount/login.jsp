<%@ page contentType="text/html; charset=gb2312" %>
<%@ page import="java.util.*"  %>
<%@ page import="edu.sjtu.jaccount.*"  %>
<html>
    <head>
        <title>login.jsp</title>
        <meta http-equiv="Content-Type" content="text/html; charset=gb2312">
    </head>

    <%
        JAccountManager jam = new JAccountManager("jagraduate20130107", getServletContext().getRealPath("/"));
        Hashtable ht = jam.checkLogin(request, response, session, "/ssfw/j_spring_jaccount_security_check");
        if (ht != null) {
            response.sendRedirect("/ssfw/j_spring_jaccount_security_check");
            return;
        }
    %>

    <body bgcolor="#FFFFFF" text="#000000">
        <a href="logout.jsp">退出</a>
    </body>
</html>
