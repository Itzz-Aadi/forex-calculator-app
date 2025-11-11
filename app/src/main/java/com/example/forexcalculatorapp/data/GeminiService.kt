package com.example.forexcalculatorapp.data

import com.google.firebase.Firebase
import com.google.firebase.vertexai.GenerativeModel
import com.google.firebase.vertexai.type.generationConfig
import com.google.firebase.vertexai.vertexAI
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class GeminiService {

    private val generativeModel: GenerativeModel by lazy {
        Firebase.vertexAI.generativeModel(
            modelName = "gemini-1.5-flash",
            generationConfig = generationConfig {
                temperature = 0.7f
                topK = 40
                topP = 0.95f
                maxOutputTokens = 1024
            }
        )
    }

    /**
     * Send a message to Gemini and get a response
     */
    suspend fun sendMessage(prompt: String): Result<String> {
        return try {
            val response = generativeModel.generateContent(prompt)
            val text = response.text ?: "No response received"
            Result.success(text)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Send a message with streaming response
     */
    fun sendMessageStream(prompt: String): Flow<String> = flow {
        try {
            val response = generativeModel.generateContentStream(prompt)
            response.collect { chunk ->
                chunk.text?.let { emit(it) }
            }
        } catch (e: Exception) {
            emit("Error: ${e.message}")
        }
    }

    /**
     * Send a forex-specific query with context
     */
    suspend fun askForexQuestion(question: String): Result<String> {
        val contextualPrompt = """
            You are a helpful forex and financial market assistant. 
            Answer the following question about forex, currency exchange, or financial markets.
            Keep your answer concise, informative, and easy to understand.
            
            Question: $question
        """.trimIndent()

        return sendMessage(contextualPrompt)
    }

    /**
     * Get market analysis for a currency pair
     */
    suspend fun analyzeCurrencyPair(
        fromCurrency: String,
        toCurrency: String,
        currentRate: Double
    ): Result<String> {
        val prompt = """
            Provide a brief analysis of the $fromCurrency/$toCurrency currency pair.
            Current exchange rate: 1 $fromCurrency = $currentRate $toCurrency
            
            Include:
            1. Brief overview of factors affecting this pair
            2. Recent trends (general market knowledge)
            3. Key economic indicators to watch
            
            Keep it concise (2-3 short paragraphs).
        """.trimIndent()

        return sendMessage(prompt)
    }

    /**
     * Get general forex tips
     */
    suspend fun getForexTips(): Result<String> {
        val prompt = """
            Provide 5 important tips for someone interested in forex trading or currency exchange.
            Keep each tip concise and practical.
        """.trimIndent()

        return sendMessage(prompt)
    }
}
