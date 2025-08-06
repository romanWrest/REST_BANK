package com.example.bankcards.controller;

import com.example.bankcards.dto.User.UserDTO;
import com.example.bankcards.entity.enums.RoleUsers;
import com.example.bankcards.security.jwt.JwtFilter;
import com.example.bankcards.service.CardService;
import com.example.bankcards.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
public class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CardService cardService;

    @MockBean
    private JwtFilter jwtFilter;

    @MockBean
    private UserService userService;

    @Test
    @WithMockUser(roles = "ADMIN")
    void getAllUsers_ShouldReturnPageOfUsers() throws Exception {

        Pageable pageable = PageRequest.of(0, 10);
        UserDTO userDTO = new UserDTO()
                .setEmail("user@example.com")
                .setRole(RoleUsers.ROLE_USER)
                .setFullName("Userov");
        Page<UserDTO> page = new PageImpl<>(Collections.singletonList(userDTO), pageable, 1);

        given(userService.getAllUsers(any(Pageable.class))).willReturn(page);


        mockMvc.perform(get("/api/users/all")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].email").value(userDTO.getEmail()));
    }
}
