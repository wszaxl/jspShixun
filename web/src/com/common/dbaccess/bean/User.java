package com.common.dbaccess.bean;

/**
 * 用户实体（对应数据库 lizx.user 表）
 *
 * 字段命名约定：setter 方法去掉 "set" 前缀后即为数据库列名，
 * 由框架 BaseDAO + MysqlReflectMapping 通过反射自动完成 结果集 -> 实体 的映射。
 *   setId(int)      -> 列 id
 *   setUsername()   -> 列 username
 *   setPassword()   -> 列 password
 */
public class User {

    private int id;
    private String username;
    private String password;
    private String role;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    @Override
    public String toString() {
        return "User{id=" + id + ", username='" + username + "', password='" + password + "', role='" + role + "'}";
    }
}
