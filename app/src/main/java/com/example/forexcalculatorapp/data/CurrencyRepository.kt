package com.example.forexcalculatorapp.data

class CurrencyRepository {
    private val api = RetrofitInstance.api

    // Common currency names mapping
    private val currencyNames = mapOf(
        "USD" to "United States Dollar",
        "EUR" to "Euro",
        "GBP" to "British Pound Sterling",
        "JPY" to "Japanese Yen",
        "AUD" to "Australian Dollar",
        "CAD" to "Canadian Dollar",
        "CHF" to "Swiss Franc",
        "CNY" to "Chinese Yuan",
        "INR" to "Indian Rupee",
        "MXN" to "Mexican Peso",
        "BRL" to "Brazilian Real",
        "ZAR" to "South African Rand",
        "RUB" to "Russian Ruble",
        "KRW" to "South Korean Won",
        "SGD" to "Singapore Dollar",
        "HKD" to "Hong Kong Dollar",
        "NOK" to "Norwegian Krone",
        "SEK" to "Swedish Krona",
        "DKK" to "Danish Krone",
        "NZD" to "New Zealand Dollar",
        "TRY" to "Turkish Lira",
        "PLN" to "Polish Zloty",
        "THB" to "Thai Baht",
        "IDR" to "Indonesian Rupiah",
        "MYR" to "Malaysian Ringgit",
        "PHP" to "Philippine Peso",
        "CZK" to "Czech Koruna",
        "HUF" to "Hungarian Forint",
        "ILS" to "Israeli New Shekel",
        "CLP" to "Chilean Peso",
        "PKR" to "Pakistani Rupee",
        "AED" to "United Arab Emirates Dirham",
        "SAR" to "Saudi Riyal",
        "EGP" to "Egyptian Pound"
    )

    suspend fun getCurrencies(): Result<Map<String, String>> {
        return try {
            val response = api.getCurrencies()
            // Get available currencies from the rates
            val availableCurrencies = response.rates.keys
                .associateWith { code -> currencyNames[code] ?: code }
                .toMutableMap()

            // Add the base currency (USD) as well
            availableCurrencies[response.base] = currencyNames[response.base] ?: response.base

            Result.success(availableCurrencies)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun convertCurrency(
        amount: Double,
        from: String,
        to: String
    ): Result<ConversionResult> {
        return try {
            val response = api.getExchangeRates(from)
            val rate = response.rates[to] ?: 0.0
            val convertedAmount = amount * rate
            val inverseRate = if (rate > 0) 1.0 / rate else 0.0

            Result.success(
                ConversionResult(
                    convertedAmount = convertedAmount,
                    rate = rate,
                    inverseRate = inverseRate,
                    fromCurrency = from,
                    toCurrency = to
                )
            )
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

data class ConversionResult(
    val convertedAmount: Double,
    val rate: Double,
    val inverseRate: Double,
    val fromCurrency: String,
    val toCurrency: String
)
