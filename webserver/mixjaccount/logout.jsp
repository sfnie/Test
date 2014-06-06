<%@ page contentType="text/html; charset=gb2312"%>
<%@ page
	import="edu.sjtu.jaccount.*,org.slf4j.Logger,org.slf4j.LoggerFactory,org.ratchetgx.orion.security.mixjaccount.MixJAccountConstants"%>
<html>
<head>
<title>logout</title>
<meta http-equiv="Content-Type" content="text/html; charset=gb2312">
</head>

<%
	Logger log = LoggerFactory.getLogger(this.getClass());
	log.debug(request.getRequestURI());

	String mode = (String)session
			.getAttribute(MixJAccountConstants.JACCOUNT_SESSION_LOGINED_MODE);
	log.debug("登出,mode=" + mode);
	if ("db".equals(mode)) {
		session.invalidate();
	} else {
		JAccountManager jam = new JAccountManager("jagraduate20130107",
				getServletContext().getRealPath("/"));
		boolean loggedout = jam.logout(request, response,
				request.getRequestURI());
		if (!loggedout) {
			return;
		}
	}
%>

<body bgcolor="#FFFFFF" text="#000000">
	<script type="text/javascript">
		window.location.href = "login_entry.jsp";
	</script>
</body>
</html>
