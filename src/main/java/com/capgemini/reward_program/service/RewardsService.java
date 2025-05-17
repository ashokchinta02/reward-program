package com.capgemini.reward_program.service;


import com.capgemini.reward_program.model.Customer;
import com.capgemini.reward_program.model.Transaction;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Service class responsible for calculating reward points.
 */
@Service
public class RewardsService {
    private final List<Customer> customers;

    // Default constructor with mock data
    public RewardsService() {
        this.customers = loadMockCustomers();
    }

    // Constructor for tests or future flexibility
    public RewardsService(List<Customer> customers) {
        this.customers = customers;
    }

    /**
     * Calculates the total and monthly reward points for each customer based on their transaction history.
     *
     * <p>This method iterates over all mock customers and their associated transactions.
     * For each transaction, it computes the reward points using {@link #calculatePoints(double)} and
     * aggregates them by month (in {@code yyyy-MM} format) as well as in total for the customer.</p>
     *
     * <p>The result is a mapping of customer names to their reward data:</p>
     * <ul>
     *   <li><b>monthlyPoints</b>: a map of month strings (e.g., "2025-04") to points earned</li>
     *   <li><b>totalPoints</b>: total reward points across all months</li>
     * </ul>
     *
     * @return a map where each key is the customer's name and the value is another map containing:
     * - {@code "monthlyPoints"}: a map of months to points
     * - {@code "totalPoints"}: the total points earned by the customer
     * @throws IllegalArgumentException if no customers exist or all have empty transaction data
     */
    public Map<String, Object> calculateAllRewards() {
        if (customers == null || customers.isEmpty()) {
            throw new IllegalArgumentException("No customer data available.");
        }

        Map<String, Object> result = new HashMap<>();
        DateTimeFormatter monthFormatter = DateTimeFormatter.ofPattern("yyyy-MM");

        for (Customer customer : customers) {
            Map<String, Integer> monthlyPoints = new HashMap<>();
            int totalPoints = 0;

            for (Transaction tx : customer.getTransactions()) {
                int points = calculatePoints(tx.getAmount());
                String month = tx.getDate().format(monthFormatter);

                monthlyPoints.put(month, monthlyPoints.getOrDefault(month, 0) + points);
                totalPoints += points;
            }

            Map<String, Object> customerPoints = new HashMap<>();
            customerPoints.put("customerId", customer.getId());
            customerPoints.put("monthlyPoints", monthlyPoints);
            customerPoints.put("totalPoints", totalPoints);
            result.put(customer.getName(), customerPoints);
        }

        return result;
    }

    /** Calculates reward points earned for a single transaction amount based on the following rules:
     *
     * No points for purchases ≤ $50
     * 1 point for every dollar spent over $50 up to $100
     * 2 points for every dollar spent over $100
     */
    private int calculatePoints(double amount) {
        if (amount <= 50) return 0;
        if (amount <= 100) return (int) (amount - 50);
        return 50 + (int) ((amount - 100) * 2);
    }


    public Map<String, Object> calculateMonthlyRewardForCustomer(Long customerId, String month) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM");

        // Validate month format
        YearMonth yearMonth;
        try {
            yearMonth = YearMonth.parse(month, formatter);
        } catch (DateTimeParseException ex) {
            throw new IllegalArgumentException("Invalid month format. Use yyyy-MM.");
        }

        // Find customer
        Optional<Customer> customerOpt = customers.stream()
                .filter(c -> c.getId().equals(customerId))
                .findFirst();

        if (customerOpt.isEmpty()) {
            throw new IllegalArgumentException("Customer not found with ID: " + customerId);
        }

        Customer customer = customerOpt.get();

        // Filter transactions for that month
        int totalPoints = customer.getTransactions().stream()
                .filter(tx -> YearMonth.from(tx.getDate()).equals(yearMonth))
                .mapToInt(tx -> calculatePoints(tx.getAmount()))
                .sum();

        Map<String, Object> result = new HashMap<>();
        result.put("customerId", customer.getId());
        result.put("customerName", customer.getName());
        result.put("month", yearMonth.toString());
        result.put("rewardPoints", totalPoints);

        return result;
    }


    /**
     * Initializes a list of mock customers with sample transactions.
     *
     * <p>This method is typically used to simulate customer and transaction data in the
     * absence of a database. It creates a predefined list of customers, each with
     * multiple transactions spread across different dates. The transactions include varying
     * amounts to demonstrate reward calculation logic for:
     * <p>
     * Amounts below $50 (no rewards)
     * Amounts between $50 and $100 (1 point per dollar)
     * Amounts above $100 (2 points per dollar over $100, plus 1 point per dollar between $50 and $100)
     * <p>
     * Note: Dates are generated dynamically using {@link java.time.LocalDate#now()}
     * and adjusted using {@link java.time.LocalDate#minusMonths(long)} to ensure no hardcoded months.
     *
     * @return a list of mock {@link com.capgemini.reward_program.model.Customer} objects with associated transactions
     */
    private List<Customer> loadMockCustomers() {
        return List.of(
                new Customer(1L, "Ashok", List.of(
                        new Transaction(LocalDate.of(2024, 3, 1), 120),
                        new Transaction(LocalDate.of(2025, 3, 10), 220),
                        new Transaction(LocalDate.of(2025, 4, 5), 75),
                        new Transaction(LocalDate.of(2025, 5, 15), 200)
                )),
                new Customer(2L, "Kumar", List.of(
                        new Transaction(LocalDate.of(2025, 3, 10), 60),
                        new Transaction(LocalDate.of(2025, 4, 20), 110),
                        new Transaction(LocalDate.of(2025, 4, 21), 90),
                        new Transaction(LocalDate.of(2025, 6, 29), 190)
                )),
                new Customer(3L, "Ram", List.of(
                        new Transaction(LocalDate.of(2024, 4, 10), 60),
                        new Transaction(LocalDate.of(2024, 5, 20), 110),
                        new Transaction(LocalDate.of(2025, 6, 21), 90),
                        new Transaction(LocalDate.of(2025, 9, 29), 190)
                )),
                new Customer(4L, "Leela", List.of(
                        new Transaction(LocalDate.of(2024, 2, 11), 60),
                        new Transaction(LocalDate.of(2025, 5, 21), 110),
                        new Transaction(LocalDate.of(2025, 8, 26), 90),
                        new Transaction(LocalDate.of(2025, 9, 28), 190)
                )),
                new Customer(5L, "Chinta", List.of(
                        new Transaction(LocalDate.of(2025, 5, 10), 45),
                        new Transaction(LocalDate.of(2025, 5, 15), 50),
                        new Transaction(LocalDate.of(2025, 5, 20), 51)
                ))
        );
    }


}
