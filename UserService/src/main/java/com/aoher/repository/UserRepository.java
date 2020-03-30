package com.aoher.repository;

import com.aoher.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static com.aoher.model.User.*;
import static com.aoher.util.Constants.DB_CONNECTION_EXCEPTION;

public class UserRepository {

    private static final Logger log = LoggerFactory.getLogger(UserRepository.class);

    @Value("${db.url}")
    private String dbUrl;

    @Value("${db.username}")
    private String dbUsername;

    @Value("${db.password}")
    private String dbPassword;

    public List<User> executeRetrieveQuery(String query) {
        try (Connection connection = DriverManager.getConnection(dbUrl, dbUsername, dbPassword);
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {

            return convertResultSetToUserList(resultSet);
        } catch (SQLException e) {
            log.error("SQLException => {}", e.getMessage());
            throw new IllegalArgumentException(DB_CONNECTION_EXCEPTION, e);
        }
    }

    public int executeUpdateQuery(String query) {
        try (Connection connection = DriverManager.getConnection(dbUrl, dbUsername, dbPassword);
            Statement statement = connection.createStatement()) {

            return statement.executeUpdate(query);
        } catch (SQLException e) {
            throw new IllegalStateException(DB_CONNECTION_EXCEPTION, e);
        }
    }

    private static List<User> convertResultSetToUserList(ResultSet rs) {
        List<User> users = new ArrayList<>();

        if (rs == null) {
            return users;
        }

        try {
            while (rs.next()) {
                users.add(new User(
                        rs.getInt(ID_FIELD),
                        rs.getString(FIRST_NAME_FIELD),
                        rs.getString(LAST_NAME_FIELD),
                        rs.getString(USERNAME_FIELD),
                        rs.getString(PASSWORD_FIELD),
                        rs.getDate(DATE_OF_BIRTH_FIELD),
                        rs.getString(ROLE_FIELD)));
            }
        } catch (SQLException e) {
            log.error("SQLException => {}", e.getMessage());
        }
        return users;
    }
}
