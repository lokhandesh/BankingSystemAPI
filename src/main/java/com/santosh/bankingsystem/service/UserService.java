package com.santosh.bankingsystem.service;

import com.santosh.bankingsystem.entity.User;
import com.santosh.bankingsystem.entity.UserRequestDTO;
import com.santosh.bankingsystem.entity.UserResponseDTO;

import java.util.List;
import java.util.Optional;

public interface UserService {
    UserResponseDTO createUser(UserRequestDTO dto);
    List<UserResponseDTO> getAllUsers();
    UserResponseDTO getUserById(Long id);
    Optional<User> findById(Long id);
}