package com.common.dbaccess.web;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import com.common.dbaccess.mvc.BaseHttpServlet;

/**
 * TomHttpServlet（视频06）：在框架的 BaseHttpServlet 之上再加一层自己的封装。
 *
 * 为什么要多这一层：BaseHttpServlet 是课程提供的公共代码，直接改怕改坏，
 * 所以自己写一个子类，把项目里反复出现的东西放进来，业务 Servlet 改成继承它。
 *
 * 继承链：HttpServlet -> BaseHttpServlet -> TomHttpServlet -> UserServlet
 *
 * 封装了两件事：
 *   1. open(url)：把 forward / redirect 二合一。带 .jsp 的一定是服务器内部转发，
 *      其余情况走重定向；转发时自动补上 WEB-INF/user/ 这个根。
 *   2. utf8Encode(url)：URL 参数里直接写中文时，Location 响应头会乱码，
 *      这里统一把非 ASCII 的参数值按 UTF-8 编码，业务代码里就能直接写中文提示。
 */
public class TomHttpServlet extends BaseHttpServlet {

    /**
     * JSP 都放在 WEB-INF 下，浏览器不能直接访问，只能由 Servlet 转发。
     * 视频07 又多了 city 模块，所以根目录放宽到 WEB-INF/，调用时写子目录：
     *   open("/user/list.jsp")  -> /WEB-INF/user/list.jsp
     *   open("/city/clist.jsp") -> /WEB-INF/city/clist.jsp
     */
    private static final String ROOT = "/WEB-INF/";

    /**
     * 打开一个地址：转发和重定向二合一。
     * 例：open("/list.jsp")               -> forward("/WEB-INF/user/list.jsp")
     *     open("/user/list?msg=删除成功")  -> redirect("/user/list?msg=%E5%88%A0...")
     */
    public String open(String url) {
        if (url == null) {
            return null;
        }
        url = utf8Encode(url.trim());
        if (url.endsWith(".jsp")) {
            return forward(ROOT + url);
        }
        return redirect(url);
    }

    /**
     * 把地址里的中文参数值按 UTF-8 编码（对应视频里在 TomHttpServlet 中解决乱码那段）。
     * 只处理 ? 后面 key=value 中的 value，key 和路径保持原样，已经是 %XX 的不再重复编码。
     */
    public String utf8Encode(String url) {
        int q = url.indexOf('?');
        if (q < 0) {
            return url;
        }
        String base = url.substring(0, q + 1);
        String query = url.substring(q + 1);
        if (query.isEmpty()) {
            return url;
        }
        StringBuilder sb = new StringBuilder(base);
        String[] pairs = query.split("&");
        for (int i = 0; i < pairs.length; i++) {
            if (i > 0) {
                sb.append('&');
            }
            String pair = pairs[i];
            int eq = pair.indexOf('=');
            if (eq < 0) {
                sb.append(pair);
                continue;
            }
            String key = pair.substring(0, eq);
            String val = pair.substring(eq + 1);
            sb.append(key).append('=').append(encodeValue(val));
        }
        return sb.toString();
    }

    /** 含非 ASCII 且未被编码过的值才做 URLEncoder，避免把 %E5%88%A0 再编一次 */
    private String encodeValue(String val) {
        if (val == null || val.isEmpty() || val.indexOf('%') >= 0) {
            return val;
        }
        boolean need = false;
        for (int i = 0; i < val.length(); i++) {
            if (val.charAt(i) > 127) {
                need = true;
                break;
            }
        }
        if (!need) {
            return val;
        }
        try {
            return URLEncoder.encode(val, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return val;
        }
    }
}
