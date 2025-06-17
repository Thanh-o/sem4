package org.example.apitest.service;

import org.example.apitest.dto.*;
import org.example.apitest.model.User;
import org.example.apitest.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public User registerUser(RegisterRequest request) {
        // Check if username already exists
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username đã tồn tại");
        }

        // Check if email already exists
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email đã tồn tại");
        }

        // Create new user
        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        return userRepository.save(user);
    }

    public User loginUser(LoginRequest request) {
        // Find user by username or email
        Optional<User> userOpt;
        if (request.getUsername().contains("@")) {
            userOpt = userRepository.findByEmail(request.getUsername());
        } else {
            userOpt = userRepository.findByUsername(request.getUsername());
        }

        if (userOpt.isEmpty()) {
            throw new RuntimeException("User không tồn tại");
        }

        User user = userOpt.get();

        // Check password
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Mật khẩu không đúng");
        }

        return user;
    }

    public User updateProfile(Long id, UpdateProfileRequest request) {
        // Check if user exists
        Optional<User> userOpt = userRepository.findById(id);
        if (userOpt.isEmpty()) {
            throw new RuntimeException("User không tồn tại");
        }

        User user = userOpt.get();

        // Check if new username already exists (if changed)
        if (!user.getUsername().equals(request.getUsername()) &&
                userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username đã tồn tại");
        }

        // Check if new email already exists (if changed)
        if (!user.getEmail().equals(request.getEmail()) &&
                userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email đã tồn tại");
        }

        // Update user info
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());

        return userRepository.save(user);
    }

    public void changePassword(ChangePasswordRequest request) {
        // Find user by id
        Optional<User> userOpt = userRepository.findById(request.getUserId());
        if (userOpt.isEmpty()) {
            throw new RuntimeException("User không tồn tại");
        }

        User user = userOpt.get();

        // Check old password
        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            throw new RuntimeException("Mật khẩu cũ không đúng");
        }

        // Update password
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }



    public User getUserById(Long id) {
        Optional<User> userOpt = userRepository.findById(id);
        if (userOpt.isEmpty()) {
            throw new RuntimeException("User không tồn tại");
        }
        return userOpt.get();
    }

    public void deleteUser(Long id) {
        // Check if user exists
        Optional<User> userOpt = userRepository.findById(id);
        if (userOpt.isEmpty()) {
            throw new RuntimeException("User không tồn tại");
        }

        userRepository.deleteById(id);
    }


    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
}
