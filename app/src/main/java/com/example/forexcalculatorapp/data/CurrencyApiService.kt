package com.example.forexcalculatorapp.data

import retrofit2.http.GET
import retrofit2.http.Path

interface CurrencyApiService {

    @GET("latest/USD")
    suspend fun getCurrencies(): ExchangeRateResponse

    @GET("latest/{base}")
    suspend fun getExchangeRates(
        @Path("base") baseCurrency: String
    ): ExchangeRateResponse
}

data class ExchangeRateResponse(
    val base: String,
    val date: String,
    val rates: Map<String, Double>
)
