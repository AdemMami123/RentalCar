package com.carrental.modules.user.service;

import com.carrental.modules.user.dto.UserDTO;
import com.carrental.modules.user.entity.User;
import com.carrental.modules.user.mapper.UserMapper;
import com.carrental.modules.user.repository.UserRepository;
import com.carrental.shared.exceptions.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public List<UserDTO> getAllUsers() { return userRepository.findAll().stream().map(userMapper::toDTO).toList(); }
    public UserDTO getUserById(Long id) { return userMapper.toDTO(userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id))); }
    public UserDTO getUserByEmail(String email) { return userMapper.toDTO(userRepository.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email))); }

    @Transactional
    public UserDTO createUser(UserDTO dto) {
        if (userRepository.existsByEmail(dto.getEmail())) throw new IllegalArgumentException("User with email '" + dto.getEmail() + "' already exists");
        return userMapper.toDTO(userRepository.save(userMapper.toEntity(dto)));
    }

    @Transactional
    public UserDTO updateUser(Long id, UserDTO dto) {
        User user = userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        if (!user.getEmail().equals(dto.getEmail()) && userRepository.existsByEmail(dto.getEmail())) throw new IllegalArgumentException("User with email '" + dto.getEmail() + "' already exists");
        userMapper.updateEntityFromDTO(dto, user);
        return userMapper.toDTO(userRepository.save(user));
    }

    @Transactional
    public void deleteUser(Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        userRepository.delete(user);
    }
}