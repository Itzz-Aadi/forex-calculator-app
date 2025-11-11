package com.example.forexcalculatorapp.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material.icons.filled.SwapVert
import androidx.compose.material.icons.filled.Update
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.forexcalculatorapp.data.StockWithPrice
import com.example.forexcalculatorapp.ui.components.CurrencyCard
import com.example.forexcalculatorapp.ui.components.CurrencyExchangeChart
import com.example.forexcalculatorapp.ui.components.CurrencyPickerModal
import com.example.forexcalculatorapp.ui.components.ShimmerEffect
import com.example.forexcalculatorapp.ui.components.StockSearchModal
import com.example.forexcalculatorapp.viewmodel.MainViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    selectedStock: StockWithPrice? = null,
    onBrowseStocksClick: () -> Unit = {},
    onForexMarketClick: () -> Unit = {},
    onGeminiChatClick: () -> Unit = {},
    onStockUsed: () -> Unit = {},
    viewModel: MainViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val focusRequester = remember { FocusRequester() }

    // Handle selected stock from stock list
    LaunchedEffect(selectedStock) {
        selectedStock?.let { stock ->
            viewModel.applyStockFromList(stock)
            onStockUsed()
        }
    }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            "SimpleConvert",
                            fontWeight = FontWeight.Bold
                        )
                        if (uiState.lastUpdateTime > 0) {
                            Text(
                                text = "Last updated: ${formatUpdateTime(uiState.lastUpdateTime)}",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                            )
                        }
                    }
                },
                actions = {
                    // Auto-refresh indicator
                    if (uiState.isAutoRefreshEnabled) {
                        Icon(
                            Icons.Default.Update,
                            contentDescription = "Auto-refresh enabled",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(end = 8.dp)
                        )
                    }
                    // Chart toggle button
                    IconButton(onClick = { viewModel.toggleChart() }) {
                        Icon(
                            if (uiState.showChart) Icons.Default.ShowChart else Icons.Default.SwapVert,
                            contentDescription = "Toggle chart",
                            tint = if (uiState.showChart) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            // Stock Symbol Search
            OutlinedTextField(
                value = uiState.stockSymbol,
                onValueChange = { viewModel.onStockSymbolChanged(it) },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Stock Symbol (e.g., AAPL, TSLA)") },
                placeholder = { Text("Enter stock symbol") },
                singleLine = true,
                isError = uiState.stockErrorMessage != null,
                supportingText = {
                    if (uiState.stockErrorMessage != null) {
                        Text(
                            text = uiState.stockErrorMessage!!,
                            color = MaterialTheme.colorScheme.error
                        )
                    } else if (uiState.stockName.isNotEmpty()) {
                        Text(
                            text = uiState.stockName,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Medium
                        )
                    }
                },
                shape = MaterialTheme.shapes.large,
                trailingIcon = {
                    if (uiState.isLoadingStock) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            strokeWidth = 2.dp
                        )
                    }
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Amount Input (Price per share)
            OutlinedTextField(
                value = uiState.amount,
                onValueChange = { viewModel.onAmountChanged(it) },
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(focusRequester),
                label = { Text("Price per Share") },
                placeholder = { Text("Enter price") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                singleLine = true,
                textStyle = LocalTextStyle.current.copy(
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Medium
                ),
                shape = MaterialTheme.shapes.large
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Number of Shares Input
            OutlinedTextField(
                value = uiState.numberOfShares,
                onValueChange = { viewModel.onNumberOfSharesChanged(it) },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Number of Shares") },
                placeholder = { Text("Enter quantity") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                singleLine = true,
                textStyle = LocalTextStyle.current.copy(
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Medium
                ),
                shape = MaterialTheme.shapes.large
            )

            Spacer(modifier = Modifier.height(32.dp))

            // From Currency Card
            CurrencyCard(
                label = "From",
                currency = uiState.fromCurrency,
                onClick = { viewModel.showCurrencyPicker(true) }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Swap Button
            FloatingActionButton(
                onClick = { viewModel.onSwapCurrencies() },
                modifier = Modifier.size(56.dp),
                containerColor = MaterialTheme.colorScheme.secondary
            ) {
                Icon(
                    Icons.Default.SwapVert,
                    contentDescription = "Swap currencies",
                    tint = MaterialTheme.colorScheme.onSecondary
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // To Currency Card
            CurrencyCard(
                label = "To",
                currency = uiState.toCurrency,
                onClick = { viewModel.showCurrencyPicker(false) }
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Conversion Result
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.large,
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.tertiaryContainer
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Price per Share",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha = 0.7f)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Result with shimmer effect
                    AnimatedContent(
                        targetState = uiState.isLoading,
                        transitionSpec = { fadeIn() togetherWith fadeOut() },
                        label = "result_animation"
                    ) { isLoading ->
                        if (isLoading) {
                            ShimmerEffect(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(60.dp)
                            )
                        } else {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = if (uiState.convertedAmount.isNotEmpty()) {
                                        "${uiState.convertedAmount} ${uiState.toCurrency.code}"
                                    } else {
                                        "Enter price"
                                    },
                                    style = MaterialTheme.typography.displaySmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onTertiaryContainer,
                                    textAlign = TextAlign.Center
                                )

                                if (uiState.inverseRateText.isNotEmpty()) {
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = uiState.inverseRateText,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onTertiaryContainer.copy(
                                            alpha = 0.7f
                                        ),
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        }
                    }

                    // Error message
                    if (uiState.errorMessage != null) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = uiState.errorMessage!!,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.error,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            if (uiState.showChart && uiState.rateHistory.isNotEmpty()) {
                Spacer(modifier = Modifier.height(16.dp))
                CurrencyExchangeChart(
                    fromCurrency = uiState.fromCurrency.code,
                    toCurrency = uiState.toCurrency.code,
                    rateHistory = uiState.rateHistory,
                    currentRate = uiState.currentRate,
                    rateChange = uiState.rateChange
                )
            }

            // Total Cost Card (shown when shares are entered)
            if (uiState.numberOfShares.isNotEmpty() &&
                uiState.totalCostInFrom.isNotEmpty() &&
                uiState.totalCostInTo.isNotEmpty()
            ) {

                Spacer(modifier = Modifier.height(16.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.large,
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Total Cost for ${uiState.numberOfShares} Shares",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Total in From Currency
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = uiState.fromCurrency.code,
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                            Text(
                                text = uiState.totalCostInFrom,
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        HorizontalDivider(
                            color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.3f)
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // Total in To Currency
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = uiState.toCurrency.code,
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                            Text(
                                text = uiState.totalCostInTo,
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = onForexMarketClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Icon(
                    Icons.Default.TrendingUp,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    "Forex Market",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = onBrowseStocksClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Icon(
                    Icons.Default.List,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    "Browse All Stocks with Prices",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = onGeminiChatClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Icon(
                    Icons.Default.Chat,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    "Gemini Chat",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }

        // Currency Picker Modal
        CurrencyPickerModal(
            visible = uiState.showCurrencyPicker,
            currencies = uiState.availableCurrencies,
            onCurrencySelected = { currency ->
                if (uiState.isPickingFromCurrency) {
                    viewModel.onFromCurrencySelected(currency)
                } else {
                    viewModel.onToCurrencySelected(currency)
                }
            },
            onDismiss = { viewModel.hideCurrencyPicker() }
        )

        // Stock Search Modal
        StockSearchModal(
            visible = uiState.showStockSearch,
            searchResults = uiState.stockSearchResults,
            onStockSelected = { stock ->
                viewModel.onStockSelected(stock)
            },
            onDismiss = { viewModel.hideStockSearch() }
        )
    }
}

fun formatUpdateTime(time: Long): String {
    val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
    return formatter.format(Date(time))
}
