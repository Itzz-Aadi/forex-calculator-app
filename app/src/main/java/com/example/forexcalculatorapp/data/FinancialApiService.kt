package com.example.forexcalculatorapp.data

import com.google.gson.annotations.SerializedName
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface FinancialApiService {

    // Get stock quote (Yahoo Finance)
    @GET("v8/finance/chart/{symbol}")
    suspend fun getQuote(
        @Path("symbol") symbol: String,
        @Query("interval") interval: String = "1d",
        @Query("range") range: String = "1d"
    ): YahooFinanceResponse

    // Search stocks (Yahoo Finance)
    @GET("v1/finance/search")
    suspend fun searchStocks(
        @Query("q") query: String,
        @Query("quotesCount") count: Int = 10
    ): YahooSearchResponse
}

// Yahoo Finance Response Structure
data class YahooFinanceResponse(
    @SerializedName("chart") val chart: YahooChart
)

data class YahooChart(
    @SerializedName("result") val result: List<YahooResult>?,
    @SerializedName("error") val error: YahooError?
)

data class YahooError(
    @SerializedName("code") val code: String?,
    @SerializedName("description") val description: String?
)

data class YahooResult(
    @SerializedName("meta") val meta: YahooMeta,
    @SerializedName("timestamp") val timestamp: List<Long>?,
    @SerializedName("indicators") val indicators: YahooIndicators?
)

data class YahooMeta(
    @SerializedName("currency") val currency: String = "USD",
    @SerializedName("symbol") val symbol: String = "",
    @SerializedName("exchangeName") val exchangeName: String? = null,
    @SerializedName("regularMarketPrice") val regularMarketPrice: Double = 0.0,
    @SerializedName("previousClose") val previousClose: Double = 0.0,
    @SerializedName("regularMarketTime") val regularMarketTime: Long = 0,
    @SerializedName("longName") val longName: String? = null,
    @SerializedName("shortName") val shortName: String? = null
)

data class YahooIndicators(
    @SerializedName("quote") val quote: List<YahooQuoteData>?
)

data class YahooQuoteData(
    @SerializedName("open") val open: List<Double?>?,
    @SerializedName("high") val high: List<Double?>?,
    @SerializedName("low") val low: List<Double?>?,
    @SerializedName("close") val close: List<Double?>?,
    @SerializedName("volume") val volume: List<Long?>?
)

// Yahoo Search Response
data class YahooSearchResponse(
    @SerializedName("quotes") val quotes: List<YahooSearchQuote> = emptyList()
)

data class YahooSearchQuote(
    @SerializedName("symbol") val symbol: String = "",
    @SerializedName("shortname") val shortName: String? = null,
    @SerializedName("longname") val longName: String? = null,
    @SerializedName("quoteType") val quoteType: String = "",
    @SerializedName("exchange") val exchange: String = ""
)
