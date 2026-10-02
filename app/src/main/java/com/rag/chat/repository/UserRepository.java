package com.rag.chat.repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Repository for User entities — handles CRUD against the users table.
 */
public class UserRepository {

    private static final Logger log = LoggerFactory.getLogger(UserRepository.class);

    // Credentials are now loaded from environment variables (no secrets in source).
    private static final String DB_URL = System.getenv("DB_URL");
    private static final String DB_USER = System.getenv("DB_USER");
    private static final String DB_PASSWORD = System.getenv("DB_PASSWORD");

    private static final int DEFAULT_PAGE_SIZE = 50;

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

    // Now uses PreparedStatement (no SQL injection) and try-with-resources (no leaks).
    public List<User> searchByEmail(String emailFragment) throws SQLException {
        List<User> results = new ArrayList<>();
        String sql = "SELECT id, email, name FROM users WHERE email LIKE ?";
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, "%" + emailFragment + "%");
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    results.add(new User(rs.getLong("id"), rs.getString("email"), rs.getString("name")));
                }
            }
        }
        return results;
    }

    // Empty catch removed — SQLException now propagates to the caller, consistent with other methods.
    public boolean deleteById(long id) throws SQLException {
        String sql = "DELETE FROM users WHERE id = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            return stmt.executeUpdate() > 0;
        }
    }

    // Replaced System.out.println with structured logger.
    public int countActive() throws SQLException {
        String sql = "SELECT COUNT(*) FROM users WHERE active = true";
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                int count = rs.getInt(1);
                log.info("Active user count: {}", count);
                return count;
            }
            return 0;
        }
    }

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

    public boolean exists(long id) throws SQLException {
        return findById(id) != null;
    }
}