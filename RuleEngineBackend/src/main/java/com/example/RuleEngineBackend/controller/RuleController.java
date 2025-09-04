package com.example.RuleEngineBackend.controller;


import com.example.RuleEngineBackend.model.Rule;
import com.example.RuleEngineBackend.model.User;
import com.example.RuleEngineBackend.repository.RuleRepository;
import com.example.RuleEngineBackend.service.SessionService;
import com.fasterxml.jackson.annotation.JsonInclude;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api/rule")
public class RuleController {

    // In-memory storage for the current rule and its AST
    private String currentRule = null;
    private Node currentAST = null;

    @Autowired
    private RuleRepository userRepository;

    // Node class definition (from ASTBuilder)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    static class Node {
        String type;
        String value;// 'operator' or 'operand'
        Node left;      // Left child (another Node)
        Node right;     // Right child (another Node)
           // Value for operand nodes (e.g., condition)

        public Node(String type, Node left, Node right, String value) {
            this.type = type;
            this.value = value;
            this.left = left;
            this.right = right;
        }
        public String getType() {
            return type;
        }
        public String getValue() {
            return value;
        }
        public Node getLeft() {
            return left;
        }

        public Node getRight() {
            return right;
        }



        @Override
        public String toString() {
            return "Node{" +
                    "type='" + type + '\'' +
                    ", left=" + left +
                    ", right=" + right +
                    ", value='" + value + '\'' +
                    '}';
        }
    }

    @Autowired
    private SessionService sessionService;

    // Sign in a user
    @GetMapping("/hello")
    public String helloRuleEnigne(){
        return "hello bud";
    }
    @PostMapping("/signin")
    public String signIn(@RequestParam String email) {
        sessionService.signIn(email);
        if (!userRepository.findByEmail(email).isPresent()) {
            // Create a new user if not already exists
            userRepository.save(new User(email, new ArrayList<>()));
        }
        return "Signed in as " + email;
    }

    // Sign out a user
    @PostMapping("/signout")
    public String signOut(@RequestParam String email) {
        sessionService.signOut(email);
        return "Signed out successfully.";
    }

    // Get all rules for the signed-in user
    @GetMapping("/getAll")
    public List<Rule> getRules(@RequestParam String email) {
        if (!sessionService.isSignedIn(email)) {
            throw new RuntimeException("User not signed in.");
        }

        Optional<User> user = userRepository.findByEmail(email);
        return user.map(User::getRules).orElse(List.of());
    }
    // Unified endpoint for both rule submission and evaluation
    @PostMapping
    public Map<String, Object> processRule(@RequestParam String email,@RequestBody Map<String, Object> payload) {
        Map<String, Object> response = new HashMap<>();

        if (!sessionService.isSignedIn(email)) {
            throw new RuntimeException("User not signed in.");
        }

        if (payload.containsKey("rule")) {
            // Rule is submitted
            String rule = (String) payload.get("rule");
            currentRule = rule;
            Node node = generateAST(rule);

            User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found."));

            // Add the new rule
            Rule newRule = new Rule(UUID.randomUUID().toString(), rule);
            user.getRules().add(newRule);

            // Save the updated user
            userRepository.save(user);

            response.put("ASt",node);
            response.put("rule", rule);
        }
        else {
            response.put("error", "Invalid request. Please provide  'rule'");
        }

        return response;
    }

    @PostMapping("/evaluate")
    public Map<String , Object> evaluateRule(@RequestBody Map<String, Object> payload){
        Map<String, Object> response = new HashMap<>();
        if(!payload.containsKey("rule") && !payload.containsKey("userData")){
            response.put("error", "Invalid request. Please provide either 'rule' or 'userData'.");
            return response;
        }
        String rule = (String) payload.get("rule");
        Node node = generateAST(rule);
        Map<String, Object> userData = (Map<String, Object>) payload.get("userData");
        boolean result = evaluateRule(node, userData);

        response.put("ast",node);
        response.put("rule", rule);
        response.put("userData", userData);
        response.put("result", result);
        return response;
    }

    @PostMapping("/combine")
    public Map<String, Object> combineRules(@RequestBody Map<String, Object> payload) {
        List<String> rules = (List<String>) payload.get("rules");
        Map<String, Object> userData = (Map<String, Object>) payload.get("userData");
        String operator = (String) payload.get("operator");

        // Combine the rules into one AST
        Node combinedAST = combineRules(rules, operator);

        // Evaluate the combined rule with the provided data
        boolean result = evaluateRule(combinedAST, userData);

        // Return the combined result
        Map<String, Object> response = new HashMap<>();
        response.put("combinedRules", rules);
        response.put("result", result);
        response.put("ast", combinedAST);
        return response;
    }
    @DeleteMapping("/{id}")
    public String deleteRule(@RequestParam String email, @PathVariable String id) {
        if (!sessionService.isSignedIn(email)) {
            throw new RuntimeException("User not signed in.");
        }

        // Find the user
        User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found."));

        // Remove the rule
        user.getRules().removeIf(rule -> rule.getId().equals(id));

        // Save the updated user
        userRepository.save(user);

        return "Rule deleted successfully.";
    }

    // ===== Helper methods from ASTBuilder (modified from previous example) ===== //

    public static Node generateAST(String rule) {
        Stack<Node> operandStack = new Stack<>();
        Stack<String> operatorStack = new Stack<>();

        String[] tokens = tokenizeRule(rule);

        for (String token : tokens) {
            token = token.trim();
            if (token.isEmpty()) continue;

            if (token.equals("(")) {
                operatorStack.push(token);
            } else if (token.equals(")")) {
                while (!operatorStack.isEmpty() && !operatorStack.peek().equals("(")) {
                    processOperator(operandStack, operatorStack.pop());
                }
                operatorStack.pop();
            } else if (token.equals("AND") || token.equals("OR")) {
                while (!operatorStack.isEmpty() && precedence(operatorStack.peek()) >= precedence(token)) {
                    processOperator(operandStack, operatorStack.pop());
                }
                operatorStack.push(token);
            } else {
                operandStack.push(new Node("operand", null, null, token));
            }
        }

        while (!operatorStack.isEmpty()) {
            processOperator(operandStack, operatorStack.pop());
        }

        return operandStack.pop();
    }

    public static boolean evaluateRule(Node node, Map<String, Object> data) {
        if (node.type.equals("operator")) {
            boolean leftResult = evaluateRule(node.left, data);
            boolean rightResult = evaluateRule(node.right, data);
            if (node.value.equals("AND")) {
                return leftResult && rightResult;
            } else if (node.value.equals("OR")) {
                return leftResult || rightResult;
            }
        } else if (node.type.equals("operand")) {
            return evaluateCondition(node.value, data);
        }
        return false;
    }

    private static boolean evaluateCondition(String condition, Map<String, Object> data) {
        String[] parts = condition.split(" ");
        if (parts.length != 3) return false;

        String attribute = parts[0];
        String operator = parts[1];
        String value = parts[2].replace("'", "").replace(")", "").trim();

        Object attributeValue = data.get(attribute);

        if (attributeValue instanceof Integer) {
            int intValue = (int) attributeValue;
            try {
                int comparisonValue = Integer.parseInt(value);
                return operator.equals(">") ? intValue > comparisonValue :
                        operator.equals("<") ? intValue < comparisonValue :
                                operator.equals("=") ? intValue == comparisonValue : false;
            } catch (NumberFormatException e) {
                return false;
            }
        } else if (attributeValue instanceof String) {
            return attributeValue.toString().toLowerCase().equals(value.toLowerCase());
        }

        return false;
    }

    private static void processOperator(Stack<Node> operandStack, String operator) {
        Node right = operandStack.pop();
        Node left = operandStack.pop();
        operandStack.push(new Node("operator", left, right, operator));
    }

    private static int precedence(String operator) {
        return operator.equals("AND") ? 2 : operator.equals("OR") ? 1 : 0;
    }

    private static String[] tokenizeRule(String rule) {
        return rule.split("(?<=\\()|(?=\\))|(?<=[^\\s]+(?=\\s+(AND|OR)))|(?<=(AND|OR))|(?=\\s+(AND|OR))");
    }
    public static Node combineRules(List<String> rules, String operator) {
        Node combinedAST = null;
        for (String rule : rules) {
            Node ruleAST = generateAST(rule);
            if (combinedAST == null) {
                combinedAST = ruleAST;
            } else {
                combinedAST = new Node("operator", combinedAST, ruleAST, operator);
            }
        }
        return combinedAST;
    }
}

