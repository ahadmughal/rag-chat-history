package com.rag.chat.repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Repository for User entities — handles CRUD against the users table.
 * MIX OF GOOD CODE + ISSUES — for AI-MR-Reviewer testing.
 */
public class UserRepository {

    // === ISSUE (HIGH): hardcoded credentials in source ===
    private static final String DB_URL = "jdbc:mysql://prod-db.example.com:3306/users";
    private static final String DB_USER = "admin";
    private static final String DB_PASSWORD = "Admin@123!Prod";

    // === GOOD: clean constant, properly named ===
    private static final int DEFAULT_PAGE_SIZE = 50;

    // === GOOD: try-with-resources, parameterized query, no leaks ===
    public User findById(long id) throws SQLException {
        String sql = "SELECT id, email, name FROM users WHERE id = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new User(rs.getLong("id"), rs.getString("email"), rs.getString("name"));
                }
                return null;
            }
        }
    }

    // === ISSUE (HIGH): SQL injection via string concatenation ===
    public List<User> searchByEmail(String emailFragment) throws SQLException {
        List<User> results = new ArrayList<>();
        String sql = "SELECT id, email, name FROM users WHERE email LIKE '%" + emailFragment + "%'";
        Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(sql);
        // === ISSUE (HIGH): resource leak — conn/stmt/rs never closed ===
        while (rs.next()) {
            results.add(new User(rs.getLong("id"), rs.getString("email"), rs.getString("name")));
        }
        return results;
    }

    // === ISSUE (HIGH): empty catch block swallows the error ===
    public boolean deleteById(long id) {
        String sql = "DELETE FROM users WHERE id = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
        }
        return false;
    }

    // === ISSUE (MID): System.out used instead of logger ===
    public int countActive() throws SQLException {
        String sql = "SELECT COUNT(*) FROM users WHERE active = true";
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                int count = rs.getInt(1);
                System.out.println("Active user count: " + count);
                return count;
            }
            return 0;
        }
    }

    // === GOOD: clean pagination logic ===
    public List<User> listPage(int pageNumber) throws SQLException {
        String sql = "SELECT id, email, name FROM users ORDER BY id LIMIT ? OFFSET ?";
        List<User> results = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, DEFAULT_PAGE_SIZE);
            stmt.setInt(2, pageNumber * DEFAULT_PAGE_SIZE);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    results.add(new User(rs.getLong("id"), rs.getString("email"), rs.getString("name")));
                }
            }
        }
        return results;
    }

    // === ISSUE (LOW): TODO comment left in code ===
    // TODO: add caching layer before next release
    public boolean exists(long id) throws SQLException {
        return findById(id) != null;
    }
}

