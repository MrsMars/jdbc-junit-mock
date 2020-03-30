package com.aoher.service;

import com.aoher.model.User;
import com.aoher.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.List;

@Service
public class UserService {

    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    private static final String GET_USERS = "select * from user where role='user'";
    private static final String GET_ALL_USERS = "select * from user";
    private static final String GET_USER_BY_ID = "select * user where id=%d";
    private static final String CREATE_USER = "insert into user (firstName, lastName, dateOfBirth, username, password, role) values ('%s', '%s', '%s', '%s', '%s', 'user');";
    private static final String EDIT_USER = "update user set firstName='%s', lastName='%s', dateOfBirth='%s', password='%s' where id=%d";
    private static final String DELETE_USER = "delete from user where id='%d' and role='user'";
    private static final String TASK_SERVICE_URL = "http://localhost:8081/deleteTask/%d";

    private DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private UserRepository userRepository = new UserRepository();

    public List<User> getUsers() {
        return userRepository.executeRetrieveQuery(GET_USERS);
    }

    public User getUser(int id) {
        if (id < 1) return null;

        String query = String.format(GET_USER_BY_ID, id);
        List<User> users = userRepository.executeRetrieveQuery(query);

        return users.isEmpty() ? null : users.get(0);
    }

    public int createUser(User user) {
        if (user == null || doesUserHaveNullFields(user)) return 0;

        String formattedDOB = LocalDate.parse(dateFormat.format(user.getDateOfBirth())).plusDays(1).toString();
        String query = String.format(CREATE_USER, user.getFirstName(), user.getLastName(), formattedDOB, user.getUsername(), user.getPassword());

        return userRepository.executeUpdateQuery(query);
    }

    public int editUser(int id, User user) {
        if (id < 1 || user == null || doesUserHaveNullFields(user)) return 0;

        String formattedDOB = LocalDate.parse(dateFormat.format(user.getDateOfBirth())).plusDays(1).toString();
        String query = String.format(EDIT_USER, user.getFirstName(), user.getLastName(), formattedDOB, user.getPassword(), id);

        return userRepository.executeUpdateQuery(query);
    }

    public int deleteUser(int id) {
        if (id < 1 || !isDeleteUserTasksSuccessful(id)) return 0;

        String query = String.format(DELETE_USER, id);
        return userRepository.executeUpdateQuery(query);
    }

    public boolean isUniqueUsername(String username) {
        if (username == null || username.isEmpty())  return false;

        List<User> users = userRepository.executeRetrieveQuery(GET_ALL_USERS);
        return users.stream().noneMatch(u -> u.getUsername().equals(username));
    }

    private boolean doesUserHaveNullFields(User user) {
        return user.getFirstName() == null ||
                user.getLastName() == null ||
                user.getUsername() == null ||
                user.getPassword() == null ||
                user.getDateOfBirth() == null;
    }

    private boolean isDeleteUserTasksSuccessful(int userId) {
        try {
            URL url = new URL(String.format(TASK_SERVICE_URL, userId));
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("DELETE");
            int responseCode = connection.getResponseCode();
            if (responseCode != -1)
                return true;
        } catch (IOException e) {
            log.error("IOException => {}", e.getMessage());
        }
        return false;
    }
}
