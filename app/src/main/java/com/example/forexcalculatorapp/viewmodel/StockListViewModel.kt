package com.example.forexcalculatorapp.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.forexcalculatorapp.data.StockRepository
import com.example.forexcalculatorapp.data.StockWithPrice
import com.example.forexcalculatorapp.ui.components.StockPricePoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.Job

data class StockListUiState(
    val stocks: List<StockWithPrice> = emptyList(),
    val selectedCategory: Int = 0, // 0: Most Active, 1: Gainers, 2: Losers
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val lastUpdateTime: Long = 0,
    val isAutoRefreshEnabled: Boolean = true,
    val priceHistoryMap: Map<String, List<StockPricePoint>> = emptyMap(), // Track price over time
    val showCharts: Boolean = true
)

class StockListViewModel : ViewModel() {
    private val repository = StockRepository()

    private val _uiState = MutableStateFlow(StockListUiState())
    val uiState: StateFlow<StockListUiState> = _uiState.asStateFlow()

    private var autoRefreshJob: Job? = null
    private val maxHistoryPoints = 20 // Keep last 20 price points per stock
    private var hasPerformedFirstLoad = false
    private var isLoadingInProgress = false

    companion object {
        private const val TAG = "StockListViewModel"
        private const val AUTO_REFRESH_INTERVAL_MS = 60_000L // 1 minute (60 seconds)
    }

    init {
        // Don't start auto-refresh immediately - wait for first load
        Log.d(TAG, "ViewModel initialized")
    }

    fun loadStocks() {
        // Prevent concurrent loads
        if (isLoadingInProgress) {
            Log.d(TAG, "Load already in progress, skipping")
            return
        }

        isLoadingInProgress = true
        Log.d(TAG, "Loading stocks for category: ${_uiState.value.selectedCategory}")

        viewModelScope.launch {
            // Only show loading indicator on first load, not on auto-refresh
            val isFirstLoad = _uiState.value.stocks.isEmpty()
            _uiState.value = _uiState.value.copy(
                isLoading = isFirstLoad,
                errorMessage = null
            )

            val result = when (_uiState.value.selectedCategory) {
                1 -> {
                    Log.d(TAG, "Fetching gainers...")
                    repository.getGainers()
                }

                2 -> {
                    Log.d(TAG, "Fetching losers...")
                    repository.getLosers()
                }

                else -> {
                    Log.d(TAG, "Fetching most active...")
                    repository.getMostActiveStocks()
                }
            }

            result.onSuccess { stocks ->
                Log.d(TAG, "Successfully loaded ${stocks.size} stocks")
                // Update price history for each stock
                val updatedHistory = _uiState.value.priceHistoryMap.toMutableMap()
                stocks.forEach { stock ->
                    val currentHistory = updatedHistory[stock.symbol] ?: emptyList()
                    val newPoint = StockPricePoint(
                        timestamp = System.currentTimeMillis(),
                        price = stock.price
                    )
                    // Keep only last maxHistoryPoints
                    val updated = (currentHistory + newPoint).takeLast(maxHistoryPoints)
                    updatedHistory[stock.symbol] = updated
                }

                _uiState.value = _uiState.value.copy(
                    stocks = stocks,
                    isLoading = false,
                    errorMessage = null,
                    lastUpdateTime = System.currentTimeMillis(),
                    priceHistoryMap = updatedHistory
                )

                // Start auto-refresh after first successful load
                if (!hasPerformedFirstLoad) {
                    hasPerformedFirstLoad = true
                    Log.d(TAG, "First load successful, starting auto-refresh (1 minute interval)")
                    if (_uiState.value.isAutoRefreshEnabled) {
                        startAutoRefresh()
                    }
                }
            }.onFailure { error ->
                Log.e(TAG, "Failed to load stocks: ${error.message}", error)
                _uiState.value = _uiState.value.copy(
                    stocks = emptyList(),
                    isLoading = false,
                    errorMessage = "Failed to load stocks: ${error.message ?: "Unknown error"}",
                    lastUpdateTime = System.currentTimeMillis()
                )
            }

            isLoadingInProgress = false
        }
    }

    fun selectCategory(category: Int) {
        if (_uiState.value.selectedCategory != category) {
            Log.d(TAG, "Category changed to: $category")
            _uiState.value = _uiState.value.copy(selectedCategory = category)
            loadStocks()
        }
    }

    fun toggleAutoRefresh() {
        val newState = !_uiState.value.isAutoRefreshEnabled
        Log.d(TAG, "Auto-refresh toggled to: $newState")
        _uiState.value = _uiState.value.copy(isAutoRefreshEnabled = newState)

        if (newState) {
            startAutoRefresh()
        } else {
            stopAutoRefresh()
        }
    }

    fun toggleCharts() {
        val newState = !_uiState.value.showCharts
        Log.d(TAG, "Charts toggled to: $newState")
        _uiState.value = _uiState.value.copy(showCharts = newState)
    }

    fun getPriceHistory(symbol: String): List<StockPricePoint> {
        return _uiState.value.priceHistoryMap[symbol] ?: emptyList()
    }

    private fun startAutoRefresh() {
        // Only start if we've had at least one successful load
        if (!hasPerformedFirstLoad) {
            Log.d(TAG, "Skipping auto-refresh start - no successful load yet")
            return
        }

        autoRefreshJob?.cancel()
        Log.d(TAG, "Starting auto-refresh (1 minute interval)")
        autoRefreshJob = viewModelScope.launch {
            while (_uiState.value.isAutoRefreshEnabled) {
                delay(AUTO_REFRESH_INTERVAL_MS) // Refresh every 1 minute
                if (!isLoadingInProgress) {
                    Log.d(TAG, "Auto-refresh triggered (1 minute elapsed)")
                    loadStocks()
                }
            }
        }
    }

    private fun stopAutoRefresh() {
        Log.d(TAG, "Stopping auto-refresh")
        autoRefreshJob?.cancel()
        autoRefreshJob = null
    }

    override fun onCleared() {
        super.onCleared()
        Log.d(TAG, "ViewModel cleared")
        stopAutoRefresh()
    }
}
