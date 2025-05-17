package com.capgemini.reward_program.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Unit tests for RewardsService class.
 */
public class RewardsServiceTest {

    private RewardsService rewardsService;

    @BeforeEach
    void setUp() {
        rewardsService = new RewardsService();  // Initialize the service
    }

    /**
     * Test reward calculation for multiple customers with different transactions.
     */
    @Test
    void testCalculateAllRewards() {
        Map<String, Object> rewards = rewardsService.calculateAllRewards();

        // Test for Ashok
        Map<String, Integer> aliceMonthlyPoints = (Map<String, Integer>) ((Map<String, Object>) rewards.get("Ashok")).get("monthlyPoints");
        assertEquals(290, aliceMonthlyPoints.get("2025-03"));
        assertEquals(25, aliceMonthlyPoints.get("2025-04"));
        assertEquals(250, aliceMonthlyPoints.get("2025-05"));

        int aliceTotalPoints = (int) ((Map<String, Object>) rewards.get("Ashok")).get("totalPoints");
        assertEquals(655, aliceTotalPoints);

        // Test for Kumar
        Map<String, Integer> bobMonthlyPoints = (Map<String, Integer>) ((Map<String, Object>) rewards.get("Kumar")).get("monthlyPoints");
        assertEquals(10, bobMonthlyPoints.get("2025-03"));
        assertEquals(110, bobMonthlyPoints.get("2025-04"));

        int bobTotalPoints = (int) ((Map<String, Object>) rewards.get("Kumar")).get("totalPoints");
        assertEquals(350, bobTotalPoints);
    }

    /**
     * Test the reward calculation with no customer data.
     */
    @Test
    void testCalculateAllRewardsEmptyData() {
        // Simulating empty customer list in RewardsService
        rewardsService = new RewardsService(List.of());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            rewardsService.calculateAllRewards();
        });
        assertEquals("No customer data available.", exception.getMessage());
    }

}
