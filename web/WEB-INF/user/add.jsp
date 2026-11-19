<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%--
  add.jsp —— 添加用户 / 修改用户（视频05 打开添加页面，视频06 复用为修改页面）
  位置：WEB-INF/user/add.jsp，只能由 UserServlet 的 add() 转发进来
  入口：/user/add        -> 不带 id，面包屑「添加用户」，表单为空
        /user/add?id=5   -> 带   id，面包屑「修改用户」，表单回填
  提交：/user/submit（添加和修改共用），靠隐藏域 id 区分 insert / update
--%>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <title>${msg}用户 - 运营管理平台</title>
    <link rel="stylesheet" href="/static/css/admin.css">
</head>
<body>

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
            <span class="uname">${empty sessionScope.username ? '张三' : sessionScope.username}<small class="urole">${empty sessionScope.status ? '' : sessionScope.status}</small></span>
        </div>
        <span class="caret"></span>
        <div class="usermenu">
            <a href="/user/logout">退出登录</a>
        </div>
    </div>
</div>

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
            <c:if test="${sessionScope.status == '管理员'}">
            <li><a href="/user/list">用户管理</a></li>
            </c:if>
            <li><a href="/city/list">城市查看</a></li>
            <li><a href="/weather/chart">天气预报</a></li>
        </ul>
    </ul>
    <div class="collapse"><span>&#8249;&#8249;</span></div>
</aside>

<div class="content">
    <div class="breadcrumb">
        <a href="/user/list">用户管理</a><span class="sep">/</span>${msg}用户
    </div>

    <div class="page-header">基本信息</div>

    <form class="form-horizontal" action="/user/submit" method="post">
        <!-- 修改时带主键，添加时为 0；UserDAO.insertOrUpdate 靠它判断 insert 还是 update -->
        <input type="hidden" name="id" value="${b.id}" />

        <div class="form-row">
            <label>用户名</label>
            <div class="ctl"><input type="text" name="username" value="${b.username}" /></div>
        </div>
        <div class="form-row">
            <label>密码</label>
            <div class="ctl"><input type="text" name="password" value="${b.password}" /></div>
        </div>
        <!-- 视频07：身份从隐藏域改成下拉框，修改时由下面的 JS 选中原来的身份 -->
        <div class="form-row">
            <label>身份</label>
            <div class="ctl">
                <select name="role" required>
                    <option value="管理员">管理员</option>
                    <option value="用户">用户</option>
                </select>
            </div>
        </div>

        <div class="form-row form-actions">
            <button type="submit" class="btn btn-green">保存</button>
            <a class="btn btn-orange" href="/user/list">返回上一级</a>
        </div>
    </form>
</div>

<script>
    // 视频07：修改时下拉框默认选中这个人原来的身份，不然一进去就变成"管理员"
    window.onload = function () {
        var sel = document.getElementsByName('role')[0];
        var v = '${b.role}';
        if (sel && v) {
            sel.value = v;
        }
    };
</script>

</body>
</html>
