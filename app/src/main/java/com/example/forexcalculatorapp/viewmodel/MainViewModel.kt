package com.example.forexcalculatorapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.forexcalculatorapp.data.CurrencyRepository
import com.example.forexcalculatorapp.data.ConversionResult
import com.example.forexcalculatorapp.data.StockRepository
import com.example.forexcalculatorapp.data.StockSearchResult
import com.example.forexcalculatorapp.data.StockInfo
import com.example.forexcalculatorapp.data.StockWithPrice
import com.example.forexcalculatorapp.ui.components.ExchangeRatePoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Locale

data class CurrencyItem(
    val code: String,
    val name: String
)

data class MainUiState(
    val stockSymbol: String = "",
    val stockName: String = "",
    val amount: String = "",
    val numberOfShares: String = "",
    val fromCurrency: CurrencyItem = CurrencyItem("USD", "United States Dollar"),
    val toCurrency: CurrencyItem = CurrencyItem("EUR", "Euro"),
    val convertedAmount: String = "",
    val totalCostInFrom: String = "",
    val totalCostInTo: String = "",
    val inverseRateText: String = "",
    val isLoading: Boolean = false,
    val isLoadingStock: Boolean = false,
    val errorMessage: String? = null,
    val stockErrorMessage: String? = null,
    val availableCurrencies: List<CurrencyItem> = emptyList(),
    val stockSearchResults: List<StockSearchResult> = emptyList(),
    val showCurrencyPicker: Boolean = false,
    val showStockSearch: Boolean = false,
    val isPickingFromCurrency: Boolean = true,
    val rateHistory: List<ExchangeRatePoint> = emptyList(),
    val currentRate: Double = 0.0,
    val rateChange: Double? = null,
    val isAutoRefreshEnabled: Boolean = true,
    val showChart: Boolean = true,
    val lastUpdateTime: Long = 0
)

class MainViewModel : ViewModel() {
    private val repository = CurrencyRepository()
    private val stockRepository = StockRepository()

    private val _uiState = MutableStateFlow(MainUiState())
    val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()

    private var conversionJob: Job? = null
    private var stockSearchJob: Job? = null
    private var autoRefreshJob: Job? = null
    private val maxHistoryPoints = 30 // Keep last 30 rate points
    private var hasPerformedFirstConversion = false

    init {
        loadCurrencies()
        // Don't start auto-refresh immediately - wait for first conversion
    }

    private fun loadCurrencies() {
        viewModelScope.launch {
            val result = repository.getCurrencies()
            result.onSuccess { currencyMap ->
                val currencies = currencyMap.map { (code, name) ->
                    CurrencyItem(code, name)
                }.sortedBy { it.code }
                _uiState.value = _uiState.value.copy(availableCurrencies = currencies)
            }.onFailure {
                _uiState.value = _uiState.value.copy(
                    errorMessage = "Failed to load currencies"
                )
            }
        }
    }

    fun onStockSymbolChanged(symbol: String) {
        // Convert to uppercase for stock symbols
        val upperSymbol = symbol.uppercase()
        _uiState.value = _uiState.value.copy(stockSymbol = upperSymbol)

        // Debounce stock search
        stockSearchJob?.cancel()
        if (upperSymbol.length >= 1) {
            stockSearchJob = viewModelScope.launch {
                delay(500)
                searchStocks(upperSymbol)
            }
        } else {
            _uiState.value = _uiState.value.copy(
                stockSearchResults = emptyList(),
                showStockSearch = false
            )
        }
    }

    private fun searchStocks(query: String) {
        viewModelScope.launch {
            val result = stockRepository.searchStocks(query)
            result.onSuccess { stocks ->
                _uiState.value = _uiState.value.copy(
                    stockSearchResults = stocks,
                    showStockSearch = stocks.isNotEmpty()
                )
            }.onFailure {
                _uiState.value = _uiState.value.copy(
                    stockSearchResults = emptyList(),
                    showStockSearch = false
                )
            }
        }
    }

