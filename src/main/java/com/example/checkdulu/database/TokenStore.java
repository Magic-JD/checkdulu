package com.example.checkdulu.database;

import com.example.checkdulu.data.AccessToken;
import com.example.checkdulu.exception.CheckDuluException.DatabaseException;
import org.springframework.stereotype.Component;

import java.sql.*;
import java.util.Optional;

@Component
public class TokenStore {
    private static final String DB_URL = "jdbc:sqlite:tokens.db";

    static {
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement()) {
            String createTableSQL = """
                CREATE TABLE IF NOT EXISTS token_store (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    access_token TEXT NOT NULL,
                    expiry_time BIGINT NOT NULL
                );
            """;
            stmt.execute(createTableSQL);
        } catch (SQLException e) {
            throw new DatabaseException("Failed to initialize database", e);
        }
    }

    public void saveToken(AccessToken accessToken) {
        String insertSQL = "INSERT INTO token_store (access_token, expiry_time) VALUES (?, ?);";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(insertSQL)) {
            clearTokens(conn);

            pstmt.setString(1, accessToken.token());
            pstmt.setLong(2, accessToken.timeStamp());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Failed to save token", e);
        }
    }

    public Optional<AccessToken> loadToken() {
        String querySQL = "SELECT access_token, expiry_time FROM token_store LIMIT 1;";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(querySQL)) {
            if (rs.next()) {
                return Optional.of(new AccessToken(rs.getLong("expiry_time"),
                        rs.getString("access_token")));
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to load token", e);
        }

        return Optional.empty();
    }

    private static void clearTokens(Connection conn) throws SQLException {
        String deleteSQL = "DELETE FROM token_store;";
        try (Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(deleteSQL);
        }
    }
}
