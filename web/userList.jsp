<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="com.common.dbaccess.bean.User" %>
<%@ page import="java.util.List" %>
<%
    String dbState = (String) request.getAttribute("dbState");
    List<User> list = (List<User>) request.getAttribute("userList");
%>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <title>用户列表 - Servlet + DAO 获取数据库数据</title>
    <style>
        body { font-family: "Microsoft YaHei", sans-serif; margin: 32px; }
        h2 { color: #2c3e50; }
        code { background: #f4f4f4; padding: 2px 6px; border-radius: 3px; }
        table { border-collapse: collapse; width: 60%; margin-top: 12px; }
        th, td { border: 1px solid #bbb; padding: 8px 12px; text-align: left; }
        th { background: #f2f2f2; }
        .warn { color: #c0392b; }
    </style>
</head>
<body>
    <h2>用户列表</h2>
    <p>数据来源：Servlet + DAO（<code>BaseDAO.selectList</code>）查询 MySQL <code>lizx.user</code> 表</p>

    <% if ("error".equals(dbState)) { %>
        <p class="warn">数据库连接失败：<%= (String) request.getAttribute("dbMsg") %></p>
    <% } %>

    <table>
        <tr><th>ID</th><th>用户名</th><th>密码</th></tr>
        <% if (list != null) {
            for (User u : list) { %>
        <tr>
            <td><%= u.getId() %></td>
            <td><%= u.getUsername() %></td>
            <td><%= u.getPassword() %></td>
        </tr>
        <%  }
        } else { %>
        <tr><td colspan="3">暂无数据</td></tr>
        <% } %>
    </table>
</body>
</html>
