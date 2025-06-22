package com.santosh.bankingsystem.service.impl;

import com.santosh.bankingsystem.entity.User;
import com.santosh.bankingsystem.entity.UserRequestDTO;
import com.santosh.bankingsystem.entity.UserResponseDTO;
import com.santosh.bankingsystem.exceptions.ResourceNotFoundException;
import com.santosh.bankingsystem.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    private User user1;
    private User user2;

    @BeforeEach
    void setUp() {
        user1 = new User(1L, "john_doe", "john@example.com", "password123");
        user2 = new User(2L, "jane_doe", "jane@example.com", "password456");
    }

    @Test
    void testCreateUser_successfullyCreatesUser() {
        // Given
        UserRequestDTO requestDTO = new UserRequestDTO();
        requestDTO.setUsername("santosh");
        requestDTO.setEmail("santosh@mail.com");
        requestDTO.setPassword("plainPassword");

        User savedUser = new User(1L, "santosh", "santosh@mail.com", "encodedPassword");

        when(passwordEncoder.encode("plainPassword")).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        // When
        UserResponseDTO result = userService.createUser(requestDTO);

        // Then
        assertEquals(1L, result.getId());
        assertEquals("santosh", result.getUsername());
        assertEquals("santosh@mail.com", result.getEmail());

        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void testGetAllUser_successfullyGetAllUser() {
        List<User> userList = Arrays.asList(user1, user2);

        when(userRepository.findAll()).thenReturn(userList);

        List<UserResponseDTO> result = userService.getAllUsers();

        assertEquals(2,result.size());
        assertEquals("john_doe",result.get(0).getUsername());
        assertEquals("jane_doe",result.get(1).getUsername());

        verify(userRepository, times(1)).findAll();

    }

    @Test
    void testGetUserById_Success() {

        when(userRepository.findById(user1.getId())).thenReturn(Optional.of(user1));
        UserResponseDTO result = userService.getUserById(user1.getId());

        assertNotNull(result);
        assertEquals("john_doe",result.getUsername());
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    void testGetUserById_NotFound() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> userService.getUserById(99L));

        assertEquals("User not found with id: 99", exception.getMessage());

        verify(userRepository, times(1)).findById(99L);
    }


}