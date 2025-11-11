package com.matheusbarbosase.uptime_monitor.controller;

import com.matheusbarbosase.uptime_monitor.service.TargetService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import static org.mockito.Mockito.when;

@SpringBootTest
@AutoConfigureMockMvc
class TargetControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TargetService targetService;

    @Test
    void whenGetTargetsAnonymously_thenShouldReturn403Forbidden() throws Exception {

        mockMvc.perform(
                        get("/api/targets")
                )
                .andExpect(
                        status().isForbidden()
                );
    }

    @Test
    @WithMockUser(username = "test-user")
    void whenGetTargetsAsAuthenticatedUser_thenShouldReturnOkAndEmptyList() throws Exception {

        when(targetService.findAllTargets())
                .thenReturn(Collections.emptyList());

        mockMvc.perform(
                        get("/api/targets")
                )
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }
}