    fun onStockSelected(stock: StockSearchResult) {
        _uiState.value = _uiState.value.copy(
            stockSymbol = stock.symbol,
            showStockSearch = false,
            isLoadingStock = true,
            stockErrorMessage = null
        )

        // Fetch the stock quote
        viewModelScope.launch {
            val result = stockRepository.getStockQuote(stock.symbol)
            result.onSuccess { stockInfo ->
                // Find the currency in available currencies
                val currency = _uiState.value.availableCurrencies.find {
                    it.code == stockInfo.currency
                } ?: _uiState.value.fromCurrency

                _uiState.value = _uiState.value.copy(
                    stockName = stockInfo.name,
                    amount = stockInfo.price.toString(),
                    fromCurrency = currency,
                    isLoadingStock = false,
                    stockErrorMessage = null
                )

                // Trigger conversion if we have shares
                if (_uiState.value.numberOfShares.isNotEmpty()) {
                    performConversion()
                }
            }.onFailure {
                _uiState.value = _uiState.value.copy(
                    isLoadingStock = false,
                    stockErrorMessage = "Failed to fetch stock data"
                )
            }
        }
    }

    fun hideStockSearch() {
        _uiState.value = _uiState.value.copy(showStockSearch = false)
    }

    fun applyStockFromList(stock: StockWithPrice) {
        // Find the currency (assuming USD for most US stocks)
        val currency = _uiState.value.availableCurrencies.find {
            it.code == "USD"
        } ?: _uiState.value.fromCurrency

        _uiState.value = _uiState.value.copy(
            stockSymbol = stock.symbol,
            stockName = stock.name,
            amount = stock.price.toString(),
            fromCurrency = currency
        )

        // Trigger conversion if we have shares
        if (_uiState.value.numberOfShares.isNotEmpty()) {
            performConversion()
        }
    }

    fun onAmountChanged(newAmount: String) {
        // Only allow valid decimal numbers
        if (newAmount.isEmpty() || newAmount.matches(Regex("^\\d*\\.?\\d*$"))) {
            _uiState.value = _uiState.value.copy(amount = newAmount)

            // Debounce conversion
            conversionJob?.cancel()
            if (newAmount.isNotEmpty() && newAmount != ".") {
                conversionJob = viewModelScope.launch {
                    delay(300)
                    performConversion()
                }
            } else {
                _uiState.value = _uiState.value.copy(
                    convertedAmount = "",
                    totalCostInFrom = "",
                    totalCostInTo = "",
                    inverseRateText = "",
                    errorMessage = null
                )
            }
        }
    }

    fun onNumberOfSharesChanged(newShares: String) {
        // Only allow valid decimal numbers
        if (newShares.isEmpty() || newShares.matches(Regex("^\\d*\\.?\\d*$"))) {
            _uiState.value = _uiState.value.copy(numberOfShares = newShares)

            // Recalculate if we have an amount
            if (_uiState.value.amount.isNotEmpty() && newShares.isNotEmpty() && newShares != ".") {
                conversionJob?.cancel()
                conversionJob = viewModelScope.launch {
                    delay(300)
                    performConversion()
                }
            } else if (newShares.isEmpty()) {
                // Clear total costs if shares are cleared
                _uiState.value = _uiState.value.copy(
                    totalCostInFrom = "",
                    totalCostInTo = ""
                )
            }
        }
    }

    fun onFromCurrencySelected(currency: CurrencyItem) {
        _uiState.value = _uiState.value.copy(
            fromCurrency = currency,
            showCurrencyPicker = false,
            rateHistory = emptyList() // Reset history when currency changes
        )
        if (_uiState.value.amount.isNotEmpty()) {
            performConversion()
        }
    }

    fun onToCurrencySelected(currency: CurrencyItem) {
        _uiState.value = _uiState.value.copy(
            toCurrency = currency,
            showCurrencyPicker = false,
            rateHistory = emptyList() // Reset history when currency changes
        )
        if (_uiState.value.amount.isNotEmpty()) {
            performConversion()
        }
    }

