<%@page import="org.mardep.ssrs.servlet.FsqcDownload"%>
<%@ page language="java" contentType="text/html; charset=UTF8" pageEncoding="UTF8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF8">
<title>FSQC DEV</title>
</head>
<body>
<%
	try {
FsqcDownload.test(request, response);
	} catch (Exception e) {
		e.printStackTrace(response.getWriter());
	}

%>
</body>
</html>