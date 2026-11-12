package dao;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import model.Account6;

/** Connection belongs to the caller; DAO closes only its own statements/result sets. */
public abstract class DAO6 {
    protected final Connection con;
    protected final Account6 sessionAccount;

    protected DAO6(Connection con, Account6 sessionAccount) {
        this.con = con;
        this.sessionAccount = sessionAccount;
    }

    /** Public catalog queries require a connection, not an authenticated account. */
    protected void requireConnection() throws SQLException {
        if (con == null || con.isClosed()) {
            throw new SQLException("Không có kết nối cơ sở dữ liệu.");
        }
    }

    public static Connection getConnection() throws SQLException {
        Properties properties = new Properties();
        try (InputStream input = DAO6.class.getClassLoader().getResourceAsStream("shopman.properties")) {
            if (input != null) properties.load(input);
        } catch (IOException e) {
            throw new SQLException("Không đọc được cấu hình ShopMan.", e);
        }
        String url = setting(properties, "shopman.jdbc.url", "SHOPMAN_JDBC_URL",
                "jdbc:mysql://localhost:3306/ShopMan?useUnicode=true&characterEncoding=UTF-8&serverTimezone=UTC");
        String user = setting(properties, "shopman.jdbc.user", "SHOPMAN_JDBC_USER", "root");
        String password = setting(properties, "shopman.jdbc.password", "SHOPMAN_JDBC_PASSWORD", "");
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new SQLException("Thiếu MySQL Connector/J trong WEB-INF/lib.", e);
        }
        return DriverManager.getConnection(url, user, password);
    }

    private static String setting(Properties properties, String key, String env, String fallback) {
        String value = System.getProperty(key);
        if (value == null) value = System.getenv(env);
        if (value == null) value = properties.getProperty(key);
        return value == null ? fallback : value;
    }
}
