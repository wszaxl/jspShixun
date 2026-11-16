package com.common.dbaccess.dao;

import java.util.List;

import com.common.dbaccess.bean.User;
import com.common.dbaccess.core.BaseDAO;

/**
 * UserDAO：继承通用泛型基类 BaseDAO<User>。
 *
 * 基类已经封装好 连接/预编译/执行/映射/关闭 等所有 JDBC 样板代码，
 * 这里只写“业务 SQL + 返回类型”即可，体现了 DAO 层“只关心数据与 SQL”的思想。
 */
public class UserDAO extends BaseDAO<User> {

    /** 查询 user 表全部记录 */
    public List<User> findAll() {
        return selectList(User.class, "select * from user");
    }

    /** 按主键 id 查询单条记录 */
    public List<User> findById(int id) {
        return selectList(User.class, "select * from user where id = ?", id);
    }

    /**
     * 登录校验（视频04）。
     * 用户名和密码两个条件一起丢给 SQL，查到就说明账号密码都对；查不到返回 null。
     * singleBySql 是 BaseDAO 提供的“取单条”方法，内部用 MysqlReflectMapping 反射映射成 User。
     */
    public User getUser4Login(User bean) {
        String sql = "select * from user where username = ? and password = ?";
        return singleBySql(User.class, sql, bean.getUsername(), bean.getPassword());
    }

    /** 按用户名查单条记录（视频04：登录成功后取完整用户信息） */
    public User getSingleUser(String username) {
        String sql = "select * from user where username = ?";
        return singleBySql(User.class, sql, username);
    }

    /** 用户列表（视频05）：不带条件，查全部 */
    public List<User> getUserList() {
        return selectList(User.class, "select * from user order by id");
    }

    /** 用户列表（视频05）：按用户名模糊查询，k 为空则等同于查全部 */
    public List<User> getUserList(String k) {
        if (k == null || k.trim().isEmpty()) {
            return getUserList();
        }
        String sql = "select * from user where username like ? order by id";
        return selectList(User.class, sql, "%" + k.trim() + "%");
    }

    /**
     * 用户列表（视频06 修改功能）：复用同一个方法做两种查询。
     * id 为空/0 -> 查全部；id 有值 -> 只查这个人，返回 List，取 get(0) 即可。
     * 基类 BaseDAO 里没有按 id 查单条的封装，查询类 SQL 得自己写。
     */
    public List<User> getUserList(Integer id) {
        if (id == null || id <= 0) {
            return getUserList();
        }
        return selectList(User.class, "select * from user where id = ? order by id", id);
    }

    /**
     * 插入或更新（视频06）：添加页和修改页共用同一个表单、同一个提交地址，
     * 靠表单里隐藏的 id 区分。
     *   id > 0  -> 有主键，是修改（4 个 ?，最后一个是 where id）
     *   id <= 0 -> 没主键，是添加（3 个 ?）
     * 注意：User 的 id 是 int 基本类型，表单没带 id 时框架的 JsonAdapt 会给 0，
     * 所以不能写成 id != null，否则添加会误走进 update，这正是视频里老师踩的坑。
     */
    public int insertOrUpdate(User b) {
        String role = (b.getRole() == null || b.getRole().trim().isEmpty()) ? "用户" : b.getRole().trim();
        if (b.getId() > 0) {
            String sql = "update user set username = ?, password = ?, role = ? where id = ?";
            return updateBySql(sql, b.getUsername().trim(), b.getPassword(), role, b.getId());
        }
        String sql = "insert into user(username, password, role) values(?, ?, ?)";
        return insert(sql, b.getUsername().trim(), b.getPassword(), role);
    }
}
