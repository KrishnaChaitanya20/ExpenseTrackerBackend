package dev.dkc.expensetracker.util;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

import dev.dkc.expensetracker.model.User;

public class SessionManager {

    private static HashMap<String, Session> userSessions = new HashMap<>();
    private static HashMap<String, User> currentUsers = new HashMap<>();
    private static final long SESSION_EXPIRATION_TIME = 3600000; 

    static class Session {
        String userId;
        long creationTime;

        Session(String userId) {
            this.userId = userId;
            this.creationTime = System.currentTimeMillis();
        }
    }

    public static String createSession(User user) {
        String userId = user.getUserId();
        cleanupExpiredSessions();
        
        String sessionId = UUID.randomUUID().toString();
        userSessions.put(sessionId, new Session(userId));
        currentUsers.put(userId, user);
        return sessionId;
    }

    public static String getUserId(String sessionId) {
        cleanupExpiredSessions();

        Session session = userSessions.get(sessionId);
        return (session != null && isSessionValid(session)) ? session.userId : null;
    }

    public static User getUser(String sessionId) {
        String userId = getUserId(sessionId);
        return userId != null ? currentUsers.get(userId) : null;
    }

    public static void deleteSession(String sessionId) {
        userSessions.remove(sessionId);
        cleanupExpiredSessions();
    }

    public static boolean isValidSession(String sessionId) {
        cleanupExpiredSessions();

        Session session = userSessions.get(sessionId);
        return session != null && isSessionValid(session);
    }

    private static void cleanupExpiredSessions() {
        Iterator<Map.Entry<String, Session>> iterator = userSessions.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, Session> entry = iterator.next();
            if (isSessionExpired(entry.getValue())) {
                currentUsers.remove(entry.getValue().userId);
                iterator.remove();
            }
        }
    }

    private static boolean isSessionExpired(Session session) {
        return System.currentTimeMillis() - session.creationTime > SESSION_EXPIRATION_TIME;
    }

    private static boolean isSessionValid(Session session) {
        return !isSessionExpired(session);
    }

    public static void clearSessions() {
        userSessions.clear();
    }
}
