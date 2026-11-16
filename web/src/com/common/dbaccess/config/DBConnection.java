package com.common.dbaccess.config;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

/**
 * 数据库连接（视频07 改造）：账号密码不再写死在 Java 代码里。
 *
 * 读取顺序：环境变量 > classpath 根目录的 db.properties > 空值
 *   - db.properties 放在 src 下，由 .gitignore 排除，只在本机存在，不会进仓库
 *   - 换一台机器只要复制 db.properties.example 为 db.properties 改密码，不用动代码
 *   - 也可以用环境变量 DB_DATABASE / DB_USERNAME / DB_PASSWORD 覆盖，适合不落文件的情况
 */
public class DBConnection {
	static String database;
	static String username;
	static String password;

	static {
		Properties prop = new Properties();
		// classpath 根目录：部署后就是 WEB-INF/classes/db.properties
		InputStream in = DBConnection.class.getClassLoader().getResourceAsStream("db.properties");
		if (in != null) {
			try {
				prop.load(in);
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		// 环境变量优先，配置文件其次
		database = pick("DB_DATABASE", prop.getProperty("db.database"), "lizx");
		username = pick("DB_USERNAME", prop.getProperty("db.username"), "root");
		password = pick("DB_PASSWORD", prop.getProperty("db.password"), "");
		if (password.isEmpty()) {
			System.err.println("[DBConnection] 未读到数据库密码：请把 src/db.properties.example 复制为 db.properties 并填密码，或设置环境变量 DB_PASSWORD");
		}
		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/** 环境变量 > 配置文件 > 默认值 */
	private static String pick(String envKey, String propValue, String defaultValue) {
		String env = System.getenv(envKey);
		if (env != null && !env.trim().isEmpty()) {
			return env.trim();
		}
		if (propValue != null && !propValue.trim().isEmpty()) {
			return propValue.trim();
		}
		return defaultValue;
	}

	public Connection getConnection() {
		String url = "jdbc:mysql://127.0.0.1:3306/" + database + "?useUnicode=true&characterEncoding=utf8&autoReconnect=true&failOverReadOnly=false&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=Asia/Shanghai";
		try {
			return DriverManager.getConnection(url, username, password);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}


	public void closeConnection(Connection conn) {
		try {
			if (conn != null) {
				conn.close();
				conn = null;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void closeStatement(Statement stmt) {
		try {
			if (stmt != null) {
				stmt.close();
				stmt = null;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void closeResultSet(ResultSet rs) {
		try {
			if (rs != null) {
				rs.close();
				rs = null;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void commitTrans(Connection conn) {
		try {
			if (conn != null) {
				conn.commit();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void rollbackTrans(Connection conn) {
		try {
			if (conn != null) {
				conn.rollback();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}
