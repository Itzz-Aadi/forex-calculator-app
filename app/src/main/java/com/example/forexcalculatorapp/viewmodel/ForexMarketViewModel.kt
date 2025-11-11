package com.example.forexcalculatorapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.forexcalculatorapp.data.CurrencyRepository
import com.example.forexcalculatorapp.ui.components.ExchangeRatePoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ForexPair(
    val fromCurrency: String,
    val toCurrency: String,
    val currentRate: Double,
    val rateChange: Double?,
    val rateHistory: List<ExchangeRatePoint>
)

data class ForexMarketUiState(
    val forexPairs: List<ForexPair> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val lastUpdateTime: Long = 0,
    val isAutoRefreshEnabled: Boolean = true
)

class ForexMarketViewModel : ViewModel() {
    private val repository = CurrencyRepository()

    private val _uiState = MutableStateFlow(ForexMarketUiState())
    val uiState: StateFlow<ForexMarketUiState> = _uiState.asStateFlow()

    private var autoRefreshJob: Job? = null
    private val maxHistoryPoints = 30
    private var isLoadingInProgress = false

    // Popular forex pairs to track
    private val popularPairs = listOf(
        "USD" to "EUR",  // US Dollar to Euro
        "USD" to "GBP",  // US Dollar to British Pound
        "USD" to "JPY",  // US Dollar to Japanese Yen
        "USD" to "CHF",  // US Dollar to Swiss Franc
        "EUR" to "GBP",  // Euro to British Pound
        "EUR" to "JPY",  // Euro to Japanese Yen
        "GBP" to "USD",  // British Pound to US Dollar
        "AUD" to "USD",  // Australian Dollar to US Dollar
        "USD" to "CAD",  // US Dollar to Canadian Dollar
        "NZD" to "USD"   // New Zealand Dollar to US Dollar
    )

    init {
        // Don't start auto-refresh immediately - wait for first load
    }

    fun loadForexPairs() {
        // Prevent concurrent loads
        if (isLoadingInProgress) {
            return
        }

        isLoadingInProgress = true
        viewModelScope.launch {
            // Only show loading spinner on first load, not on auto-refresh
            val isFirstLoad = _uiState.value.forexPairs.isEmpty()
            _uiState.value = _uiState.value.copy(
                isLoading = isFirstLoad,
                errorMessage = null
            )

            try {
                val updatedPairs = mutableListOf<ForexPair>()
                val currentPairs = _uiState.value.forexPairs

                popularPairs.forEach { (from, to) ->
                    try {
                        val result = repository.convertCurrency(1.0, from, to)

                        result.onSuccess { conversionResult ->
                            // Find existing pair history
                            val existingPair = currentPairs.find {
                                it.fromCurrency == from && it.toCurrency == to
                            }

                            val currentHistory = existingPair?.rateHistory ?: emptyList()
                            val newPoint = ExchangeRatePoint(
                                timestamp = System.currentTimeMillis(),
                                rate = conversionResult.rate
                            )
                            val updatedHistory =
                                (currentHistory + newPoint).takeLast(maxHistoryPoints)

                            // Calculate rate change
                            val rateChange = if (currentHistory.isNotEmpty()) {
                                conversionResult.rate - currentHistory.last().rate
                            } else null

                            updatedPairs.add(
                                ForexPair(
                                    fromCurrency = from,
                                    toCurrency = to,
                                    currentRate = conversionResult.rate,
                                    rateChange = rateChange,
                                    rateHistory = updatedHistory
                                )
                            )
                        }.onFailure { error ->
                            // Log but continue with other pairs
                            println("Failed to load $from/$to: ${error.message}")
                        }
                    } catch (e: Exception) {
                        // Continue with other pairs even if one fails
                        println("Error loading $from/$to: ${e.message}")
                    }
                }

                _uiState.value = _uiState.value.copy(
                    forexPairs = updatedPairs,
                    isLoading = false,
                    errorMessage = if (updatedPairs.isEmpty()) "Unable to load any forex pairs. Check your internet connection." else null,
                    lastUpdateTime = System.currentTimeMillis()
                )

                // Start auto-refresh after first successful load
                if (updatedPairs.isNotEmpty() && autoRefreshJob == null) {
                    startAutoRefresh()
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "Failed to load forex data: ${e.message}"
                )
            } finally {
                isLoadingInProgress = false
            }
        }
    }

    fun refreshRates() {
        loadForexPairs()
    }

    fun toggleAutoRefresh() {
        val newState = !_uiState.value.isAutoRefreshEnabled
        _uiState.value = _uiState.value.copy(isAutoRefreshEnabled = newState)

        if (newState) {
            startAutoRefresh()
        } else {
            stopAutoRefresh()
        }
    }

    private fun startAutoRefresh() {
        autoRefreshJob?.cancel()
        autoRefreshJob = viewModelScope.launch {
            while (_uiState.value.isAutoRefreshEnabled) {
                delay(3_000) // Refresh every 3 seconds for real-time forex updates
                if (!isLoadingInProgress) {
                    loadForexPairs()
                }
            }
        }
    }

    private fun stopAutoRefresh() {
        autoRefreshJob?.cancel()
        autoRefreshJob = null
    }

    override fun onCleared() {
        super.onCleared()
        stopAutoRefresh()
    }
}
