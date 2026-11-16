<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="com.common.dbaccess.util.StringTools" %>
<%@ page import="com.google.gson.Gson" %>
<%@ page import="java.util.*" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%
    String javaValue = StringTools.toFirstUpperCase("jspShixun");
    List<String> jars = Arrays.asList("gson-2.8.5", "hutool-all-5.8.48", "jsoup-1.11.2",
                                     "jstl", "standard", "servlet-api", "mysql-connector-java-5.1.26-bin");
    Map<String, String> info = new LinkedHashMap<String, String>();
    info.put("项目", "jspShixun");
    info.put("JDK", System.getProperty("java.version"));
    info.put("file.encoding", System.getProperty("file.encoding"));
    info.put("容器", application.getServerInfo());
    info.put("服务器时间", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
    String json = new Gson().toJson(info);
%>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
<meta charset="UTF-8">
<title>Java 值如何变成 HTML</title>
<style>
body{font-family:"Microsoft YaHei",sans-serif;max-width:820px;margin:32px auto;padding:0 18px;color:#1f2937;line-height:1.6}
h1{font-size:24px;border-bottom:2px solid #2563eb;padding-bottom:8px}
h2{font-size:17px;margin-top:26px}
code,pre{background:#f3f4f6;padding:2px 6px;border-radius:4px}
pre{padding:12px;overflow:auto}
table{border-collapse:collapse;width:100%}
th,td{border:1px solid #d1d5db;padding:6px 10px;text-align:left;font-size:14px}
th{background:#f3f4f6}
.hint{background:#eff6ff;border-left:4px solid #2563eb;padding:10px 14px;margin-top:24px}
</style>
</head>
<body>
<h1>Java 算出来的值，怎么放进 HTML</h1>
<p>这一整页是 JSP：Tomcat 先执行 Java，再把结果填进下面的 HTML 模板。</p>

<h2>① 单个值：文本插值</h2>
<p>Java 调用 <code>StringTools.toFirstUpperCase("jspShixun")</code> → 页面显示 <strong><%= javaValue %></strong></p>

<h2>② Java 集合 → HTML 列表</h2>
<ul>
<% for (String j : jars) { %><li><%= j %>.jar</li><% } %>
</ul>

<h2>③ Java Map → HTML 表格</h2>
<table><tr><th>项</th><th>值（来自 Java）</th></tr>
<% for (Map.Entry<String,String> e : info.entrySet()) { %>
<tr><td><%= e.getKey() %></td><td><%= e.getValue() %></td></tr>
<% } %>
</table>

<h2>④ Java 对象 → JSON（用 gson.jar）</h2>
<pre><%= json %></pre>

<div class="hint">
浏览器收到的只有 HTML，看不到任何 Java。上面每个 <code>&lt;%= ... %&gt;</code> 的位置，都已经在服务器里被 Java 的值替换掉了；中文按 UTF-8 输出。
</div>
</body>
</html>
