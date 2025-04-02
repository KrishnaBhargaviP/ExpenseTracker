package com.example.expensetracker.network

import com.example.expensetracker.models.CurrencyRatesResponse
import retrofit2.http.GET
import retrofit2.http.Path


/**
 * API documentation: https://github.com/fawazahmed0/exchange-api
 */
interface CurrencyApiService {
    @GET("currencies.json")
    suspend fun getCurrencyCodes(): Map<String, String>

    @GET("{base}.json")
    suspend fun getRates(@Path("base") base: String): CurrencyRatesResponse
}

