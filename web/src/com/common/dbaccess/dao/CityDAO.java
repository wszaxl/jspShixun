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
}
