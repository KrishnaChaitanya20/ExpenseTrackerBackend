package dev.dkc.expensetracker.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import dev.dkc.expensetracker.model.User;

@Service
public class LoginService {
    
    @Autowired
    private UserService userService;

    public boolean validateUser(User user) {
        User existingUser = userService.getUserByEmail(user.getEmail());
        return existingUser != null && existingUser.getPassword().equals(user.getPassword());
    }
}
