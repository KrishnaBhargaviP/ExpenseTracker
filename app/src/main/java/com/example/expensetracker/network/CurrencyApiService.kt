package com.example.expensetracker.network

import retrofit2.http.GET
import retrofit2.http.Path

/**
 * API documentation: https://github.com/fawazahmed0/exchange-api
 */
interface CurrencyApiService {
    @GET("latest/currencies.json")
    suspend fun getCurrencyCodes(): Map<String, String>

    @GET("latest/currencies/{base}.json")
    suspend fun getRates(@Path("base") base: String): Map<String, Map<String, Double>>
}