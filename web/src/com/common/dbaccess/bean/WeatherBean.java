package com.common.dbaccess.bean;

import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

/**
 * WeatherBean（视频08）：从 2345 天气首页抓 8 天预报。
 *
 * 页面结构（实测）：
 *   <div class="banner-right-con-list-item ...">
 *     <div class="banner-right-con-list-time">今天</div>
 *     <div class="banner-right-con-list-status">晴</div>
 *     <div class="banner-right-con-list-temp">9~17°</div>
 *   </div>
 *
 * 老师用了一个通用做法：属性名去掉数字后正好是 HTML 的 class 后缀
 *   time   -> banner-right-con-list-time
 *   status -> banner-right-con-list-status
 *   temp1  -> 去掉 1 -> temp -> banner-right-con-list-temp，取 ~ 前面（低温）
 *   temp2  -> 去掉 2 -> temp -> banner-right-con-list-temp，取 ~ 后面（高温）
 * 老师视频里就是这么写的：temp1 存低温（8,6,5...），temp2 存高温（17,17,19...）。
 * 这里我把回调换成一个普通函数式接口 parse，思路一样、不依赖 hutool 反射。
 */
public class WeatherBean {

    /** 温度解析回调：把 "9~17°" 这种文本切成需要的那一段 */
    public interface Parse {
        String parse(String text);
    }

    private List<String> time = new ArrayList<String>();
    private List<String> status = new ArrayList<String>();
    private List<String> temp1 = new ArrayList<String>();
    private List<String> temp2 = new ArrayList<String>();

    /** 解析回调：temp1 取 ~ 前（低温），temp2 取 ~ 后（高温） */
    public static final Parse LOW = new Parse() {
        public String parse(String text) {
            String t = text.replace("°", "");
            return t.split("~")[0].trim();
        }
    };
    public static final Parse HIGH = new Parse() {
        public String parse(String text) {
            String t = text.replace("°", "");
            String[] a = t.split("~");
            return a.length > 1 ? a[1].trim() : a[0].trim();
        }
    };

    /**
     * 从 HTML 里按 class 抽取一类字段。
     * 关键：temp1/temp2 的属性名带数字，但 HTML class 是不带数字的 temp，
     * 所以要把 key 里的数字去掉再拼 class 名（老师用 hutool 的正则去掉数字，这里用 replace）。
     */
    private void add(String key, Parse parse, Document dom) {
        String css = "banner-right-con-list-" + key.replace("1", "").replace("2", "");
        for (Element div : dom.getElementsByClass(css)) {
            String text = div.text().trim();
            list(key).add(parse == null ? text : parse.parse(text));
        }
    }

    /** 根据属性名拿到对应的 List，等价于老师用 BeanUtil 反射取属性 */
    private List<String> list(String key) {
        if ("time".equals(key)) return time;
        if ("status".equals(key)) return status;
        if ("temp1".equals(key)) return temp1;
        if ("temp2".equals(key)) return temp2;
        return new ArrayList<String>();
    }

    /** 解析整页 HTML，四个字段一次抓齐；高温低温对调由调用方选 parse 决定 */
    public static WeatherBean parse(String html) {
        WeatherBean bean = new WeatherBean();
        Document dom = Jsoup.parse(html);
        bean.add("time", null, dom);
        bean.add("status", null, dom);
        bean.add("temp1", LOW, dom);   // 低温 8,6,5...（字符串 ~ 前）
        bean.add("temp2", HIGH, dom);  // 高温 17,17,19...（字符串 ~ 后）
        return bean;
    }

    public List<String> getTime() { return time; }
    public List<String> getStatus() { return status; }
    public List<String> getTemp1() { return temp1; }
    public List<String> getTemp2() { return temp2; }

    @Override
    public String toString() {
        return "WeatherBean{time=" + time + ", status=" + status
                + ", temp1=" + temp1 + ", temp2=" + temp2 + "}";
    }
}
