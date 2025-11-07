package com.example.forexcalculatorapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.forexcalculatorapp.data.CurrencyRepository
import com.example.forexcalculatorapp.data.ConversionResult
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class CurrencyItem(
    val code: String,
    val name: String
)

data class MainUiState(
    val amount: String = "",
    val fromCurrency: CurrencyItem = CurrencyItem("USD", "United States Dollar"),
    val toCurrency: CurrencyItem = CurrencyItem("EUR", "Euro"),
    val convertedAmount: String = "",
    val inverseRateText: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val availableCurrencies: List<CurrencyItem> = emptyList(),
    val showCurrencyPicker: Boolean = false,
    val isPickingFromCurrency: Boolean = true
)

class MainViewModel : ViewModel() {
    private val repository = CurrencyRepository()

    private val _uiState = MutableStateFlow(MainUiState())
    val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()

    private var conversionJob: Job? = null

    init {
        loadCurrencies()
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
                    inverseRateText = "",
                    errorMessage = null
                )
            }
        }
    }

    fun onFromCurrencySelected(currency: CurrencyItem) {
        _uiState.value = _uiState.value.copy(
            fromCurrency = currency,
            showCurrencyPicker = false
        )
        if (_uiState.value.amount.isNotEmpty()) {
            performConversion()
        }
    }

    fun onToCurrencySelected(currency: CurrencyItem) {
        _uiState.value = _uiState.value.copy(
            toCurrency = currency,
            showCurrencyPicker = false
        )
        if (_uiState.value.amount.isNotEmpty()) {
            performConversion()
        }
    }

    fun onSwapCurrencies() {
        val currentState = _uiState.value
        _uiState.value = currentState.copy(
            fromCurrency = currentState.toCurrency,
            toCurrency = currentState.fromCurrency
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

    private fun performConversion() {
        val currentState = _uiState.value
        val amount = currentState.amount.toDoubleOrNull() ?: return

        if (amount <= 0) return

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

            val result = repository.convertCurrency(
                amount = amount,
                from = currentState.fromCurrency.code,
                to = currentState.toCurrency.code
            )

            result.onSuccess { conversionResult ->
                _uiState.value = _uiState.value.copy(
                    convertedAmount = String.format("%.2f", conversionResult.convertedAmount),
                    inverseRateText = "1 ${conversionResult.fromCurrency} = ${
                        String.format(
                            "%.4f",
                            conversionResult.rate
                        )
                    } ${conversionResult.toCurrency}",
                    isLoading = false,
                    errorMessage = null
                )
            }.onFailure {
                _uiState.value = _uiState.value.copy(
                    convertedAmount = "N/A",
                    inverseRateText = "",
                    isLoading = false,
                    errorMessage = "Couldn't update rates"
                )
            }
        }
    }
}
