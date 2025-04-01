package com.example.expensetracker

import android.icu.util.Currency

data class Expense(
    val id: Number,
    val expenseName: String,
    val expenseAmount: Double,
    val expenseDate: String,
    val currency: Currency,
    val convertedCost: Double
)
