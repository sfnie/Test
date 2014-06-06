<%@ page contentType="text/html; charset=gb2312" %>
<%@ page import="edu.sjtu.jaccount.*"  %>
<html>
    <head>
        <title>logout</title>
        <meta http-equiv="Content-Type" content="text/html; charset=gb2312">
    </head>

    <%
        JAccountManager jam = new JAccountManager("jagraduate20130107", getServletContext().getRealPath("/"));
        boolean loggedout = jam.logout(request, response, request.getRequestURI());
        if (!loggedout) {
            return;
        }
    %>

    <body bgcolor="#FFFFFF" text="#000000">
        <script type="text/javascript">
            window.location.href="login_jaccount";
        </script>
        <a href="login_jaccount">登录</a>
    </body>
</html>
