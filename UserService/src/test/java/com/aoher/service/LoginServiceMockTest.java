package com.aoher.service;

import com.aoher.model.User;
import com.aoher.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.Silent.class)
class LoginServiceMockTest {

    private static final String GET_USER_BY_CREDENTIALS = "select * from user where username='%s' and password='%s'";
    private static final String USERNAME_MAP_KEY = "username";
    private static final String PASSWORD_MAP_KEY = "password";

    @Mock
    private UserRepository userRepositoryMock;

    @InjectMocks
    private LoginService loginService;

    private User user;
    private Map<String, Object> validCredentials;
    private Map<String, Object> nullCredentials;
    private Map<String, Object> invalidCredentials;

    @BeforeEach
    void setUp() {
        user = new User(1, "bruce", "wayne",
                "admin", "password", new Date(), "user");
        validCredentials = new HashMap<>();
        nullCredentials = new HashMap<>();
        invalidCredentials = new HashMap<>();

        validCredentials.put(USERNAME_MAP_KEY, "admin");
        validCredentials.put(PASSWORD_MAP_KEY, "password");
        nullCredentials.put(USERNAME_MAP_KEY, null);
        nullCredentials.put(PASSWORD_MAP_KEY, null);
        invalidCredentials.put(USERNAME_MAP_KEY, "test");
        invalidCredentials.put(PASSWORD_MAP_KEY, "invalidpassword");
    }

    @Test
    void testLoginForNullOrEmptyCredentialsMap() {
        assertNull(loginService.login(null));
        assertNull(loginService.login(new HashMap<>()));
    }

    @Test
    void testLoginWithNoUsersInDatabase() {
        String query = String.format(GET_USER_BY_CREDENTIALS, "admin", "password");

        when(userRepositoryMock.executeRetrieveQuery(query)).thenReturn(new ArrayList<>());

        assertNull(loginService.login(validCredentials));
        assertNull(loginService.login(nullCredentials));
        assertNull(loginService.login(invalidCredentials));
    }

    @Test
    public void testLoginWithUsersInDatabase() {
        String query = String.format(GET_USER_BY_CREDENTIALS, "admin", "password");
        when(userRepositoryMock.executeRetrieveQuery(query)).thenReturn(Collections.singletonList(user));
        assertEquals(user, loginService.login(validCredentials));
        assertNotEquals(user, loginService.login(nullCredentials));
        assertNotEquals(user, loginService.login(invalidCredentials));
    }
}