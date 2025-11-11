# 📈 Forex Calculator with Automatic Stock Fetching

## 🆕 New Feature: Automatic Stock Data Fetching

Your Forex Calculator app now automatically fetches stock names and prices in real-time!

### ✨ How It Works

#### 1. **Search for a Stock**

- Enter a stock symbol (e.g., `AAPL`, `TSLA`, `GOOGL`) in the first field
- The app will automatically search for matching stocks as you type
- A dropdown will appear showing available stocks

#### 2. **Select Your Stock**

- Tap on any stock from the search results
- The app automatically fetches:
    - ✅ **Stock Name** (e.g., "Apple Inc")
    - ✅ **Current Price** (real-time market price)
    - ✅ **Currency** (automatically sets the correct currency)

#### 3. **Calculate Total Cost**

- Enter the **number of shares** you want to buy/sell
- The app calculates:
    - Price per share in your local currency
    - **Total cost in stock's currency**
    - **Total cost in your local currency**

### 📊 Example Usage

**Scenario:** You want to buy 50 shares of Apple stock

1. **Type:** `AAPL` in the stock symbol field
2. **Select:** "Apple Inc (AAPL)" from the dropdown
3. **Auto-fills:**
    - Stock Name: "Apple Inc"
    - Price: $175.50 (current market price)
    - Currency: USD
4. **Enter:** `50` in Number of Shares
5. **See Results:**
    - Price per share: ₹14,625.00 INR (if converting to INR)
    - Total USD: $8,775.00
    - Total INR: ₹7,31,250.00

### 🌍 Supported Stock Exchanges

- 🇺🇸 **US Markets** (NYSE, NASDAQ) - e.g., AAPL, TSLA, GOOGL
- 🇬🇧 **London Stock Exchange** (LSE) - e.g., HSBA.LON
- 🇯🇵 **Tokyo Stock Exchange** (TSE) - e.g., 7203.T
- 🇩🇪 **Frankfurt Stock Exchange** (FRA) - e.g., VOW.F
- 🇮🇳 **Indian Stock Exchange** (NSE/BSE) - e.g., RELIANCE.NS
- And many more global exchanges!

### 🔍 Search Tips

- **US Stocks:** Just type the symbol (e.g., `MSFT`)
- **International Stocks:** Add exchange suffix (e.g., `RELIANCE.NS` for Indian stocks)
- **Search by Name:** You can also search by company name
- **Partial Match:** Type just a few letters to see all matching stocks

### 💡 Features

- 🔄 **Real-time Data:** Fetches current market prices
- 🌐 **Multi-Currency:** Automatically detects stock currency
- 📱 **Easy Search:** Type-ahead search with instant results
- 💰 **Total Cost Calculator:** Shows complete breakdown
- 🔁 **Auto-Conversion:** Converts to your preferred currency instantly
- 📊 **Exchange Rate Display:** Shows current forex rate

### 🎯 Popular Stock Symbols

| Symbol | Company | Exchange |
|--------|---------|----------|
| AAPL | Apple Inc | NASDAQ |
| TSLA | Tesla Inc | NASDAQ |
| MSFT | Microsoft Corporation | NASDAQ |
| GOOGL | Alphabet Inc | NASDAQ |
| AMZN | Amazon.com Inc | NASDAQ |
| META | Meta Platforms Inc | NASDAQ |
| NVDA | NVIDIA Corporation | NASDAQ |
| RELIANCE.NS | Reliance Industries | NSE (India) |
| TCS.NS | Tata Consultancy Services | NSE (India) |
| HSBA.LON | HSBC Holdings | LSE (UK) |

### 📲 Installation

The APK is located at:

```
C:\Users\aadit\AndroidStudioProjects\forexcalculatorapp\app\build\outputs\apk\debug\app-debug.apk
```

Transfer this file to your phone and install it!

### 🛠️ Technical Details

- **Stock API:** Twelve Data API (free tier)
- **Forex API:** ExchangeRate-API
- **Update Frequency:** Real-time (on-demand)
- **Supported Currencies:** 35+ major world currencies
- **Debouncing:** 500ms for smooth search experience

### ⚠️ Important Notes

1. **Internet Required:** App needs internet to fetch stock data
2. **Market Hours:** Prices update during market hours
3. **API Limits:** Free tier has usage limits (sufficient for personal use)
4. **Delayed Data:** Some exchanges may have 15-minute delay

### 🎨 UI/UX Improvements

- **Loading Indicator:** Shows when fetching stock data
- **Error Messages:** Clear feedback if stock not found
- **Stock Name Display:** Shows full company name below symbol
- **Smooth Animations:** Shimmer effect during loading
- **Auto-complete:** Instant search results as you type

### 🚀 Future Enhancements (Possible)

- Stock price charts
- Historical price data
- Portfolio tracking
- Price alerts
- Multiple stock comparison
- Profit/loss calculator

---

**Enjoy your enhanced Forex Share Calculator with automatic stock fetching! 📈💱**
