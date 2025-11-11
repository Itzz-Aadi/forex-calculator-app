package com.example.forexcalculatorapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.forexcalculatorapp.data.StockWithPrice
import com.example.forexcalculatorapp.ui.screens.MainScreen
import com.example.forexcalculatorapp.ui.screens.StockListScreen
import com.example.forexcalculatorapp.ui.screens.ForexMarketScreen
import com.example.forexcalculatorapp.ui.screens.GeminiChatScreen
import com.example.forexcalculatorapp.ui.theme.SimpleConvertTheme
import com.example.forexcalculatorapp.viewmodel.MainViewModel
import com.example.forexcalculatorapp.viewmodel.StockListViewModel
import com.example.forexcalculatorapp.viewmodel.ForexMarketViewModel
import com.example.forexcalculatorapp.viewmodel.GeminiViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SimpleConvertTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    ForexCalculatorApp()
                }
            }
        }
    }
}

@Composable
fun ForexCalculatorApp() {
    val mainViewModel: MainViewModel = viewModel()
    val stockListViewModel: StockListViewModel = viewModel()
    val forexMarketViewModel: ForexMarketViewModel = viewModel()
    val geminiViewModel: GeminiViewModel = viewModel()

    var showStockList by remember { mutableStateOf(false) }
    var showForexMarket by remember { mutableStateOf(false) }
    var showGeminiChat by remember { mutableStateOf(false) }
    var selectedStock by remember { mutableStateOf<StockWithPrice?>(null) }

    // Capture current currency info for Gemini context
    val mainUiState by mainViewModel.uiState.collectAsState()

    if (showStockList) {
        StockListScreen(
            viewModel = stockListViewModel,
            onStockSelected = { stock ->
                selectedStock = stock
                showStockList = false
            },
            onBackPressed = {
                showStockList = false
            }
        )
    } else if (showForexMarket) {
        ForexMarketScreen(
            viewModel = forexMarketViewModel,
            onBackPressed = {
                showForexMarket = false
            }
        )
    } else if (showGeminiChat) {
        GeminiChatScreen(
            viewModel = geminiViewModel,
            onBackPressed = {
                showGeminiChat = false
            },
            fromCurrency = mainUiState.fromCurrency.code,
            toCurrency = mainUiState.toCurrency.code,
            currentRate = mainUiState.currentRate
        )
    } else {
        MainScreen(
            viewModel = mainViewModel,
            selectedStock = selectedStock,
            onBrowseStocksClick = {
                showStockList = true
            },
            onForexMarketClick = {
                showForexMarket = true
            },
            onGeminiChatClick = {
                showGeminiChat = true
            },
            onStockUsed = {
                selectedStock = null
            }
        )
    }
}
