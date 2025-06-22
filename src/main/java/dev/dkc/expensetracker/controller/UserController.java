package dev.dkc.expensetracker.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import dev.dkc.expensetracker.exception.UserAlreadyExistsException;
import dev.dkc.expensetracker.model.User;
import dev.dkc.expensetracker.service.UserService;
import dev.dkc.expensetracker.util.SessionManager;



@RestController
@RequestMapping("/api/users")
public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping
    public ResponseEntity<Map<String, String>> createUser(@RequestBody User user) {
        try{
            user = userService.createUser(user);
        } catch (UserAlreadyExistsException e) {
            System.out.println(e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("msg", "User already exists with this email"));
        }
        return ResponseEntity.ok(
            Map.of(
                "userId", user.getUserId() , 
                "msg", "User created successfully")
            );
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<?> getUser(@PathVariable String id) {
        User user = userService.getUserById(id);
        if (user == null) {
            return ResponseEntity.status(404).body(Map.of("msg", "User not found"));
        }
        return ResponseEntity.ok(user);
    }

    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @PutMapping
    public ResponseEntity<?> updateUser(
            @RequestBody User user,
            @CookieValue(value = "sessionId", required = false) String sessionId) 
    {
        
        if (sessionId == null || !SessionManager.isValidSession(sessionId)) {
            return ResponseEntity.status(401).body(Map.of("msg", "Unauthorized"));
        }
        
        user = userService.updateUser(user, SessionManager.getUserId(sessionId));
        return ResponseEntity.ok(Map.of("msg", "User updated successfully", "user" , user.toMap()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable String id) {
        userService.deleteUser(id);
        System.out.println("User:" + id + " deleted successfully");
        return ResponseEntity.ok().body(
            Map.of(
                "msg", "User deleted successfully"
            )
        );
    }

}
