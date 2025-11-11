package com.example.forexcalculatorapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.forexcalculatorapp.data.GeminiService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ChatMessage(
    val text: String,
    val isUser: Boolean,
    val timestamp: Long = System.currentTimeMillis()
)

data class GeminiUiState(
    val messages: List<ChatMessage> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val currentInput: String = ""
)

class GeminiViewModel : ViewModel() {

    private val geminiService = GeminiService()

    private val _uiState = MutableStateFlow(GeminiUiState())
    val uiState: StateFlow<GeminiUiState> = _uiState.asStateFlow()

    /**
     * Send a message to Gemini
     */
    fun sendMessage(message: String) {
        if (message.isBlank()) return

        // Add user message
        val userMessage = ChatMessage(text = message, isUser = true)
        _uiState.value = _uiState.value.copy(
            messages = _uiState.value.messages + userMessage,
            isLoading = true,
            error = null,
            currentInput = ""
        )

        viewModelScope.launch {
            val result = geminiService.askForexQuestion(message)

            result.fold(
                onSuccess = { response ->
                    val aiMessage = ChatMessage(text = response, isUser = false)
                    _uiState.value = _uiState.value.copy(
                        messages = _uiState.value.messages + aiMessage,
                        isLoading = false
                    )
                },
                onFailure = { exception ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = exception.message ?: "Unknown error occurred"
                    )
                }
            )
        }
    }

    /**
     * Get analysis for a currency pair
     */
    fun analyzeCurrencyPair(from: String, to: String, rate: Double) {
        _uiState.value = _uiState.value.copy(
            isLoading = true,
            error = null
        )

        viewModelScope.launch {
            val result = geminiService.analyzeCurrencyPair(from, to, rate)

            result.fold(
                onSuccess = { response ->
                    val aiMessage = ChatMessage(
                        text = "📊 Analysis for $from/$to:\n\n$response",
                        isUser = false
                    )
                    _uiState.value = _uiState.value.copy(
                        messages = _uiState.value.messages + aiMessage,
                        isLoading = false
                    )
                },
                onFailure = { exception ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = exception.message ?: "Failed to get analysis"
                    )
                }
            )
        }
    }

    /**
     * Get general forex tips
     */
    fun getForexTips() {
        _uiState.value = _uiState.value.copy(
            isLoading = true,
            error = null
        )

        viewModelScope.launch {
            val result = geminiService.getForexTips()

            result.fold(
                onSuccess = { response ->
                    val aiMessage = ChatMessage(
                        text = "💡 Forex Tips:\n\n$response",
                        isUser = false
                    )
                    _uiState.value = _uiState.value.copy(
                        messages = _uiState.value.messages + aiMessage,
                        isLoading = false
                    )
                },
                onFailure = { exception ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = exception.message ?: "Failed to get tips"
                    )
                }
            )
        }
    }

    /**
     * Update the current input text
     */
    fun updateInput(text: String) {
        _uiState.value = _uiState.value.copy(currentInput = text)
    }

    /**
     * Clear error message
     */
    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    /**
     * Clear all messages
     */
    fun clearMessages() {
        _uiState.value = GeminiUiState()
    }
}
