package com.example.forexcalculatorapp.ui.components

import android.util.Log
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

data class StockPricePoint(
    val timestamp: Long,
    val price: Double
)

private const val TAG = "StockChart"

@Composable
fun StockChart(
    symbol: String,
    priceHistory: List<StockPricePoint>,
    currentPrice: Double,
    priceChange: Double?,
    percentChange: Double?,
    modifier: Modifier = Modifier
) {
    val isPositive = (priceChange ?: 0.0) >= 0
    val chartColor = if (isPositive) Color(0xFF4CAF50) else Color(0xFFF44336)

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
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
                Text(
                    text = symbol,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "$${String.format("%.2f", currentPrice)}",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    if (priceChange != null && percentChange != null) {
                        Text(
                            text = buildString {
                                append(if (isPositive) "+" else "")
                                append(String.format("%.2f", priceChange))
                                append(" (")
                                append(if (isPositive) "+" else "")
                                append(String.format("%.2f", percentChange))
                                append("%)")
                            },
                            style = MaterialTheme.typography.bodyMedium,
                            color = chartColor,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Chart area
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                contentAlignment = Alignment.Center
            ) {
                when {
                    priceHistory.size >= 2 -> {
                        // Show chart with enough data
                        StockLineChart(
                            priceHistory = priceHistory,
                            chartColor = chartColor,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                        )
                    }

                    priceHistory.size == 1 -> {
                        // Building data
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
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                            )
                        }
                    }

                    else -> {
                        // No data yet
                        Text(
                            text = "Loading chart data...",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Time range indicator
            if (priceHistory.isNotEmpty()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Last ${priceHistory.size} updates",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                    )
                    Text(
                        text = "Live",
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
fun StockLineChart(
    priceHistory: List<StockPricePoint>,
    chartColor: Color,
    modifier: Modifier = Modifier
) {
    // Only render if we have enough data
    if (priceHistory.size < 2) {
        Box(
            modifier = modifier,
            contentAlignment = Alignment.Center
        ) {
            Text("Need more data")
        }
        return
    }

    val chartEntryModelProducer = remember { ChartEntryModelProducer() }

    LaunchedEffect(priceHistory) {
        try {
            val entries = priceHistory.mapIndexed { index, point ->
                FloatEntry(x = index.toFloat(), y = point.price.toFloat())
            }
            chartEntryModelProducer.setEntries(entries)
            Log.d(TAG, "Chart updated with ${entries.size} entries")
        } catch (e: Exception) {
            Log.e(TAG, "Error updating chart", e)
        }
    }

    Chart(
        chart = lineChart(),
        chartModelProducer = chartEntryModelProducer,
        startAxis = rememberStartAxis(),
        bottomAxis = rememberBottomAxis(),
        modifier = modifier
    )
}

@Composable
fun StockComparisonChart(
    stocks: List<Pair<String, Double>>,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Top Movers",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))

            stocks.forEach { (symbol, change) ->
                ComparisonBar(symbol = symbol, percentChange = change)
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
private fun ComparisonBar(
    symbol: String,
    percentChange: Double
) {
    val isPositive = percentChange >= 0
    val barColor = if (isPositive) Color(0xFF4CAF50) else Color(0xFFF44336)
    val normalizedWidth = (kotlin.math.abs(percentChange) / 10.0).coerceIn(0.0, 1.0)

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = symbol,
            style = MaterialTheme.typography.labelLarge,
            modifier = Modifier.width(60.dp),
            fontWeight = FontWeight.Medium
        )

        Box(
            modifier = Modifier
                .weight(1f)
                .height(24.dp)
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth(normalizedWidth.toFloat())
                    .height(24.dp),
                color = barColor.copy(alpha = 0.7f),
                shape = MaterialTheme.shapes.small
            ) {}
        }

        Text(
            text = buildString {
                append(if (isPositive) "+" else "")
                append(String.format("%.2f", percentChange))
                append("%")
            },
            style = MaterialTheme.typography.labelMedium,
            color = barColor,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.width(70.dp)
        )
    }
}
