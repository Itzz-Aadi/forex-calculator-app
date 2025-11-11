# Real-Time Graphs Feature Summary

## Quick Overview

Your Forex Calculator App now includes **three types of real-time price graphs**:

## 1. 📊 Main Screen - Currency Exchange Chart

**What it shows:**

- Live exchange rate between your selected currency pair
- Historical trend (last 30 data points)
- Rate change indicator (green ↑ or red ↓)

**How to access:**

1. Enter an amount on the main screen
2. Select from/to currencies
3. Chart appears automatically below the conversion result

**Auto-refresh:** Every 10 seconds

---

## 2. 💱 Forex Market Screen

**What it shows:**

- 10 popular currency pairs with individual charts
- USD/EUR, USD/GBP, USD/JPY, USD/CHF, EUR/GBP, EUR/JPY, GBP/USD, AUD/USD, USD/CAD, NZD/USD
- Each pair has its own real-time chart
- Rate change tracking for each pair

**How to access:**

1. Click "Forex Market" button (with trending up icon) on main screen
2. Scroll through all pairs

**Auto-refresh:** Every 10 seconds for all pairs

---

## 3. 📈 Stock List Screen - Multiple Stock Charts

**What it shows:**

- Individual price charts for each stock
- Price history with trend indicators
- Top Movers comparison bar chart

**How to access:**

1. Click "Browse All Stocks with Prices" button on main screen
2. View individual charts for each stock
3. Scroll to bottom for Top Movers chart

**Auto-refresh:** Every 30 seconds

---

## Key Features

### ✅ Auto-Refresh

All charts update automatically in the background without manual intervention.

### ✅ Toggle Visibility

Use the chart icon in the top bar to show/hide graphs on any screen.

### ✅ Visual Indicators

- **Green**: Price/rate going up
- **Red**: Price/rate going down
- **Update Icon**: Shows when auto-refresh is active
- **Timestamp**: Last update time displayed in top bar

### ✅ Historical Tracking

- Currency pairs: Up to 30 data points
- Stocks: Up to 20 data points
- Automatic data point rotation (oldest removed when limit reached)

---

## Controls

### Top Bar Controls (Available on All Screens)

| Icon | Function |
|------|----------|
| 📊 Chart Icon | Toggle chart visibility on/off |
| 🔄 Update Icon | Indicates auto-refresh is active |
| ↻ Refresh Button | Manually refresh data now |
| Last Updated | Shows timestamp of last data fetch |

---

## Technical Details

### Chart Library

**Vico Charts** - Modern, Material 3 compatible charting library

- Smooth animations
- Responsive scaling
- Touch-friendly interface

### Data Sources

- **Currency Rates**: exchangerate-api.com
- **Stock Prices**: Yahoo Finance API

### Performance

- Efficient state management with Kotlin Flow
- Coroutine-based async updates
- Memory-optimized with data point limits
- Automatic cleanup on screen exit

---

## Usage Tips

### 💡 Best Practices

1. **For accurate monitoring**: Keep the screen active and connected to Wi-Fi
2. **To save battery**: Toggle off charts when not actively viewing them
3. **For manual control**: Disable auto-refresh and use manual refresh button
4. **For specific pairs**: Use Main Screen for focused tracking of one pair
5. **For market overview**: Use Forex Market Screen to monitor multiple pairs

### ⚠️ Things to Note

- Charts require internet connection
- Some stock data may have 15-minute delay
- API rate limits may apply with heavy usage
- Historical data resets when changing currency pairs/stocks

---

## Navigation Flow

```
Main Screen
├─> Enter amount & select currencies → Chart appears automatically
├─> "Forex Market" button → Forex Market Screen (10 pairs with charts)
└─> "Browse All Stocks" button → Stock List Screen (individual stock charts)
```

---

## Quick Start Guide

### To view your first chart:

1. **Open the app**
2. **Enter any amount** (e.g., 100)
3. **Select currencies** (default is USD to EUR)
4. **Wait 1 second** - Chart appears automatically!
5. **Watch it update** - New data point every 10 seconds

### To view multiple forex pairs:

1. **Click "Forex Market"** button on main screen
2. **Scroll through the list** of 10 popular pairs
3. **Each has its own live chart**
4. **Watch all update simultaneously** every 10 seconds

### To view stock charts:

1. **Click "Browse All Stocks with Prices"** on main screen
2. **See charts for each stock** (if toggle is on)
3. **Switch between** Most Active / Gainers / Losers tabs
4. **View Top Movers chart** at the bottom

---

## Customization

Want to change update intervals or tracked pairs?

See **[Complete Graph Guide](REAL_TIME_GRAPHS_GUIDE.md)** for customization options.

---

## Need Help?

📚 **Full Documentation**: [REAL_TIME_GRAPHS_GUIDE.md](REAL_TIME_GRAPHS_GUIDE.md)  
🏗️ **Architecture Details**: Check ViewModels in `app/src/main/java/viewmodel/`  
🎨 **UI Components**: Check components in `app/src/main/java/ui/components/`

---

**Feature Status:** ✅ Production Ready  
**Last Updated:** 2025  
**Version:** 1.0
