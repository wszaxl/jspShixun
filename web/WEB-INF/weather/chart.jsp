<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%--
  chart.jsp —— 天气折线图（视频08）
  入口：/weather/chart   数据：/weather/spider   城市编号：/weather/cityCode?city=乐山
  图表配置照视频09老师的成品（XX一周气温变化 / 高温蓝线 / 低温绿线 / 双横轴 / 中文图例），
  外壳沿用我们项目的运营管理平台皮肤，保证和用户管理、城市管理界面一套风格。
--%>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <title>天气折线图 - 运营管理平台</title>
    <link rel="stylesheet" href="/static/css/admin.css">
    <style>
        .chart-box { background:#fff; margin:20px 26px; padding:18px; border:1px solid #e6e6e6; border-radius:4px; }
        #chart { width:100%; height:460px; }
        .city-bar { display:flex; align-items:center; gap:10px; margin-bottom:12px; }
        .city-bar input { height:36px; width:180px; padding:0 10px; border:1px solid #cfd8dd; border-radius:3px; }
        /* 视频09：老师天气页的查询栏是浅蓝渐变条（跟用户列表页一致） */
        .content .filter {
            background: linear-gradient(to bottom, #e8f6fc 0%, #d9eef8 100%);
            border-bottom: 1px solid #cfe5f0;
        }
        .content .filter label { font-size: 14px; color: #333; white-space: nowrap; }
        #code { color:#31708f; font-size:13px; }
    </style>
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
        <div class="usermenu"><a href="/user/logout">退出登录</a></div>
    </div>
</div>

<aside class="sidebar">
    <div class="shortcuts">
        <a class="sc sc1" title="统计"><svg width="18" height="18" viewBox="0 0 24 24" fill="#fff"><rect x="3" y="12" width="4" height="8"/><rect x="10" y="7" width="4" height="13"/><rect x="17" y="3" width="4" height="17"/></svg></a>
        <a class="sc sc2" title="编辑"><svg width="17" height="17" viewBox="0 0 24 24" fill="#fff"><path d="M3 17.2V21h3.8L18 9.8 14.2 6 3 17.2zM20.7 7.1c.4-.4.4-1 0-1.4l-2.4-2.4c-.4-.4-1-.4-1.4 0l-1.9 1.9 3.8 3.8 1.9-1.9z"/></svg></a>
        <a class="sc sc3" title="报表"><svg width="17" height="17" viewBox="0 0 24 24" fill="none" stroke="#fff" stroke-width="1.8"><rect x="3" y="4" width="18" height="16" rx="1.5"/><path d="M3 9h18M9 9v11M15 9v11"/></svg></a>
        <a class="sc sc4" title="设置"><svg width="18" height="18" viewBox="0 0 24 24" fill="#fff"><path d="M12 8.5A3.5 3.5 0 1 0 12 15.5 3.5 3.5 0 0 0 12 8.5zm9 3.5c0-.6-.1-1.2-.2-1.7l2-1.5-2-3.4-2.3 1c-.9-.7-1.9-1.3-3-1.6L15 2H9l-.5 2.8c-1.1.3-2.1.9-3 1.6l-2.3-1-2 3.4 2 1.5c-.1.5-.2 1.1-.2 1.7s.1 1.2.2 1.7l-2 1.5 2 3.4 2.3-1c.9.7 1.9 1.3 3 1.6L9 22h6l.5-2.8c1.1-.3 2.1-.9 3-1.6l2.3 1 2-3.4-2-1.5c.1-.5.2-1.1.2-1.7z"/></svg></a>
    </div>
    <ul class="nav">
        <li class="nav-item"><span class="ic"><svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8"><circle cx="12" cy="12" r="9"/><path d="M12 3v9l6 3"/></svg></span>控制台</li>
        <li class="nav-item parent"><span class="ic"><svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8"><rect x="3" y="4" width="18" height="12" rx="1.5"/><path d="M8 20h8M12 16v4"/></svg></span>系统管理<span class="chev">&#9662;</span></li>
        <ul class="submenu">
            <c:if test="${sessionScope.status == '管理员'}">
            <li><a href="/user/list">用户管理</a></li>
            </c:if>
            <li><a href="/city/list">城市查看</a></li>
            <li class="active"><a href="/weather/chart">天气预报</a></li>
        </ul>
    </ul>
    <div class="collapse"><span>&#8249;&#8249;</span></div>
</aside>

<div class="content">
    <form class="filter" onsubmit="return false;">
        <label>城市名称：</label>
        <input type="text" id="city" value="北京" />
        <button type="button" class="btn btn-red" onclick="weatherCity()">查询</button>
    </form>

    <div class="chart-box">
        <div id="chart"></div>
    </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/echarts@5.4.3/dist/echarts.min.js"></script>
<script>
    var chart = echarts.init(document.getElementById('chart'));
    var option = {
        title: { text: '一周气温变化', left: 'center' },
        tooltip: { trigger: 'axis' },
        legend: { data: ['高温', '低温'], top: 4, right: 140 },
        toolbox: { show: true, feature: { dataZoom: {}, dataView: { readOnly: false }, magicType: { type: ['line', 'bar'] }, restore: {}, saveAsImage: {} } },
        xAxis: [
            { type: 'category', boundaryGap: false, data: [], position: 'top' },
            { type: 'category', boundaryGap: false, data: [], position: 'bottom' }
        ],
        yAxis: { type: 'value', axisLabel: { formatter: '{value} °C' } },
        series: [
            {
                name: '高温', type: 'line', data: [], smooth: true,
                markPoint: { data: [{ type: 'max', name: '最高气温' }] },
                markLine: { data: [{ type: 'average', name: '平均值' }] },
                lineStyle: { color: '#5470c6' }, itemStyle: { color: '#5470c6' }
            },
            {
                name: '低温', type: 'line', data: [], smooth: true,
                markPoint: { data: [{ type: 'min', name: '最低气温' }] },
                markLine: { data: [{ type: 'average', name: '平均值' }] },
                lineStyle: { color: '#91cc75' }, itemStyle: { color: '#91cc75' }
            }
        ]
    };

    // 视频09 切换城市：城市存 localStorage，刷新后还记得上次查的城市
    function weatherCity(city) {
        if (!city) {
            city = document.getElementById('city').value.trim() || localStorage.getItem('city') || '北京';
        }
        document.getElementById('city').value = city;
        fetch('/weather/spider?city=' + encodeURIComponent(city))
            .then(function (r) { return r.json(); })
            .then(function (w) {
                if (w.error) { document.getElementById('chart').innerHTML = '数据获取失败：' + w.error; return; }
                option.title.text = w.city + '一周气温变化';
                localStorage.setItem('city', w.city);
                option.xAxis[0].data = w.time;      // 上轴：日期
                option.xAxis[1].data = w.status;    // 下轴：天气状况
                option.series[0].data = w.temp2;    // 高温蓝线
                option.series[1].data = w.temp1;    // 低温绿线
                chart.setOption(option, true);
            })
            .catch(function (e) {
                document.getElementById('chart').innerHTML = '数据加载失败：' + e;
            });
    }

    weatherCity(localStorage.getItem('city') || document.getElementById('city').value.trim());
    window.addEventListener('resize', function () { chart.resize(); });
</script>

</body>
</html>
