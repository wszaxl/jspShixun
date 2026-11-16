package com.common.dbaccess.bean;

/**
 * City（视频07）：城市表的实体类，和 lizx.city 表一一对应。
 *
 * 字段照数据表抄：id 自增主键，city 城市名称（不能为空、唯一），code 城市编号（允许为空）。
 * MysqlReflectMapping 是按 setter 名去掉 set 去匹配列名的，所以 setCity / setCode 正好对上 city / code 列。
 */
public class City {

    private int id;
    private String city;
    private String code;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Override
    public String toString() {
        return "City{id=" + id + ", city='" + city + "', code='" + code + "'}";
    }
}
