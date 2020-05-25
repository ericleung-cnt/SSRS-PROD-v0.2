<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="UTF-8"%>
<%@ taglib uri="/WEB-INF/iscTaglib.tld" prefix="isomorphic"%>
<%
response.addHeader("Access-Control-Allow-Origin", "http://127.0.0.1");
%>
<HTML>
<head>
<TITLE>Marine Dep. SSRS</TITLE>

<meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1" />
<meta http-equiv="Cache-Control" content="max-age=0">
<meta http-equiv="Access-Control-Allow-Origin" content="*">
<link rel="stylesheet" type="text/css" href="css/ssrs_custom.css" />
<link rel="stylesheet" type="text/css" href="css/ssrs_mingliu.css" />
<link rel="icon" type="image/png" href="images/marine2.png" />
</head>
<body bgcolor="<%=System.getProperty("ssrsIndexBodyBg") != null ? System.getProperty("ssrsIndexBodyBg") :  "#7fa5b8"%>">
	<isomorphic:loadISC skin="EnterpriseBlue" includeModules="Calendar, PluginBridges" runat="server" />
	<SCRIPT SRC=isomorphic/system/modules/ISC_FileLoader.js></SCRIPT>
	<SCRIPT type='text/javascript' SRC='datasource.js' charset="UTF-8"></SCRIPT>
	<script type='text/javascript' src="./js/jquery.min.js" charset="UTF-8"></script>
	<script type='text/javascript'>jQuery.noConflict();</script>
	<SCRIPT type='text/javascript' SRC='widget.js' charset="UTF-8"></SCRIPT>
	<SCRIPT type='text/javascript' SRC='login.js' charset="UTF-8"></SCRIPT>
	<SCRIPT type='text/javascript' SRC='./js/sr/shipReg/openRegMaster.js' charset="UTF-8"></SCRIPT>
</body>
</html>