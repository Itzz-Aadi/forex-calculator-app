package com.example.forexcalculatorapp.data

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

class StockRepository {
    private val api = FinancialRetrofitInstance.api

    companion object {
        private const val TAG = "StockRepository"

        // Stock symbols with company names (fallback)
        private val STOCK_INFO = mapOf(
            "AAPL" to "Apple Inc.",
            "TSLA" to "Tesla Inc.",
            "NVDA" to "NVIDIA Corporation",
            "MSFT" to "Microsoft Corporation",
            "AMZN" to "Amazon.com Inc.",
            "META" to "Meta Platforms Inc.",
            "AMD" to "Advanced Micro Devices",
            "NFLX" to "Netflix Inc.",
            "F" to "Ford Motor",
            "GM" to "General Motors",
            "T" to "AT&T Inc.",
            "VZ" to "Verizon",
            "XOM" to "Exxon Mobil"
        )
    }

    // Fetch real stock data from Yahoo Finance
    private suspend fun fetchStockFromAPI(symbol: String): StockWithPrice? {
        return withContext(Dispatchers.IO) {
            try {
                val response = api.getQuote(symbol)
                val result = response.chart.result?.firstOrNull()

                if (result != null) {
                    val meta = result.meta
                    val price = meta.regularMarketPrice
                    val previousClose = meta.previousClose
                    val change = price - previousClose
                    val percentChange = if (previousClose > 0) {
                        ((change / previousClose) * 100)
                    } else 0.0

                    val name = meta.longName ?: meta.shortName ?: STOCK_INFO[symbol] ?: symbol

                    StockWithPrice(
                        symbol = symbol,
                        name = name,
                        price = price,
                        change = change,
                        percentChange = percentChange,
                        exchange = meta.exchangeName ?: "NASDAQ"
                    )
                } else {
                    Log.w(TAG, "No data returned for $symbol")
                    null
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error fetching $symbol from API: ${e.message}", e)
                null
            }
        }
    }

    // Get stock quote
    suspend fun getStockQuote(symbol: String): Result<StockInfo> {
        return try {
            val stock = fetchStockFromAPI(symbol)
            if (stock != null) {
                Result.success(
                    StockInfo(
                        symbol = stock.symbol,
                        name = stock.name,
                        price = stock.price,
                        currency = "USD",
                        change = stock.change,
                        percentChange = stock.percentChange
                    )
                )
            } else {
                Result.failure(Exception("Could not fetch stock data for $symbol"))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error getting quote for $symbol", e)
            Result.failure(e)
        }
    }

    // Search stocks
    suspend fun searchStocks(query: String): Result<List<StockSearchResult>> {
        return try {
            withContext(Dispatchers.IO) {
                val response = api.searchStocks(query, 10)
                val results = response.quotes
                    .filter { it.quoteType == "EQUITY" }
                    .map { quote ->
                        StockSearchResult(
                            symbol = quote.symbol,
                            name = quote.longName ?: quote.shortName ?: quote.symbol,
                            currency = "USD",
                            exchange = quote.exchange
                        )
                    }
                Result.success(results)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error searching stocks", e)
            Result.failure(e)
        }
    }

    // Fetch multiple stocks in parallel
    private suspend fun fetchMultipleStocks(symbols: List<String>): List<StockWithPrice> {
        return coroutineScope {
            symbols.mapIndexed { index, symbol ->
                async {
                    // Stagger requests slightly to be nice to the API
                    delay(index * 300L)
                    fetchStockFromAPI(symbol)
                }
            }.awaitAll().filterNotNull()
        }
    }

    // Get most active stocks
    suspend fun getMostActiveStocks(): Result<List<StockWithPrice>> {
        return try {
            val symbols = listOf("AAPL", "TSLA", "NVDA", "MSFT", "AMZN")
            val stocks = fetchMultipleStocks(symbols)

            if (stocks.isEmpty()) {
                Result.failure(Exception("Could not fetch stock data. Please check your internet connection."))
            } else {
                Result.success(stocks)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error getting most active stocks", e)
            Result.failure(e)
        }
    }

    // Get gainers
    suspend fun getGainers(): Result<List<StockWithPrice>> {
        return try {
            val symbols = listOf("NVDA", "AMD", "TSLA", "META", "NFLX")
            val stocks = fetchMultipleStocks(symbols)

            // Sort by percentage change (highest first)
            val sorted = stocks.sortedByDescending { it.percentChange ?: 0.0 }

            if (sorted.isEmpty()) {
                Result.failure(Exception("Could not fetch stock data. Please check your internet connection."))
            } else {
                Result.success(sorted)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error getting gainers", e)
            Result.failure(e)
        }
    }

    // Get losers
    suspend fun getLosers(): Result<List<StockWithPrice>> {
        return try {
            val symbols = listOf("F", "GM", "T", "VZ", "XOM")
            val stocks = fetchMultipleStocks(symbols)

            // Sort by percentage change (lowest first)
            val sorted = stocks.sortedBy { it.percentChange ?: 0.0 }

            if (sorted.isEmpty()) {
                Result.failure(Exception("Could not fetch stock data. Please check your internet connection."))
            } else {
                Result.success(sorted)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error getting losers", e)
            Result.failure(e)
        }
    }

    // Get all stocks
    suspend fun getAllStocksWithPrices(): Result<List<StockWithPrice>> {
        return getMostActiveStocks()
    }
}

// Data classes
data class StockInfo(
    val symbol: String,
    val name: String,
    val price: Double,
    val currency: String,
    val change: Double?,
    val percentChange: Double?
)

data class StockWithPrice(
    val symbol: String,
    val name: String,
    val price: Double,
    val change: Double?,
    val percentChange: Double?,
    val exchange: String
)

data class StockSearchResult(
    val symbol: String,
    val name: String,
    val currency: String,
    val exchange: String
)
