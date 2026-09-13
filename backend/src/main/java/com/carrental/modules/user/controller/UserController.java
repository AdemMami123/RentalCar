package com.carrental.modules.user.controller;

import com.carrental.modules.user.dto.UserDTO;
import com.carrental.modules.user.service.UserService;
import com.carrental.shared.dtos.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class UserController {
    private final UserService userService;

    @GetMapping public ResponseEntity<ApiResponse<List<UserDTO>>> getAll() { return ResponseEntity.ok(ApiResponse.success(userService.getAllUsers(), "Users retrieved successfully")); }
    @GetMapping("/{id}") public ResponseEntity<ApiResponse<UserDTO>> getById(@PathVariable Long id) { return ResponseEntity.ok(ApiResponse.success(userService.getUserById(id), "User retrieved successfully")); }
    @GetMapping("/search/email") public ResponseEntity<ApiResponse<UserDTO>> getByEmail(@RequestParam String email) { return ResponseEntity.ok(ApiResponse.success(userService.getUserByEmail(email), "User retrieved successfully")); }
    @PostMapping public ResponseEntity<ApiResponse<UserDTO>> create(@Valid @RequestBody UserDTO dto) { return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(userService.createUser(dto), "User created successfully")); }
    @PutMapping("/{id}") public ResponseEntity<ApiResponse<UserDTO>> update(@PathVariable Long id, @Valid @RequestBody UserDTO dto) { return ResponseEntity.ok(ApiResponse.success(userService.updateUser(id, dto), "User updated successfully")); }
    @DeleteMapping("/{id}") public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) { userService.deleteUser(id); return ResponseEntity.ok(ApiResponse.success(null, "User deleted successfully")); }
}