package com.example.RuleEngineBackend.model;

public class Rule {

    private String id;  // MongoDB ID or any identifier
    private String ruleString;

    public Rule(String id, String ruleString) {
        this.id = id;
        this.ruleString = ruleString;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRuleString() {
        return ruleString;
    }

    public void setRuleString(String ruleString) {
        this.ruleString = ruleString;
    }
}

