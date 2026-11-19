package com.common.dbaccess.dao;

import java.util.List;

import com.common.dbaccess.bean.City;
import com.common.dbaccess.core.BaseDAO;

/**
 * CityDAO（视频07）：和 UserDAO 一个套路。
 *
 * 继承 BaseDAO<City> 之后，连接、预编译、结果集映射、关闭这些样板代码都不用写，
 * 这里只留业务 SQL：查全部城市。
 */
public class CityDAO extends BaseDAO<City> {

    /** 查询全部城市（视频07 只做了查询和删除，添加/修改后面再说） */
    public List<City> getCityList() {
        return selectList(City.class, "select * from city order by id");
    }

    /**
     * 添加城市（视频09）：搜索城市天气时，把城市名和编号存进 city 表。
     * city 列有唯一约束，重复插入会抛异常，老师就是用 try/catch + "已经存在了" 来提示。
     * @return 插入成功返回 true，城市已存在返回 false
     */
    public boolean insert(String city, String code) {
        String sql = "insert into city(city, code) values(?, ?)";
        try {
            return insert(sql, city, code) > 0;
        } catch (Exception e) {
            // 城市已经存在了，不用重复添加
            return false;
        }
    }
}
