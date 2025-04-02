package com.example.expensetracker.models


data class Expense(
    val id: Number,
    val expenseName: String,
    val expenseAmount: Double,
    val expenseDate: String,
    val currency: String,
    val convertedCost: Double
)
