package com.santosh.bankingsystem.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.santosh.bankingsystem.entity.User;
import com.santosh.bankingsystem.entity.UserRequestDTO;
import com.santosh.bankingsystem.repository.UserRepository;
import com.santosh.bankingsystem.security.JwtUtil;
import com.santosh.bankingsystem.security.TestSecurityConfig;
import com.santosh.bankingsystem.service.UserService;
import com.santosh.bankingsystem.userservice.impl.UserDetailsServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.Commit;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@ActiveProfiles("test")
@SpringBootTest
//@WebMvcTest(UserController.class)
@Import(TestSecurityConfig.class)
class UserControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @MockBean
    private UserDetailsServiceImpl userDetailsService;

    //@MockBean
   // private UserService userService;

    @BeforeEach
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    void setup() {
      //  userRepository.deleteAll();
     //   userRepository.saveAndFlush(new User(null, "john3", "john1@example.com", "encoded"));
    }

    @Test
    void testCreateUser_Success() throws Exception {
        UserRequestDTO dto = new UserRequestDTO();
        dto.setUsername("abcde");
        dto.setEmail("abcd@example.com");
        dto.setPassword("pass123");

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.username").value("abcde"));
    }

    @Test
    @Rollback(false)
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    void testGetAllUsers() throws Exception {
        // setup data
      //  userRepository.saveAndFlush(new User(null, "john22", "john22@example.com", "encoded"));

        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[*].username", hasItem("john3")));
               // .andExpect(jsonPath("$.data[*].username").value("john3"));
    }

}