package com.example.RuleEngineBackend.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "users")
public class User {

    @Id
    private String email;  // Email as the user identifier

    private List<Rule> rules;  // List of rules associated with the user

    public User(String email, List<Rule> rules) {
        this.email = email;
        this.rules = rules;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<Rule> getRules() {
        return rules;
    }

    public void setRules(List<Rule> rules) {
        this.rules = rules;
    }
}

