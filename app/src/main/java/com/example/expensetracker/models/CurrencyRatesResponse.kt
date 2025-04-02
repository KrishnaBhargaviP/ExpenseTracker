package com.example.expensetracker.models

data class CurrencyRatesResponse(
    val date: String,
    val cad: Map<String, Double>
)
