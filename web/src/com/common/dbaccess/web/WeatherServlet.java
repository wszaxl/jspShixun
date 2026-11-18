package com.common.dbaccess.web;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.common.dbaccess.bean.WeatherBean;
import com.common.dbaccess.util.HttpConn;
import com.google.gson.Gson;

/**
 * WeatherServlet（视频08）：天气数据抓取 + 折线图 + 城市编号。
 *
 * 路由（web.xml 里 /weather/*）：
 *   /weather/spider     -> spider()    抓 2345 天气首页，返回 JSON
 *   /weather/chart      -> chart()     打开折线图页面
 *   /weather/cityCode   -> cityCode()  按城市名查 2345 的城市编号
 *
 * 抓数据用的是课程自带的 HttpConn.get（底层 HttpURLConnection），
 * 解析用 WEB-INF/lib 里的 jsoup，跟老师 video08 里的思路一致。
 */
public class WeatherServlet extends TomHttpServlet {

    /** 2345 天气首页：8 天预报就在这页的 banner-right-con-list-* 里 */
    private static final String HOME = "https://tianqi.2345.com/";
    /** 城市搜索接口：返回 {"res":[{"href":"/pc/pcIndex/leshan/56386",...}]} */
    private static final String CITY_SEARCH = "https://tianqi.2345.com/tpc/searchCity.php?q=";

    /**
     * 带浏览器 UA 的 GET。2345 会拒绝没有 UA 的请求（课程自带的 HttpConn 就不带 UA），
     * 所以这里单独写一个，不去改公共工具类，免得影响别处。
     */
    private String fetch(String url) throws Exception {
        HttpURLConnection conn = null;
        try {
            conn = (HttpURLConnection) new URL(url).openConnection();
            conn.setInstanceFollowRedirects(true);
            conn.setConnectTimeout(15000);
            conn.setReadTimeout(15000);
            conn.setRequestMethod("GET");
            conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0 Safari/537.36");
            conn.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/json,*/*");
            conn.setRequestProperty("Accept-Language", "zh-CN,zh;q=0.9");
            if (conn.getResponseCode() != 200) {
                return null;
            }
            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            br.close();
            return sb.toString();
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
    }

    /** 抓天气并返回 JSON，前端拿它喂给 ECharts */
    public String spider(HttpServletRequest req, HttpServletResponse resp) {
        String json;
        try {
            String html = fetch(HOME);
            WeatherBean bean = WeatherBean.parse(html);
            // 老师视频里打印的四段：time / status / temp1(低温) / temp2(高温)
            json = new Gson().toJson(bean);
        } catch (Exception e) {
            e.printStackTrace();
            json = "{\"error\":\"" + e.getMessage() + "\"}";
        }
        return outRespJson(json, resp);
    }

    /** 打开折线图页面 */
    public String chart(HttpServletRequest req, HttpServletResponse resp) {
        return open("/weather/chart.jsp");
    }

    /**
     * 按城市名查编号：/weather/cityCode?city=乐山
     * 从 href /pc/pcIndex/leshan/56386 里抠出最后那段数字。
     */
    public String cityCode(HttpServletRequest req, HttpServletResponse resp) {
        String city = req.getParameter("city");
        if (city == null || city.trim().isEmpty()) {
            city = "乐山";
        }
        String json;
        try {
            String url = CITY_SEARCH + URLEncoder.encode(city.trim(), "UTF-8") + "&pType=pc";
            String ret = fetch(url);
            // 取第一个 href 里最后一段数字，即城市编号
            // 接口返回的 href 里斜杠被转义成 \/，先把 \/ 还原成 / 再匹配
            String clean = (ret == null ? "" : ret).replace("\\/", "/");
            Matcher m = Pattern.compile("/pc/pcIndex/[a-z]+/(\\d+)").matcher(clean);
            String code = m.find() ? m.group(1) : "";
            String href = code.isEmpty() ? "" : "/pc/pcIndex/" + city.trim() + "/" + code;
            json = "{\"city\":\"" + city.trim() + "\",\"code\":\"" + code
                    + "\",\"href\":\"" + href.replace("\\/", "/") + "\"}";
        } catch (Exception e) {
            e.printStackTrace();
            json = "{\"error\":\"" + e.getMessage() + "\"}";
        }
        return outRespJson(json, resp);
    }
}
