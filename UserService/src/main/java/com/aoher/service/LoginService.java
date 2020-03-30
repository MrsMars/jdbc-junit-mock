package com.aoher.service;

import com.aoher.model.User;
import com.aoher.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class LoginService {

    private static final String GET_USER_BY_CREDENTIALS = "select * from user where username='%s' and password='%s'";
    private static final String USERNAME_MAP_KEY = "username";
    private static final String PASSWORD_MAP_KEY = "password";

    private UserRepository userRepository = new UserRepository();

    public User login(Map<String, Object> credentials) {
        if (isInvalidCredential(credentials)) {
            return null;
        }

        String query = String.format(GET_USER_BY_CREDENTIALS, credentials.get(USERNAME_MAP_KEY), credentials.get(PASSWORD_MAP_KEY));
        List<User> users = userRepository.executeRetrieveQuery(query);
        return users.isEmpty() ? null : users.get(0);
    }

    private boolean isInvalidCredential(Map<String, Object> credentials) {
        return credentials == null || credentials.isEmpty() ||
                credentials.get(USERNAME_MAP_KEY) == null || credentials.get(PASSWORD_MAP_KEY) == null;
    }
}
