package org.example.apitest;

import org.example.apitest.dto.RegisterRequest;
import org.example.apitest.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
public class SqlLoggingTest {
    
    @Autowired
    private UserService userService;
    
    @Test
    public void testUserRegistrationWithSqlLogging() {
        System.out.println("\n=== TESTING USER REGISTRATION WITH SQL LOGGING ===\n");
        
        // Test case 1: Valid user registration
        RegisterRequest validRequest = new RegisterRequest();
        validRequest.setUsername("testuser123");
        validRequest.setEmail("test@example.com");
        validRequest.setPassword("securepassword123");
        
        try {
            userService.registerUser(validRequest);
            System.out.println("✅ Valid user registration completed - Check logs above for SQL analysis");
        } catch (Exception e) {
            System.out.println("Expected exception for duplicate user: " + e.getMessage());
        }
        
        // Test case 2: Duplicate username
        try {
            userService.registerUser(validRequest);
        } catch (RuntimeException e) {
            System.out.println("✅ Duplicate username properly handled: " + e.getMessage());
        }
        
        System.out.println("\n=== SQL LOGGING TEST COMPLETED ===\n");
    }
}
