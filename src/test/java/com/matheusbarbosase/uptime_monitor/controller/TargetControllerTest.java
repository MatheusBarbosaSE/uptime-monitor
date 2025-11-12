package com.matheusbarbosase.uptime_monitor.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.matheusbarbosase.uptime_monitor.config.AbstractIntegrationTest;
import com.matheusbarbosase.uptime_monitor.dto.CreateTargetRequest;
import com.matheusbarbosase.uptime_monitor.model.Target;
import com.matheusbarbosase.uptime_monitor.model.User;
import com.matheusbarbosase.uptime_monitor.repository.HealthCheckRepository;
import com.matheusbarbosase.uptime_monitor.repository.TargetRepository;
import com.matheusbarbosase.uptime_monitor.repository.UserRepository;
import com.matheusbarbosase.uptime_monitor.service.DynamicTaskScheduler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class TargetControllerTest extends AbstractIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TargetRepository targetRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private HealthCheckRepository healthCheckRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private DynamicTaskScheduler taskScheduler;


    @BeforeEach
    void setUp() {
        healthCheckRepository.deleteAll();
        targetRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void whenGetTargetsAnonymously_thenShouldReturn403Forbidden() throws Exception {
        mockMvc.perform(get("/api/targets"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "user-1")
    void whenGetTargetsAsAuthenticatedUser_shouldOnlyReturnOwnedTargets() throws Exception {

        User user1 = new User();
        user1.setUsername("user-1");
        user1.setEmail("user1@test.com");
        user1.setPassword(passwordEncoder.encode("pass123"));
        userRepository.save(user1);

        User user2 = new User();
        user2.setUsername("user-2");
        user2.setEmail("user2@test.com");
        user2.setPassword(passwordEncoder.encode("pass123"));
        userRepository.save(user2);

        Target targetUser1 = new Target();
        targetUser1.setName("Target de User 1");
        targetUser1.setUrl("http://user1.com");
        targetUser1.setCheckInterval(5);
        targetUser1.setUser(user1);
        targetUser1.setLastStatus("PENDING");
        targetRepository.save(targetUser1);

        Target targetUser2 = new Target();
        targetUser2.setName("Target de User 2");
        targetUser2.setUrl("http://user2.com");
        targetUser2.setCheckInterval(10);
        targetUser2.setUser(user2);
        targetUser2.setLastStatus("PENDING");
        targetRepository.save(targetUser2);

        mockMvc.perform(get("/api/targets"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name", is("Target de User 1")));
    }

    @Test
    @WithMockUser(username = "user-test")
    void whenCreateTarget_thenTargetIsCreatedAndOwnedByUser() throws Exception {

        User user = new User();
        user.setUsername("user-test");
        user.setEmail("user@test.com");
        user.setPassword(passwordEncoder.encode("pass123"));
        User savedUser = userRepository.save(user);

        CreateTargetRequest requestDto = new CreateTargetRequest(
                "Novo Target",
                "http://novo.com",
                10
        );

        mockMvc.perform(
                        post("/api/targets")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(requestDto))
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name", is("Novo Target")))
                .andExpect(jsonPath("$.checkInterval", is(10)));

        List<Target> targetsInDb = targetRepository.findAll();
        assertThat(targetsInDb).hasSize(1);
        assertThat(targetsInDb.get(0).getName()).isEqualTo("Novo Target");
        assertThat(targetsInDb.get(0).getUser().getId()).isEqualTo(savedUser.getId());
    }
}