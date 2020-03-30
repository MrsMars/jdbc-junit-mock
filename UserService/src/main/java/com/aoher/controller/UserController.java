package com.aoher.controller;

import com.aoher.model.User;
import com.aoher.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins="*")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/getUsers")
    public List<User> getUsers() {
        return userService.getUsers();
    }

    @GetMapping("/getUser/{id}")
    public User getUser(@PathVariable int id) {
        return userService.getUser(id);
    }

    @PostMapping("/createUser")
    public void createUser(@RequestBody User user) {
        userService.createUser(user);
    }

    @PostMapping("/editUser/{id}")
    public void editUser(@RequestBody User user, @PathVariable int id) {
        userService.editUser(id, user);
    }

    @DeleteMapping("/deleteUser/{id}")
    public void deleteUser(@PathVariable int id) {
        userService.deleteUser(id);
    }

    @GetMapping("/isUniqueUsername/{username}")
    public boolean isUniqueUsername(@PathVariable String username) {
        return userService.isUniqueUsername(username);
    }
}
