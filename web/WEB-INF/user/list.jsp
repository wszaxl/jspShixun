<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="com.common.dbaccess.bean.User" %>
<%@ page import="java.util.List" %>
<%--
  list.jsp —— 用户列表（视频05）
  位置：WEB-INF/user/list.jsp，只能由 UserServlet 的 list() 转发进来
  入口：/user/list?k=关键字
--%>
<%
    List<User> retList = (List<User>) request.getAttribute("retList");
    // 视频07：查询关键字从 servlet 回传，输入框用它做回显（原来叫 k，改成和老师一致的 username）
    String kw = (String) request.getAttribute("username");
    if (kw == null) kw = "";
    String msg = request.getParameter("msg");
%>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <title>用户管理 - 运营管理平台</title>
    <link rel="stylesheet" href="/static/css/admin.css">
</head>
<body>

<!-- ===================== 顶部导航 ===================== -->
<div class="topbar">
    <a class="brand" href="/user/list">
        <svg width="20" height="20" viewBox="0 0 24 24" fill="none">
            <path d="M4 20c0-8 5-14 16-15-1 11-7 16-16 15z" fill="#ffffff" opacity=".92"/>
            <path d="M4 20c3-6 7-9 12-11" stroke="#5492B7" stroke-width="1.4" stroke-linecap="round"/>
        </svg>
        运营管理平台
    </a>
    <div class="userzone">
        <span class="avatar"></span>
        <div class="uinfo">
            <span>欢迎光临，</span>
            <span class="uname">${empty sessionScope.username ? '张三' : sessionScope.username}</span>
        </div>
        <span class="caret"></span>
    </div>
</div>

<!-- ===================== 左侧栏 ===================== -->
<aside class="sidebar">
    <div class="shortcuts">
        <a class="sc sc1" title="统计">
            <svg width="18" height="18" viewBox="0 0 24 24" fill="#fff">
                <rect x="3" y="12" width="4" height="8"/><rect x="10" y="7" width="4" height="13"/>
                <rect x="17" y="3" width="4" height="17"/>
            </svg>
        </a>
        <a class="sc sc2" title="编辑">
            <svg width="17" height="17" viewBox="0 0 24 24" fill="#fff">
                <path d="M3 17.2V21h3.8L18 9.8 14.2 6 3 17.2zM20.7 7.1c.4-.4.4-1 0-1.4l-2.4-2.4c-.4-.4-1-.4-1.4 0l-1.9 1.9 3.8 3.8 1.9-1.9z"/>
            </svg>
        </a>
        <a class="sc sc3" title="报表">
            <svg width="17" height="17" viewBox="0 0 24 24" fill="none" stroke="#fff" stroke-width="1.8">
                <rect x="3" y="4" width="18" height="16" rx="1.5"/>
                <path d="M3 9h18M9 9v11M15 9v11"/>
            </svg>
        </a>
        <a class="sc sc4" title="设置">
            <svg width="18" height="18" viewBox="0 0 24 24" fill="#fff">
                <path d="M12 8.5A3.5 3.5 0 1 0 12 15.5 3.5 3.5 0 0 0 12 8.5zm9 3.5c0-.6-.1-1.2-.2-1.7l2-1.5-2-3.4-2.3 1c-.9-.7-1.9-1.3-3-1.6L15 2H9l-.5 2.8c-1.1.3-2.1.9-3 1.6l-2.3-1-2 3.4 2 1.5c-.1.5-.2 1.1-.2 1.7s.1 1.2.2 1.7l-2 1.5 2 3.4 2.3-1c.9.7 1.9 1.3 3 1.6L9 22h6l.5-2.8c1.1-.3 2.1-.9 3-1.6l2.3 1 2-3.4-2-1.5c.1-.5.2-1.1.2-1.7z"/>
            </svg>
        </a>
    </div>

    <ul class="nav">
        <li class="nav-item active">
            <span class="ic">
                <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8">
                    <circle cx="12" cy="12" r="9"/><path d="M12 3v9l6 3"/>
                </svg>
            </span>
            控制台
        </li>
        <li class="nav-item parent">
            <span class="ic">
                <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8">
                    <rect x="3" y="4" width="18" height="12" rx="1.5"/><path d="M8 20h8M12 16v4"/>
                </svg>
            </span>
            系统管理
            <span class="chev">&#9662;</span>
        </li>
        <ul class="submenu">
            <li><a href="/user/list">用户管理</a></li>
            <li><a href="/city/list">城市查看</a></li>
        </ul>
    </ul>

    <div class="collapse"><span>&#8249;&#8249;</span></div>
</aside>

<!-- ===================== 主内容 ===================== -->
<div class="content">
    <div class="breadcrumb">
        <a href="/user/list">用户管理</a><span class="sep">/</span>查询用户
    </div>

    <form class="filter" action="/user/list" method="get">
        <label>用户名：</label>
        <input type="text" name="username" value="<%= kw %>" placeholder="请输入用户名" />
        <button type="submit" class="btn btn-red">查询</button>
        <a class="btn btn-green" href="/user/add">添加用户</a>
    </form>

    <% if (msg != null && !msg.isEmpty()) { %>
    <div class="msgbar"><%= msg %></div>
    <% } %>

    <table class="tbl">
        <thead>
        <tr>
            <th class="col-no">编号</th>
            <th class="col-pk">主键</th>
            <th class="col-name">账号/用户名</th>
            <th class="col-pwd">密码</th>
            <th class="col-role">身份</th>
            <th class="col-op">操作</th>
        </tr>
        </thead>
        <tbody>
        <%
            if (retList != null && !retList.isEmpty()) {
                int no = 1;
                for (User u : retList) {
        %>
        <tr>
            <td><%= no++ %></td>
            <td><%= u.getId() %></td>
            <td><%= u.getUsername() %></td>
            <td><%= u.getPassword() %></td>
            <td><%= u.getRole() %></td>
            <td>
                <div class="op-cell">
                    <a class="btn btn-blue btn-xs" href="/user/add?id=<%= u.getId() %>">修改</a>
                    <a class="btn btn-red btn-xs" href="/user/delete?id=<%= u.getId() %>"
                       onclick="return confirm('确定要删除【<%= u.getUsername() %>】吗？')">删除</a>
                </div>
            </td>
        </tr>
        <%
                }
            } else {
        %>
        <tr><td colspan="6" style="text-align:center;color:#999;padding:26px;">暂无数据</td></tr>
        <% } %>
        </tbody>
    </table>
</div>

</body>
</html>
