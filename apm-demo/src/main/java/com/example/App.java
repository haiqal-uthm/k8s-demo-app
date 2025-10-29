package com.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

@SpringBootApplication
@RestController
public class App {
    
    private List<String> quotes = Arrays.asList(
        "The only way to do great work is to love what you do. - Steve Jobs",
        "Innovation distinguishes between a leader and a follower. - Steve Jobs",
        "Life is what happens to you while you're busy making other plans. - John Lennon",
        "The future belongs to those who believe in the beauty of their dreams. - Eleanor Roosevelt"
    );
    
    private Map<String, Integer> visitCounter = new HashMap<>();
    
    @GetMapping("/")
    public Map<String, Object> home() {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "🚀 Welcome to the Enhanced Spring Boot APM Demo with Session Management!");
        response.put("timestamp", LocalDateTime.now());
        response.put("version", "2.1");
        response.put("endpoints", Arrays.asList(
            "/health", "/quote", "/counter", "/user/{name}", "/calculate", "/random",
            "/session/info", "/session/set", "/session/get/{key}", "/session/all", 
            "/session/remove/{key}", "/session/invalidate"
        ));
        return response;
    }
    
    @GetMapping("/health")
    public Map<String, Object> health() {
        Map<String, Object> status = new HashMap<>();
        status.put("status", "UP");
        status.put("uptime", System.currentTimeMillis());
        status.put("memory", Runtime.getRuntime().freeMemory());
        status.put("timestamp", LocalDateTime.now());
        return status;
    }
    
    @GetMapping("/quote")
    public Map<String, Object> randomQuote() {
        String quote = quotes.get(ThreadLocalRandom.current().nextInt(quotes.size()));
        Map<String, Object> response = new HashMap<>();
        response.put("quote", quote);
        response.put("timestamp", LocalDateTime.now());
        return response;
    }
    
    @GetMapping("/counter")
    public Map<String, Object> getCounter() {
        Map<String, Object> response = new HashMap<>();
        response.put("totalVisits", visitCounter.values().stream().mapToInt(Integer::intValue).sum());
        response.put("uniqueVisitors", visitCounter.size());
        response.put("visitors", visitCounter);
        return response;
    }
    
    @GetMapping("/user/{name}")
    public Map<String, Object> greetUser(@PathVariable String name) {
        visitCounter.put(name, visitCounter.getOrDefault(name, 0) + 1);
        
        Map<String, Object> response = new HashMap<>();
        response.put("greeting", "Hello, " + name + "! 👋");
        response.put("visitCount", visitCounter.get(name));
        response.put("timestamp", LocalDateTime.now());
        return response;
    }
    
    @PostMapping("/calculate")
    public ResponseEntity<Map<String, Object>> calculate(@RequestBody Map<String, Object> request) {
        try {
            double num1 = Double.parseDouble(request.get("num1").toString());
            double num2 = Double.parseDouble(request.get("num2").toString());
            String operation = request.get("operation").toString();
            
            double result;
            switch (operation.toLowerCase()) {
                case "add":
                    result = num1 + num2;
                    break;
                case "subtract":
                    result = num1 - num2;
                    break;
                case "multiply":
                    result = num1 * num2;
                    break;
                case "divide":
                    if (num2 == 0) {
                        return ResponseEntity.badRequest().body(
                            Map.of("error", "Division by zero is not allowed")
                        );
                    }
                    result = num1 / num2;
                    break;
                default:
                    return ResponseEntity.badRequest().body(
                        Map.of("error", "Invalid operation. Use: add, subtract, multiply, divide")
                    );
            }
            
            Map<String, Object> response = new HashMap<>();
            response.put("num1", num1);
            response.put("num2", num2);
            response.put("operation", operation);
            response.put("result", result);
            response.put("timestamp", LocalDateTime.now());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                Map.of("error", "Invalid input: " + e.getMessage())
            );
        }
    }
    
    @GetMapping("/random")
    public Map<String, Object> randomData() {
        Map<String, Object> response = new HashMap<>();
        response.put("randomNumber", ThreadLocalRandom.current().nextInt(1, 101));
        response.put("randomBoolean", ThreadLocalRandom.current().nextBoolean());
        response.put("randomColor", getRandomColor());
        response.put("timestamp", LocalDateTime.now());
        return response;
    }
    
    private String getRandomColor() {
        String[] colors = {"Red", "Blue", "Green", "Yellow", "Purple", "Orange", "Pink", "Cyan"};
        return colors[ThreadLocalRandom.current().nextInt(colors.length)];
    }
    
    // Session-based endpoints
    @GetMapping("/session/info")
    public Map<String, Object> getSessionInfo(HttpSession session, HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();
        response.put("sessionId", session.getId());
        response.put("sessionCreated", new Date(session.getCreationTime()));
        response.put("sessionLastAccessed", new Date(session.getLastAccessedTime()));
        response.put("sessionMaxInactiveInterval", session.getMaxInactiveInterval());
        response.put("sessionIsNew", session.isNew());
        response.put("timestamp", LocalDateTime.now());
        return response;
    }
    
    @PostMapping("/session/set")
    public Map<String, Object> setSessionAttribute(@RequestBody Map<String, String> request, HttpSession session) {
        String key = request.get("key");
        String value = request.get("value");
        
        if (key != null && value != null) {
            session.setAttribute(key, value);
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Session attribute set successfully");
            response.put("key", key);
            response.put("value", value);
            response.put("sessionId", session.getId());
            response.put("timestamp", LocalDateTime.now());
            return response;
        } else {
            Map<String, Object> response = new HashMap<>();
            response.put("error", "Both 'key' and 'value' are required");
            return response;
        }
    }
    
    @GetMapping("/session/get/{key}")
    public Map<String, Object> getSessionAttribute(@PathVariable String key, HttpSession session) {
        Object value = session.getAttribute(key);
        
        Map<String, Object> response = new HashMap<>();
        response.put("key", key);
        response.put("value", value);
        response.put("sessionId", session.getId());
        response.put("found", value != null);
        response.put("timestamp", LocalDateTime.now());
        return response;
    }
    
    @GetMapping("/session/all")
    public Map<String, Object> getAllSessionAttributes(HttpSession session) {
        Map<String, Object> attributes = new HashMap<>();
        Enumeration<String> attributeNames = session.getAttributeNames();
        
        while (attributeNames.hasMoreElements()) {
            String attributeName = attributeNames.nextElement();
            attributes.put(attributeName, session.getAttribute(attributeName));
        }
        
        Map<String, Object> response = new HashMap<>();
        response.put("sessionId", session.getId());
        response.put("attributes", attributes);
        response.put("attributeCount", attributes.size());
        response.put("timestamp", LocalDateTime.now());
        return response;
    }
    
    @DeleteMapping("/session/remove/{key}")
    public Map<String, Object> removeSessionAttribute(@PathVariable String key, HttpSession session) {
        Object removedValue = session.getAttribute(key);
        session.removeAttribute(key);
        
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Session attribute removed");
        response.put("key", key);
        response.put("removedValue", removedValue);
        response.put("sessionId", session.getId());
        response.put("timestamp", LocalDateTime.now());
        return response;
    }
    
    @DeleteMapping("/session/invalidate")
    public Map<String, Object> invalidateSession(HttpSession session) {
        String sessionId = session.getId();
        session.invalidate();
        
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Session invalidated successfully");
        response.put("invalidatedSessionId", sessionId);
        response.put("timestamp", LocalDateTime.now());
        return response;
    }

    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }
}
