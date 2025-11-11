package com.example.forexcalculatorapp.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Update
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.forexcalculatorapp.data.StockWithPrice
import com.example.forexcalculatorapp.ui.components.StockChart
import com.example.forexcalculatorapp.ui.components.StockComparisonChart
import com.example.forexcalculatorapp.viewmodel.StockListViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StockListScreen(
    onStockSelected: (StockWithPrice) -> Unit,
    onBackPressed: () -> Unit,
    viewModel: StockListViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.loadStocks()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            "All Stocks with Prices",
                            fontWeight = FontWeight.Bold
                        )
                        if (uiState.lastUpdateTime > 0) {
                            Text(
                                text = "Updated: ${formatTime(uiState.lastUpdateTime)}",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBackPressed) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
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
                    IconButton(onClick = { viewModel.toggleCharts() }) {
                        Icon(
                            Icons.Default.ShowChart,
                            contentDescription = "Toggle charts",
                            tint = if (uiState.showCharts) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    IconButton(onClick = { viewModel.loadStocks() }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Refresh")
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
        ) {
            // Category Tabs
            ScrollableTabRow(
                selectedTabIndex = uiState.selectedCategory,
                modifier = Modifier.fillMaxWidth()
            ) {
                Tab(
                    selected = uiState.selectedCategory == 0,
                    onClick = { viewModel.selectCategory(0) },
                    text = { Text("Most Active") }
                )
                Tab(
                    selected = uiState.selectedCategory == 1,
                    onClick = { viewModel.selectCategory(1) },
                    text = { Text("Gainers") }
                )
                Tab(
                    selected = uiState.selectedCategory == 2,
                    onClick = { viewModel.selectCategory(2) },
                    text = { Text("Losers") }
                )
            }

            // Stock List
            if (uiState.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator()
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Loading stocks...")
                    }
                }
            } else if (uiState.errorMessage != null) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(32.dp)
                    ) {
                        Text(
                            text = uiState.errorMessage!!,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.error,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = { viewModel.loadStocks() }) {
                            Text("Retry")
                        }
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(uiState.stocks) { stock ->
                        Column {
                            StockListItem(
                                stock = stock,
                                onClick = {
                                    onStockSelected(stock)
                                    onBackPressed()
                                }
                            )
                            if (uiState.showCharts) {
                                Spacer(modifier = Modifier.height(8.dp))
                                // Get live price history from uiState - updates automatically
                                val priceHistory =
                                    uiState.priceHistoryMap[stock.symbol] ?: emptyList()

                                StockChart(
                                    symbol = stock.symbol,
                                    priceHistory = priceHistory,
                                    currentPrice = stock.price,
                                    priceChange = stock.change,
                                    percentChange = stock.percentChange,
                                    modifier = Modifier.padding(horizontal = 8.dp)
                                )
                            }
                        }
                    }

                    // Top movers comparison chart at the end
                    if (uiState.showCharts && uiState.stocks.isNotEmpty()) {
                        item(key = "comparison_chart") {
                            Spacer(modifier = Modifier.height(16.dp))
                            val topMovers = remember(uiState.stocks) {
                                uiState.stocks
                                    .sortedByDescending { kotlin.math.abs(it.percentChange ?: 0.0) }
                                    .take(5)
                                    .map { it.symbol to (it.percentChange ?: 0.0) }
                            }
                            StockComparisonChart(
                                stocks = topMovers,
                                modifier = Modifier.padding(horizontal = 8.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun StockListItem(
    stock: StockWithPrice,
    onClick: () -> Unit
) {
    val isPositive = (stock.change ?: 0.0) >= 0
    val changeColor = if (isPositive) Color(0xFF4CAF50) else Color(0xFFF44336)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Symbol and Name
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = stock.symbol,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = stock.name,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                    maxLines = 1
                )
                Text(
                    text = stock.exchange,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Price and Change
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "$${String.format(Locale.US, "%.2f", stock.price)}",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                if (stock.change != null && stock.percentChange != null) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.End
                    ) {
                        Icon(
                            imageVector = if (isPositive) Icons.Default.TrendingUp else Icons.Default.TrendingDown,
                            contentDescription = null,
                            tint = changeColor,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${if (isPositive) "+" else ""}${
                                String.format(
                                    Locale.US,
                                    "%.2f",
                                    stock.change
                                )
                            } (${if (isPositive) "+" else ""}${
                                String.format(
                                    Locale.US,
                                    "%.2f",
                                    stock.percentChange
                                )
                            }%)",
                            style = MaterialTheme.typography.bodySmall,
                            color = changeColor,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }
    }
}

fun formatTime(time: Long): String {
    val date = Date(time)
    val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US)
    return formatter.format(date)
}
