package com.common.dbaccess.web;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.common.dbaccess.bean.User;
import com.common.dbaccess.dao.UserDAO;

/**
 * UserServlet（视频03~06）：用户模块的业务入口，现在继承自己封装的 TomHttpServlet。
 *
 * 路由规则（BaseHttpServlet.service 反射调用）：/user/xxx -> xxx(HttpServletRequest, HttpServletResponse)
 *   login / logout        登录、退出       （视频04）
 *   list / delete         查询列表、删除   （视频05）
 *   add                   打开添加/修改页面（视频05、06）
 *   submit                点击保存按钮     （视频06，添加和修改共用）
 *
 * 所有跳转统一用 open()，不再自己写 forward/redirect，也不再拼 WEB-INF 和 URL 编码。
 */
public class UserServlet extends TomHttpServlet {

    private UserDAO userDAO = new UserDAO();

    // ==================== 登录 / 退出（视频04） ====================

    public String login(HttpServletRequest req, HttpServletResponse resp) {
        String username = req.getParameter("username");
        String password = req.getParameter("password");

        User bean = new User();
        bean.setUsername(username);
        bean.setPassword(password);
        User user = userDAO.getUser4Login(bean);

        if (user != null) {
            HttpSession session = req.getSession();
            session.setAttribute("username", user.getUsername());
            // 视频09：身份也放进 session（key 用 status，和老师的 JSP 判断保持一致），
            // 菜单和页面按身份控制；查询条件单独用 request 的 username，两者不能混
            session.setAttribute("status", user.getRole());
            // 登录名放 session 给顶栏用；查询条件单独用 request 的 username，两者不能混
            req.setAttribute("retList", userDAO.getUserList());
            return open("/user/list.jsp");
        }
        // 中文提示直接写，open() 内部会按 UTF-8 编码，地址栏和页面上都不会乱码
        return open("/index.html?msg=用户名或密码错误");
    }

    public String logout(HttpServletRequest req, HttpServletResponse resp) {
        HttpSession session = req.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        return open("/index.html");
    }

    // ==================== 用户列表 / 删除（视频05） ====================

    public String list(HttpServletRequest req, HttpServletResponse resp) {
        String username = req.getParameter("username");
        // 视频07：查完要把关键字再送回页面，输入框才不会一提交就空了
        req.setAttribute("username", username == null ? "" : username.trim());
        req.setAttribute("retList", userDAO.getUserList(username));
        return open("/user/list.jsp");
    }

    public String delete(HttpServletRequest req, HttpServletResponse resp) {
        Integer id = getId(req);
        String msg;
        try {
            if (id != null && id > 0) {
                userDAO.deleteById("user", id);
                msg = "删除成功";
            } else {
                msg = "缺少 id，删除失败";
            }
        } catch (Exception e) {
            msg = "删除失败：" + e.getMessage();
        }
        return open("/user/list?msg=" + msg);
    }

    // ==================== 打开添加 / 修改页面（视频06） ====================

    /**
     * 添加和修改共用 add.jsp：
     *   /user/add         不带 id -> 面包屑「添加用户」，表单为空
     *   /user/add?id=5    带   id -> 面包屑「修改用户」，表单回填这个人
     * getId() 是 BaseHttpServlet 里封装好的简写（等价于 getIntParam(req,"id")）。
     */
    public String add(HttpServletRequest req, HttpServletResponse resp) {
        Integer id = getId(req);
        boolean isEdit = (id != null && id > 0);
        req.setAttribute("msg", isEdit ? "修改" : "添加");

        User b = new User();
        if (isEdit) {
            List<User> one = userDAO.getUserList(id);
            if (!one.isEmpty()) {
                b = one.get(0);
            }
        }
        req.setAttribute("b", b);
        return open("/user/add.jsp");
    }

    // ==================== 点击保存按钮（视频06） ====================

    /**
     * 表单提交：getParameter(req, User.class) 把请求参数一次性映射成 User，
     * 再由 insertOrUpdate 按有没有 id 决定是 insert 还是 update。
     */
    public String submit(HttpServletRequest req, HttpServletResponse resp) {
        User b = getParameter(req, User.class);
        if (b == null || b.getUsername() == null || b.getUsername().trim().isEmpty()
                || b.getPassword() == null || b.getPassword().trim().isEmpty()) {
            return jsAlert("请完善信息后再保存", resp);
        }

        // 账号不能重复：改的是自己不算冲突，改成别人已存在的账号要拦下来
        User exist = userDAO.getSingleUser(b.getUsername().trim());
        if (exist != null && exist.getId() != b.getId()) {
            return jsAlert(b.getUsername().trim() + "用户名已经存在了", resp);
        }

        try {
            userDAO.insertOrUpdate(b);
        } catch (Exception e) {
            e.printStackTrace();
            return jsAlert(b.getUsername().trim() + "用户名已经存在了", resp);
        }
        // 非 .jsp -> redirect，刷新列表不会重复提交
        return open("/user/list");
    }

    /** 兼容视频05 的旧提交地址 /user/save */
    public String save(HttpServletRequest req, HttpServletResponse resp) {
        return submit(req, resp);
    }

    // ==================== 视频03 遗留入口，保持可用 ====================

    public String queryList(HttpServletRequest req, HttpServletResponse resp) {
        try {
            List<User> list = userDAO.findAll();
            req.setAttribute("userList", list);
            req.setAttribute("dbState", "ok");
        } catch (Exception e) {
            req.setAttribute("dbState", "error");
            req.setAttribute("dbMsg", e.getMessage());
        }
        // 这个老 demo 页面放在 webapp 根下，不在 WEB-INF/user/ 里，所以直接转发
        return forward("userList.jsp");
    }
}
