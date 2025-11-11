# Real-Time Graphs Guide

## Overview

The Forex Calculator App now includes comprehensive real-time price graphs that display live
exchange rates and stock prices with automatic updates. This guide explains all the graph features
and how to use them.

## Features

### 1. Real-Time Currency Exchange Graphs

**Location:** Main conversion screen

**Features:**

- Live exchange rate tracking between selected currency pairs
- Historical rate visualization with up to 30 data points
- Real-time rate change indicators (positive/negative)
- Auto-refresh every 10 seconds
- Color-coded trend visualization:
    - Green: Rate increasing
    - Red: Rate decreasing

**How to Use:**

1. Enter an amount in the main screen
2. Select your from and to currencies
3. The graph will automatically appear showing the exchange rate history
4. Toggle the chart visibility using the chart icon in the top bar
5. The graph updates automatically every 10 seconds

**Technical Details:**

- Component: `CurrencyExchangeChart`
- ViewModel: `MainViewModel`
- Update Interval: 10 seconds
- Max History Points: 30
- Data Source: Currency exchange API via `CurrencyRepository`

### 2. Forex Market Screen

**Location:** Accessible via "Forex Market" button on main screen

**Features:**

- Display of 10 popular forex pairs with real-time rates
- Individual graphs for each currency pair
- Simultaneous tracking of multiple pairs
- Rate change tracking
- Auto-refresh for all pairs

**Popular Pairs Tracked:**

1. USD/EUR - US Dollar to Euro
2. USD/GBP - US Dollar to British Pound
3. USD/JPY - US Dollar to Japanese Yen
4. USD/CHF - US Dollar to Swiss Franc
5. EUR/GBP - Euro to British Pound
6. EUR/JPY - Euro to Japanese Yen
7. GBP/USD - British Pound to US Dollar
8. AUD/USD - Australian Dollar to US Dollar
9. USD/CAD - US Dollar to Canadian Dollar
10. NZD/USD - New Zealand Dollar to US Dollar

**How to Use:**

1. Click the "Forex Market" button on the main screen
2. View all forex pairs with their current rates and graphs
3. Scroll through the list to see all tracked pairs
4. Rates update automatically every 10 seconds
5. Use the refresh button to manually update all rates
6. The auto-refresh indicator shows when updates are active

**Technical Details:**

- Component: `ForexMarketScreen`
- ViewModel: `ForexMarketViewModel`
- Update Interval: 10 seconds
- Max History Points per Pair: 30
- Data Source: Currency exchange API via `CurrencyRepository`

### 3. Stock Price Graphs

**Location:** Stock List Screen

**Features:**

- Real-time stock price tracking
- Individual price history graphs for each stock
- Price change indicators with percentage
- Top movers comparison chart
- Auto-refresh every 30 seconds
- Toggle charts on/off

**How to Use:**

1. Click "Browse All Stocks with Prices" on the main screen
2. View the list of stocks with current prices
3. Charts are shown by default for each stock
4. Toggle charts using the chart icon in the top bar
5. View the "Top Movers" comparison chart at the bottom
6. Filter by: Most Active, Gainers, or Losers

**Chart Types:**

**Individual Stock Charts:**

- Display: Line chart showing price over time
- Data Points: Last 20 price updates
- Updates: Every 30 seconds
- Color Coding: Green (price up), Red (price down)

**Top Movers Chart:**

- Display: Horizontal bar chart
- Shows: Top 5 stocks by percentage change
- Updates: With each refresh
- Color Coding: Green (gainers), Red (losers)

**Technical Details:**

- Components: `StockChart`, `StockComparisonChart`
- ViewModel: `StockListViewModel`
- Update Interval: 30 seconds
- Max History Points: 20
- Data Source: Yahoo Finance API via `YahooFinanceApiService`

## UI Components

### Chart Toggle Button

**Location:** Top app bar (right side)

**Function:**

- Shows/hides graphs in the current screen
- Icon changes based on state
- State is preserved per screen

### Auto-Refresh Indicator

**Location:** Top app bar (right side)

**Function:**

- Shows when auto-refresh is active
- Spinning update icon
- Visible on all screens with real-time data

### Last Update Timestamp

**Location:** Top app bar subtitle

**Function:**

- Shows when data was last refreshed
- Format: YYYY-MM-DD HH:MM:SS
- Updates with each refresh cycle

## Graph Visualization

### Line Charts

**Used for:**

- Currency exchange rates
- Stock prices over time

**Features:**

- X-axis: Time (data point index)
- Y-axis: Price/Rate value
- Smooth line interpolation
- Grid lines for easier reading
- Responsive scaling

