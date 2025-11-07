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
import androidx.compose.material.icons.filled.SwapVert
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
import com.example.forexcalculatorapp.ui.components.CurrencyCard
import com.example.forexcalculatorapp.ui.components.CurrencyPickerModal
import com.example.forexcalculatorapp.ui.components.ShimmerEffect
import com.example.forexcalculatorapp.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    viewModel: MainViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "SimpleConvert",
                        fontWeight = FontWeight.Bold
                    )
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

            // Amount Input
            OutlinedTextField(
                value = uiState.amount,
                onValueChange = { viewModel.onAmountChanged(it) },
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(focusRequester),
                label = { Text("Amount") },
                placeholder = { Text("Enter amount") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                singleLine = true,
                textStyle = LocalTextStyle.current.copy(
                    fontSize = 24.sp,
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
                        text = "Converted Amount",
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
                                        "Enter an amount"
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

            Spacer(modifier = Modifier.height(32.dp))
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
    }
}
