package com.example.RuleEngineBackend.service;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class SessionService {
    private final Map<String, String> sessionStore = new HashMap<>();  // email -> session

    public void signIn(String email) {
        sessionStore.put(email, email);  // Store session using email
    }

    public void signOut(String email) {
        sessionStore.remove(email);  // Remove session
    }

    public boolean isSignedIn(String email) {
        return sessionStore.containsKey(email);
    }
}