    fun onSwapCurrencies() {
        val currentState = _uiState.value
        _uiState.value = currentState.copy(
            fromCurrency = currentState.toCurrency,
            toCurrency = currentState.fromCurrency,
            rateHistory = emptyList() // Reset history when swapping
        )
        if (currentState.amount.isNotEmpty()) {
            performConversion()
        }
    }

    fun showCurrencyPicker(isFromCurrency: Boolean) {
        _uiState.value = _uiState.value.copy(
            showCurrencyPicker = true,
            isPickingFromCurrency = isFromCurrency
        )
    }

    fun hideCurrencyPicker() {
        _uiState.value = _uiState.value.copy(showCurrencyPicker = false)
    }

    fun toggleChart() {
        _uiState.value = _uiState.value.copy(showChart = !_uiState.value.showChart)
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
        // Only start if we've had at least one successful conversion
        if (!hasPerformedFirstConversion) {
            return
        }

        autoRefreshJob?.cancel()
        autoRefreshJob = viewModelScope.launch {
            while (_uiState.value.isAutoRefreshEnabled) {
                delay(3_000) // Refresh every 3 seconds for real-time rate updates
                if (_uiState.value.amount.isNotEmpty()) {
                    performConversion(isAutoRefresh = true)
                }
            }
        }
    }

    private fun stopAutoRefresh() {
        autoRefreshJob?.cancel()
        autoRefreshJob = null
    }

    private fun performConversion(isAutoRefresh: Boolean = false) {
        val currentState = _uiState.value
        val amount = currentState.amount.toDoubleOrNull() ?: return

        if (amount <= 0) return

        viewModelScope.launch {
            if (!isAutoRefresh) {
                _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            }

            val result = repository.convertCurrency(
                amount = amount,
                from = currentState.fromCurrency.code,
                to = currentState.toCurrency.code
            )

            result.onSuccess { conversionResult ->
                val numberOfShares = currentState.numberOfShares.toDoubleOrNull()

                val totalInFrom = if (numberOfShares != null && numberOfShares > 0) {
                    String.format(Locale.US, "%.2f", amount * numberOfShares)
                } else ""

                val totalInTo = if (numberOfShares != null && numberOfShares > 0) {
                    String.format(
                        Locale.US,
                        "%.2f",
                        conversionResult.convertedAmount * numberOfShares
                    )
                } else ""

                // Update rate history
                val currentHistory = currentState.rateHistory
                val newPoint = ExchangeRatePoint(
                    timestamp = System.currentTimeMillis(),
                    rate = conversionResult.rate
                )
                val updatedHistory = (currentHistory + newPoint).takeLast(maxHistoryPoints)

                // Calculate rate change
                val rateChange = if (currentHistory.size >= 2) {
                    conversionResult.rate - currentHistory[currentHistory.size - 1].rate
                } else null

                _uiState.value = _uiState.value.copy(
                    convertedAmount = String.format(
                        Locale.US,
                        "%.2f",
                        conversionResult.convertedAmount
                    ),
                    totalCostInFrom = totalInFrom,
                    totalCostInTo = totalInTo,
                    inverseRateText = "1 ${conversionResult.fromCurrency} = ${
                        String.format(
                            Locale.US,
                            "%.4f",
                            conversionResult.rate
                        )
                    } ${conversionResult.toCurrency}",
                    isLoading = false,
                    errorMessage = null,
                    rateHistory = updatedHistory,
                    currentRate = conversionResult.rate,
                    rateChange = rateChange,
                    lastUpdateTime = System.currentTimeMillis()
                )

                // Start auto-refresh after first successful conversion
                if (!hasPerformedFirstConversion) {
                    hasPerformedFirstConversion = true
                    if (_uiState.value.isAutoRefreshEnabled) {
                        startAutoRefresh()
                    }
                }
            }.onFailure {
                _uiState.value = _uiState.value.copy(
                    convertedAmount = "N/A",
                    totalCostInFrom = "",
                    totalCostInTo = "",
                    inverseRateText = "",
                    isLoading = false,
                    errorMessage = "Couldn't update rates"
                )
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        stopAutoRefresh()
        conversionJob?.cancel()
        stockSearchJob?.cancel()
    }
}
