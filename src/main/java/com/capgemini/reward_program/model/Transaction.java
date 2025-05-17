package com.capgemini.reward_program.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * Represents a transaction made by a customer.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Transaction {

    private LocalDate date;
    private double amount;
}
