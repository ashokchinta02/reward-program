package com.capgemini.reward_program.controller;

import com.capgemini.reward_program.service.RewardsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * REST controller to expose customer rewards data.
 */
@RestController
@RequestMapping("/rewards")
public class RewardsController {

    private final RewardsService rewardsService;

    public RewardsController(RewardsService rewardsService) {
        this.rewardsService = rewardsService;
    }

    /**
     * Retrieves reward points earned by a specific customer for a given month.
     *
     * This endpoint returns the total reward points accumulated by the customer
     * for transactions made during the specified month. The month should be provided
     * in the format yyyy-MM (e.g., "2025-04")
     *
     * @param customerId the unique identifier of the customer
     * @param month the target month in yyyy-MM format
     * @return a {@link ResponseEntity} containing customer ID, name, the month, and reward points
     * @throws IllegalArgumentException if the customer does not exist or the month format is invalid
     *
     * Example: GET /rewards/1/transactions/2025-04
     *
     * Response:
     * {
     *   "customerId": 1,
     *   "customerName": "Ashok",
     *   "month": "2025-04",
     *   "rewardPoints": 25
     * }
     */
    @GetMapping("/{customerId}/transactions/{month}")
    public ResponseEntity<Map<String, Object>> getMonthlyRewardForCustomer(
            @PathVariable Long customerId,
            @PathVariable String month) {
        Map<String, Object> response = rewardsService.calculateMonthlyRewardForCustomer(customerId, month);
        return ResponseEntity.ok(response);
    }

    /**
     * Retrieves the total and monthly reward points for all customers.
     *
     * This endpoint aggregates reward points for each customer based on their
     * transaction history over multiple months. It returns both monthly breakdowns and
     * overall totals.
     *
     * @return a {@link ResponseEntity} containing a map of customer names to their reward details.
     *         Each entry includes:
     *           customerId: Id of the Customer
     *           monthlyPoints: a map of month (yyyy-MM) to reward points
     *           totalPoints: total points earned across all months
     *
     * @throws IllegalArgumentException if there is no customer reward data available
     *
     * Example: GET /rewards/allCustomers
     *
     * Response:
     * {
     *   "Ashok": {
     *     "customerId": 1,
     *     "monthlyPoints": {
     *       "2025-03": 90,
     *       "2025-04": 25
     *     },
     *     "totalPoints": 115
     *   },
     *   "Chinta": {
     *     "customerId": 3,
     *     "monthlyPoints": {
     *       "2025-03": 50
     *     },
     *     "totalPoints": 50
     *   }
     * }
     */
    @GetMapping("/allCustomers")
    public ResponseEntity<Map<String, Object>> getAllRewards() {
        Map<String, Object> rewards = rewardsService.calculateAllRewards();

        if (rewards.isEmpty()) {
            throw new IllegalArgumentException("No reward data found.");
        }

        return ResponseEntity.ok(rewards);
    }


}
