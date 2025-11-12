package com.matheusbarbosase.uptime_monitor.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.matheusbarbosase.uptime_monitor.config.AbstractIntegrationTest;
import com.matheusbarbosase.uptime_monitor.dto.CreateTargetRequest;
import com.matheusbarbosase.uptime_monitor.dto.UpdateTargetRequest;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
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

    private User user1;
    private User user2;
    private Target target1;
    private Target target2;

    @BeforeEach
    void setUp() {
        healthCheckRepository.deleteAll();
        targetRepository.deleteAll();
        userRepository.deleteAll();

        user1 = new User();
        user1.setUsername("user-1");
        user1.setEmail("user1@test.com");
        user1.setPassword(passwordEncoder.encode("pass123"));
        userRepository.save(user1);

        user2 = new User();
        user2.setUsername("user-2");
        user2.setEmail("user2@test.com");
        user2.setPassword(passwordEncoder.encode("pass123"));
        userRepository.save(user2);

        target1 = new Target();
        target1.setName("Target de User 1");
        target1.setUrl("http://user1.com");
        target1.setCheckInterval(5);
        target1.setUser(user1);
        target1.setLastStatus("PENDING");
        targetRepository.save(target1);

        target2 = new Target();
        target2.setName("Target de User 2");
        target2.setUrl("http://user2.com");
        target2.setCheckInterval(10);
        target2.setUser(user2);
        target2.setLastStatus("PENDING");
        targetRepository.save(target2);
    }

    @Test
    void whenGetTargetsAnonymously_thenShouldReturn403Forbidden() throws Exception {
        mockMvc.perform(get("/api/targets"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "user-1")
    void whenGetTargetsAsAuthenticatedUser_shouldOnlyReturnOwnedTargets() throws Exception {

        mockMvc.perform(get("/api/targets"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name", is("Target de User 1")));
    }

    @Test
    @WithMockUser(username = "user-1")
    void whenCreateTarget_thenTargetIsCreatedAndOwnedByUser() throws Exception {

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
                .andExpect(jsonPath("$.name", is("Novo Target")));

        List<Target> targetsInDb = targetRepository.findAllByUserId(user1.getId());
        assertThat(targetsInDb).hasSize(2);
    }

    @Test
    @WithMockUser(username = "user-1")
    void whenGetTargetById_asNonOwner_thenReturns404() throws Exception {
        mockMvc.perform(get("/api/targets/" + target2.getId()))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "user-1")
    void whenDeleteTarget_asNonOwner_thenReturns404() throws Exception {
        mockMvc.perform(delete("/api/targets/" + target2.getId()))
                .andExpect(status().isNotFound());

        assertThat(targetRepository.existsById(target2.getId())).isTrue();
    }

    @Test
    @WithMockUser(username = "user-1")
    void whenUpdateTarget_asOwner_thenUpdatesTarget() throws Exception {
        UpdateTargetRequest updateDto = new UpdateTargetRequest(
                "Target 1 Atualizado",
                "http://user1-updated.com",
                15
        );

        mockMvc.perform(
                        put("/api/targets/" + target1.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(updateDto))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Target 1 Atualizado")))
                .andExpect(jsonPath("$.checkInterval", is(15)));

        Target updatedTarget = targetRepository.findById(target1.getId()).get();
        assertThat(updatedTarget.getName()).isEqualTo("Target 1 Atualizado");
    }
}