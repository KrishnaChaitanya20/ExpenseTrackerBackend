package dev.dkc.expensetracker.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import dev.dkc.expensetracker.model.User;
import dev.dkc.expensetracker.service.LoginService;
import dev.dkc.expensetracker.service.UserService;
import dev.dkc.expensetracker.util.SessionManager;



@RestController
@RequestMapping("/api")
public class LoginController {
    
    @Autowired
    private LoginService loginService;

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody User user) {

        if(user.getEmail() == null || user.getPassword() == null || user.getEmail().isEmpty() || user.getPassword().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("msg", "Email and password are required"));
        }

        if( loginService.validateUser(user) ) {
            user = userService.getUserByEmail(user.getEmail());
            ResponseCookie cookie = ResponseCookie.from("sessionId", SessionManager.createSession(user))
                    .path("/")
                    .maxAge(60 * 60 )
                    .httpOnly(true)
                    .sameSite("Lax")
                    .build();

            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.SET_COOKIE, cookie.toString());

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(Map.of(
                        "id", user.getUserId(),
                        "msg", "Login successful",
                        "user" , user.toMap()
                    ));
        } else {
            return ResponseEntity.status(401).body(Map.of(
                "msg", "Invalid email or password"
            ));
        }
    }
    

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@CookieValue(name = "sessionId", required = false) String sessionId) {

        if(sessionId==null || !SessionManager.isValidSession(sessionId)) {
            return ResponseEntity.status(401).body(Map.of("msg", "User is not logged in or session is invalid"));
        }
        SessionManager.deleteSession(sessionId);
        ResponseCookie cookie = ResponseCookie.from("sessionId", null)
                .path("/")
                .maxAge(0)
                .httpOnly(true)
                .build();
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(Map.of(
                    "msg", "Logout successful"
                ));
    }
    

    @GetMapping("/session")
    public ResponseEntity<?> getSession(@CookieValue(name = "sessionId", required = false) String sessionId) {
        if (sessionId == null) {
            return ResponseEntity.status(401).body(Map.of("msg", "sessionId is missing"));
        }
        else if( SessionManager.isValidSession(sessionId)) {
            String userId = SessionManager.getUserId(sessionId);
            User user = userService.getUserById(userId);
            if (user == null) {
                return ResponseEntity.status(404).body(Map.of("msg", "User not found"));
            }
            user.setPassword(null);
            return ResponseEntity.ok(user);
        } else {
            return ResponseEntity.status(401).body(Map.of("msg", "Session is invalid or expired"));
        }
    }
    
}