**Library:** Vico Charts (Compose Material 3)

### Bar Charts

**Used for:**

- Stock comparison (Top Movers)

**Features:**

- Horizontal orientation
- Symbol labels
- Percentage values
- Normalized width based on change magnitude
- Color coding by direction

## Performance Optimization

### Data Management

- Maximum history points limit prevents memory issues
- Old data points automatically removed (FIFO)
- Efficient state updates using Kotlin Flow
- Coroutine-based async operations

### Update Strategy

- Debounced input for conversion
- Staggered API calls to avoid rate limiting
- Background updates using viewModelScope
- Automatic cleanup when screen is destroyed

## Error Handling

### API Failures

- Fallback to cached data
- User-friendly error messages
- Retry functionality
- Graceful degradation

### Network Issues

- Automatic retry with exponential backoff
- Offline mode support (shows last known data)
- Clear indicators when data is stale

## Customization

### Update Intervals

**Current Settings:**

- Currency rates: 10 seconds
- Stock prices: 30 seconds

**To Change:**
Edit in respective ViewModels:

```kotlin
// MainViewModel.kt
delay(10_000) // Refresh every 10 seconds

// StockListViewModel.kt
delay(30_000) // Refresh every 30 seconds
```

### History Points

**Current Settings:**

- Currency rates: 30 points
- Stock prices: 20 points

**To Change:**
Edit in respective ViewModels:

```kotlin
// MainViewModel.kt
private val maxHistoryPoints = 30

// StockListViewModel.kt
private val maxHistoryPoints = 20
```

### Tracked Forex Pairs

**To Add/Remove Pairs:**
Edit in `ForexMarketViewModel.kt`:

```kotlin
private val popularPairs = listOf(
    "USD" to "EUR",
    // Add more pairs here
)
```

## Troubleshooting

### Graphs Not Showing

1. Check internet connection
2. Ensure you have entered an amount/selected currencies
3. Toggle chart visibility (may be hidden)
4. Check if API limits have been reached

### Data Not Updating

1. Verify auto-refresh is enabled (look for update icon)
2. Check API service status
3. Restart the app
4. Clear app data and retry

### Performance Issues

1. Reduce update frequency
2. Limit number of history points
3. Hide charts when not needed
4. Close unused screens

## API Information

### Currency Exchange Rates

- Source: exchangerate-api.com
- Rate Limit: Varies by plan
- Update Frequency: Real-time

### Stock Prices

- Source: Yahoo Finance API
- Rate Limit: Varies by usage
- Update Frequency: Near real-time (15-min delay for some data)

## Future Enhancements

### Planned Features

1. Historical data export (CSV/PDF)
2. Custom time range selection
3. Multiple chart types (candlestick, area)
4. Price alerts and notifications
5. Comparison mode (overlay multiple pairs)
6. Dark mode optimized colors
7. Gesture controls (pinch to zoom)
8. Full-screen chart view
9. Technical indicators (MA, RSI, etc.)
10. Offline mode with local data storage

## Best Practices

### For Best Performance

1. Close screens when not in use
2. Limit number of tracked pairs
3. Use Wi-Fi for extended sessions
4. Keep app updated

### For Accurate Data

1. Ensure stable internet connection
2. Use manual refresh if auto-refresh seems delayed
3. Cross-reference with other sources for important decisions
4. Note the last update timestamp

### For Battery Life

1. Disable auto-refresh when not actively monitoring
2. Close the app when done
3. Reduce screen brightness
4. Use dark mode (when available)

## Code Architecture

### Components Layer

- `CurrencyExchangeChart.kt`: Currency rate visualization
- `StockChart.kt`: Stock price visualization
- `StockComparisonChart.kt`: Comparative stock analysis

### ViewModel Layer

- `MainViewModel.kt`: Main screen state and currency rates
- `ForexMarketViewModel.kt`: Forex market data management
- `StockListViewModel.kt`: Stock list and price tracking

### Data Layer

- `CurrencyRepository.kt`: Currency exchange API integration
- `StockRepository.kt`: Stock market API integration
- `YahooFinanceApiService.kt`: Yahoo Finance API client

### State Management

- Kotlin Flow for reactive state updates
- StateFlow for UI state observation
- Coroutines for async operations
- MutableStateFlow for state mutations

## Support

For issues or questions:

1. Check this guide first
2. Review API documentation
3. Check internet connectivity
4. Verify API keys and limits
5. Submit issue to repository

---

**Version:** 1.0  
**Last Updated:** 2025  
**Status:** Production Ready
