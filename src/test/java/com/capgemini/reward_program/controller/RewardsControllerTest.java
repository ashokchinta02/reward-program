package com.capgemini.reward_program.controller;

import com.capgemini.reward_program.service.RewardsService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


/**
 * Integration test for RewardsController class.
 */
@SpringBootTest
@AutoConfigureMockMvc
public class RewardsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RewardsService rewardsService;


    /**
     * Test the RewardsController's GET /rewards endpoint.
     */
    @Test
    void testGetAllRewardsSuccess() throws Exception {
        // Simulate valid reward data
        when(rewardsService.calculateAllRewards()).thenReturn(Map.of(
                "Ashok", Map.of("totalPoints", 90, "monthlyPoints", Map.of("2025-03", 90))
        ));

        mockMvc.perform(get("/rewards/allCustomers"))
                .andExpect(status().isOk());
    }


    /**
     * Test the scenario when there is no customer data (throws 400 Bad Request).
     */
    @Test
    void testGetAllRewardsEmptyData() throws Exception {
        // Simulate empty result from service
        when(rewardsService.calculateAllRewards()).thenReturn(Map.of());

        mockMvc.perform(get("/rewards/allCustomers"))
                .andExpect(status().isBadRequest());
    }


    @Test
    void testGetMonthlyRewardForCustomer() throws Exception {
        Long customerId = 1L;
        String month = "2025-04";

        Map<String, Object> mockResult = Map.of(
                "customerId", 1L,
                "customerName", "Ashok",
                "month", "2025-04",
                "rewardPoints", 25
        );

        when(rewardsService.calculateMonthlyRewardForCustomer(customerId, month)).thenReturn(mockResult);

        mockMvc.perform(get("/rewards/1/transactions/2025-04"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.customerName").value("Ashok"))
                .andExpect(jsonPath("$.rewardPoints").value(25));
    }

}
