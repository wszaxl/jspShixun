package com.common.dbaccess.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.common.dbaccess.dao.CityDAO;

/**
 * CityServlet（视频07）：城市管理的入口，和 UserServlet 一样继承自己封装的 TomHttpServlet。
 *
 * 路由（web.xml 里把 /city/* 交给它）：
 *   /city/list     -> list()    查询城市列表
 *   /city/delete   -> delete()  删除城市
 */
public class CityServlet extends TomHttpServlet {

    private CityDAO cityDAO = new CityDAO();

    /** 城市列表：查全部塞进 retList，转发到 WEB-INF/city/clist.jsp */
    public String list(HttpServletRequest req, HttpServletResponse resp) {
        req.setAttribute("retList", cityDAO.getCityList());
        return open("/city/clist.jsp");
    }

    /** 删除城市：拿到 id 直接 deleteById(表名, 主键)，删完重定向回列表 */
    public String delete(HttpServletRequest req, HttpServletResponse resp) {
        Integer id = getId(req);
        if (id != null && id > 0) {
            cityDAO.deleteById("city", id);
        }
        return open("/city/list");
    }
}
