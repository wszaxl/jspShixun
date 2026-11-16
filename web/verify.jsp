<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="com.common.dbaccess.util.StringTools" %>
<%
    String result = StringTools.toFirstUpperCase("jspShixun");
    String loaded = StringTools.class.getName();
%>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
  <meta charset="UTF-8">
  <title>Java 代码运行验证</title>
</head>
<body>
  <h1>Java 代码已在 Tomcat 中运行</h1>
  <p>加载到的类：<code><%= loaded %></code></p>
  <p>调用 <code>StringTools.toFirstUpperCase("jspShixun")</code> = <strong><%= result %></strong></p>
  <p>编译产物：<code>WEB-INF/classes/com/common/dbaccess/util/StringTools.class</code></p>
</body>
</html>
