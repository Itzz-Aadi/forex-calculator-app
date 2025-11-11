package com.example.forexcalculatorapp.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.patrykandpatrick.vico.compose.axis.horizontal.rememberBottomAxis
import com.patrykandpatrick.vico.compose.axis.vertical.rememberStartAxis
import com.patrykandpatrick.vico.compose.chart.Chart
import com.patrykandpatrick.vico.compose.chart.line.lineChart
import com.patrykandpatrick.vico.core.entry.ChartEntryModelProducer
import com.patrykandpatrick.vico.core.entry.FloatEntry

data class ExchangeRatePoint(
    val timestamp: Long,
    val rate: Double
)

@Composable
fun CurrencyExchangeChart(
    fromCurrency: String,
    toCurrency: String,
    rateHistory: List<ExchangeRatePoint>,
    currentRate: Double,
    rateChange: Double?,
    modifier: Modifier = Modifier
) {
    val isPositive = (rateChange ?: 0.0) >= 0
    val chartColor = if (isPositive) Color(0xFF4CAF50) else Color(0xFFF44336)

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "$fromCurrency → $toCurrency",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Text(
                        text = "Exchange Rate",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = String.format("%.4f", currentRate),
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    if (rateChange != null) {
                        Text(
                            text = "${if (isPositive) "+" else ""}${
                                String.format(
                                    "%.4f",
                                    rateChange
                                )
                            }",
                            style = MaterialTheme.typography.bodyMedium,
                            color = chartColor,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Chart - need at least 2 data points for a meaningful chart
            if (rateHistory.size >= 2) {
                ExchangeRateLineChart(
                    rateHistory = rateHistory,
                    chartColor = chartColor,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                )
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(40.dp),
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Gathering data...",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.6f)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Time range indicator
            if (rateHistory.isNotEmpty()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Last ${rateHistory.size} updates",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.6f)
                    )
                    Text(
                        text = "Real-time",
                        style = MaterialTheme.typography.labelSmall,
                        color = chartColor,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun ExchangeRateLineChart(
    rateHistory: List<ExchangeRatePoint>,
    chartColor: Color,
    modifier: Modifier = Modifier
) {
    // Early return if insufficient data
    if (rateHistory.size < 2) {
        Box(
            modifier = modifier,
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Need more data points",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
            )
        }
        return
    }

    val chartEntryModelProducer = remember { ChartEntryModelProducer() }
    var hasError by remember { mutableStateOf(false) }
    var isInitialized by remember { mutableStateOf(false) }

    LaunchedEffect(rateHistory) {
        try {
            if (rateHistory.size >= 2) {
                val entries = rateHistory.mapIndexed { index, point ->
                    FloatEntry(index.toFloat(), point.rate.toFloat())
                }
                if (entries.isNotEmpty() && entries.size >= 2) {
                    chartEntryModelProducer.setEntries(entries)
                    hasError = false
                    isInitialized = true
                }
            }
        } catch (e: Exception) {
            hasError = true
            println("Error creating chart entries: ${e.message}")
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            // Clean up when the composable is disposed
            isInitialized = false
        }
    }

    if (hasError) {
        Box(
            modifier = modifier,
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Chart error",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.error
            )
        }
    } else if (isInitialized) {
        Chart(
            chart = lineChart(),
            chartModelProducer = chartEntryModelProducer,
            startAxis = rememberStartAxis(),
            bottomAxis = rememberBottomAxis(),
            modifier = modifier
        )
    } else {
        Box(
            modifier = modifier,
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(modifier = Modifier.size(24.dp))
        }
    }
}

// Compact mini chart for currency card
@Composable
fun MiniExchangeRateChart(
    rateHistory: List<ExchangeRatePoint>,
    isPositive: Boolean,
    modifier: Modifier = Modifier
) {
    // Early return if insufficient data
    if (rateHistory.size < 2) {
        return
    }

    val chartColor = if (isPositive) Color(0xFF4CAF50) else Color(0xFFF44336)
    val chartEntryModelProducer = remember { ChartEntryModelProducer() }
    var hasError by remember { mutableStateOf(false) }
    var isInitialized by remember { mutableStateOf(false) }

    LaunchedEffect(rateHistory) {
        try {
            if (rateHistory.size >= 2) {
                val entries = rateHistory.mapIndexed { index, point ->
                    FloatEntry(index.toFloat(), point.rate.toFloat())
                }
                if (entries.isNotEmpty() && entries.size >= 2) {
                    chartEntryModelProducer.setEntries(entries)
                    hasError = false
                    isInitialized = true
                }
            }
        } catch (e: Exception) {
            hasError = true
            println("Error creating mini chart entries: ${e.message}")
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            isInitialized = false
        }
    }

    // Fail silently for mini chart - only render if initialized and no error
    if (!hasError && isInitialized) {
        Chart(
            chart = lineChart(),
            chartModelProducer = chartEntryModelProducer,
            modifier = modifier
        )
    }
}
