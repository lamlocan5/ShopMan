package dao;

import java.sql.*;
import model.Account6;

/** Optional account login support; public product search does not depend on login. */
public class AccountDAO6 extends DAO6 {
    public AccountDAO6(Connection con, Account6 sessionAccount) { super(con, sessionAccount); }
    public Account6 checkLogin(String username, String password) throws SQLException {
        if (username == null || password == null || username.trim().isEmpty()
                || username.length() > 255 || password.length() > 1024) return null;
        String sql = "SELECT a.* FROM tblAccount6 a JOIN tblCustomer6 c ON c.accountRefId=a.id"
                + " WHERE a.username=? AND a.role='CUSTOMER' AND a.status='ACTIVE'";
        try (PreparedStatement statement = con.prepareStatement(sql)) {
            statement.setString(1, username.trim());
            try (ResultSet rs = statement.executeQuery()) {
                if (!rs.next() || !PasswordHash6.verify(password, rs.getString("passwordHash"))) return null;
                Account6 account = new Account6(); account.setId(rs.getLong("id"));
                account.setUsername(rs.getString("username")); account.setRole(rs.getString("role"));
                account.setStatus(rs.getString("status"));
                // Deliberately do not place passwordHash in the session account.
                return account;
            }
        }
    }
}
