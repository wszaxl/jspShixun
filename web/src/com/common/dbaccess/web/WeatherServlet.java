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
        return fetch(url, null);
    }

    /**
     * 带自定义 Cookie 的 GET。视频09 切换城市要用：2345 的城市页会 302 到首页，
     * 通过 Set-Cookie: lastCountyId=<编号> 告诉服务器显示哪个城市，Java 的 HttpURLConnection
     * 跟随重定向时不会自动带上 Cookie，所以要手动设置。
     */
    private String fetch(String url, String cookie) throws Exception {
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
            if (cookie != null && !cookie.isEmpty()) {
                conn.setRequestProperty("Cookie", cookie);
            }
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

    /** 请求城市页但不跟随重定向，从 302 的 Set-Cookie 里取出 lastCountyId=<编号> */
    private String getCityCookie(String cityUrl) {
        HttpURLConnection conn = null;
        try {
            conn = (HttpURLConnection) new URL(cityUrl).openConnection();
            conn.setInstanceFollowRedirects(false);
            conn.setConnectTimeout(15000);
            conn.setReadTimeout(15000);
            conn.setRequestMethod("GET");
            conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 Chrome/120 Safari/537.36");
            conn.getResponseCode();
            // 302 可能返回多个 Set-Cookie，遍历所有值找 lastCountyId
            java.util.List<String> cookies = conn.getHeaderFields().get("Set-Cookie");
            if (cookies != null) {
                for (String c : cookies) {
                    if (c != null && c.contains("lastCountyId=")) {
                        String id = c.split("lastCountyId=")[1].split(";")[0];
                        return "lastCountyId=" + id;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
        return null;
    }

    /**
     * 抓天气并返回 JSON（视频09 支持切换城市）。
     * /weather/spider?city=北京 -> 先查编号拿 href，再抓该城市页面；
     * 不传 city 就抓首页（本机 IP 对应城市）。返回里多带一个 city 字段给标题用。
     */
    public String spider(HttpServletRequest req, HttpServletResponse resp) {
        String json;
        try {
            String city = req.getParameter("city");
            String html;
            if (city != null && !city.trim().isEmpty()) {
                city = city.trim();
                String searchUrl = CITY_SEARCH + URLEncoder.encode(city, "UTF-8") + "&pType=pc";
                String searchRet = fetch(searchUrl);
                String clean = (searchRet == null ? "" : searchRet).replace("\\/", "/");
                Matcher m = Pattern.compile("/pc/pcIndex/[a-z]+/\\d+").matcher(clean);
                if (!m.find()) {
                    json = "{\"error\":\"未找到城市：" + city + "\"}";
                    return outRespJson(json, resp);
                }
                // 老师视频09：城市页 302 会用 Set-Cookie 写入 lastCountyId，
                // 拿到这个 Cookie 再请求首页，服务器才返回指定城市的天气
                String cityUrl = "https://tianqi.2345.com" + m.group();
                String cookie = getCityCookie(cityUrl);
                html = fetch(HOME, cookie);
            } else {
                html = fetch(HOME);
            }
            WeatherBean bean = WeatherBean.parse(html);
            // 老师视频里打印的四段：time / status / temp1(低温) / temp2(高温)
            String beanJson = new Gson().toJson(bean);
            // 把 city 塞进返回的 JSON（去掉 WeatherBean 那段尾部大括号再拼）
            String cityJson = new Gson().toJson(city == null ? "本地" : city);
            json = beanJson.substring(0, beanJson.length() - 1) + ",\"city\":" + cityJson + "}";
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
