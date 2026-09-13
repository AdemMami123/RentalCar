package com.carrental.modules.auth;

import com.carrental.modules.user.entity.User;
import com.carrental.modules.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AuthIntegrationTest {
    @Autowired private MockMvc mockMvc;
    @Autowired private UserRepository userRepository;

    @Test
    void registrationHashesPasswordAndReturnsSafeResponse() throws Exception {
        String email = uniqueEmail();
        String request = """
                {"firstName":"Test","lastName":"User","email":"%s","password":"Password123!"}
                """.formatted(email);

        String response = mockMvc.perform(post("/api/auth/register").contentType(MediaType.APPLICATION_JSON).content(request))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.email").value(email))
                .andExpect(jsonPath("$.data.role").value("USER"))
                .andExpect(jsonPath("$.data.password").doesNotExist())
                .andReturn().getResponse().getContentAsString();
        org.junit.jupiter.api.Assertions.assertFalse(response.contains("Password123!"));

        User user = userRepository.findByEmail(email).orElseThrow();
        org.junit.jupiter.api.Assertions.assertTrue(user.getPassword().startsWith("$2"));
        org.junit.jupiter.api.Assertions.assertNotEquals("Password123!", user.getPassword());
    }

    @Test
    void duplicateEmailReturnsConflict() throws Exception {
        String email = uniqueEmail();
        String request = """
                {"firstName":"Test","lastName":"User","email":"%s","password":"Password123!"}
                """.formatted(email);

        mockMvc.perform(post("/api/auth/register").contentType(MediaType.APPLICATION_JSON).content(request))
                .andExpect(status().isCreated());
        mockMvc.perform(post("/api/auth/register").contentType(MediaType.APPLICATION_JSON).content(request))
                .andExpect(status().isConflict());
    }

    @Test
    void loginReturnsTokenAndMeRequiresThatToken() throws Exception {
        String email = uniqueEmail();
        String register = """
                {"firstName":"Test","lastName":"User","email":"%s","password":"Password123!"}
                """.formatted(email);
        mockMvc.perform(post("/api/auth/register").contentType(MediaType.APPLICATION_JSON).content(register))
                .andExpect(status().isCreated());

        String login = """
                {"email":"%s","password":"Password123!"}
                """.formatted(email);
        String response = mockMvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON).content(login))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.accessToken").isString())
                .andReturn().getResponse().getContentAsString();
        String token = new com.fasterxml.jackson.databind.ObjectMapper().readTree(response).path("data").path("accessToken").asText();

        mockMvc.perform(get("/api/auth/me").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.email").value(email));
        mockMvc.perform(get("/api/auth/me"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void regularUserCannotCreateCar() throws Exception {
        String email = uniqueEmail();
        String register = """
                {"firstName":"Test","lastName":"User","email":"%s","password":"Password123!"}
                """.formatted(email);
        mockMvc.perform(post("/api/auth/register").contentType(MediaType.APPLICATION_JSON).content(register))
                .andExpect(status().isCreated());
        String login = """
                {"email":"%s","password":"Password123!"}
                """.formatted(email);
        String response = mockMvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON).content(login))
                .andReturn().getResponse().getContentAsString();
        String token = new com.fasterxml.jackson.databind.ObjectMapper().readTree(response).path("data").path("accessToken").asText();

        mockMvc.perform(post("/api/cars").header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON).content("""
                                {"make":"Tesla","model":"Model 3","year":2024,"registrationNumber":"TEST-REG-999","licensePlate":"TEST-PLATE-999","vin":"TEST-VIN-99999","carType":"SEDAN","seats":5,"transmission":"AUTOMATIC","fuelType":"ELECTRIC","dailyRate":100,"mileage":0}
                                """))
                .andExpect(status().isForbidden());
    }

    private String uniqueEmail() { return "test-" + UUID.randomUUID() + "@example.com"; }
}