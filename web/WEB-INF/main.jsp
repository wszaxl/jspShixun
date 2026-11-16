<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<%--
  main.jsp —— 登录成功后的主页（视频04）
  1. 放在 WEB-INF 下：不能直接用地址访问，只能由 Servlet 转发进来，安全性更好
  2. ${username} 是 EL 表达式，会依次从 page/request/session/application 域里找 username
--%>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <title>运营平台 - 登录成功</title>
    <style>
        * { box-sizing: border-box; }
        body { margin: 0; font-family: "Microsoft YaHei", "PingFang SC", sans-serif; background: #f4f6f8; color: #2c3e50; }
        .navbar { height: 56px; background: #2f4050; color: #fff; display: flex; align-items: center; justify-content: space-between; padding: 0 24px; }
        .navbar .brand { font-size: 17px; letter-spacing: 1px; }
        .navbar .userbox { display: flex; align-items: center; gap: 14px; font-size: 14px; }
        .navbar .userbox b { color: #7fd1c1; font-weight: 600; }
        .navbar .logout { color: #cfd8e3; text-decoration: none; border: 1px solid #5a6c7d; border-radius: 4px; padding: 5px 12px; }
        .navbar .logout:hover { color: #fff; background: #e74c3c; border-color: #e74c3c; }
        .wrap { max-width: 760px; margin: 40px auto; background: #fff; border-radius: 8px; padding: 28px 32px; box-shadow: 0 2px 10px rgba(0,0,0,.06); }
        h2 { margin-top: 0; font-size: 20px; }
        table { border-collapse: collapse; width: 100%; margin-top: 14px; }
        th, td { border: 1px solid #e2e8f0; padding: 10px 12px; text-align: left; font-size: 14px; }
        th { background: #f8fafc; width: 130px; color: #475569; font-weight: 600; }
        code { background: #f1f5f9; padding: 2px 6px; border-radius: 4px; color: #b91c1c; }
        .tip { margin-top: 22px; padding: 12px 16px; background: #eff6ff; border-left: 4px solid #2563eb; font-size: 13px; line-height: 1.7; }
        a.btn { display: inline-block; margin-top: 18px; background: #2563eb; color: #fff; text-decoration: none; padding: 8px 18px; border-radius: 5px; font-size: 14px; }
    </style>
</head>
<body>

<div class="navbar">
    <span class="brand">运营平台</span>
    <div class="userbox">
        <span>欢迎光临，<b>${username}</b></span>
        <a class="logout" href="/user/logout">退出</a>
    </div>
</div>

<div class="wrap">
    <h2>登录成功</h2>
    <table>
        <tr><th>当前用户</th><td>${username}</td></tr>
        <tr><th>取到的位置</th><td>request / session 域中的 <code>username</code></td></tr>
        <tr><th>访问地址</th><td><code>/user/login</code>（forward 转发后地址栏不变）</td></tr>
    </table>

    <a class="btn" href="/user/list">查看用户列表</a>

    <div class="tip">
        这个页面放在 <code>WEB-INF</code> 下，直接用地址 <code>/WEB-INF/main.jsp</code> 是打不开的，
        必须由 UserServlet 登录校验通过后 forward 进来。<br>
        点右上角「退出」会访问 <code>/user/logout</code>，销毁 session 后重定向回登录页。
    </div>
</div>

</body>
</html>
