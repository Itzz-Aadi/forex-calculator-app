# SimpleConvert Implementation Guide

## Overview

SimpleConvert is a modern Android Forex Calculator app built with Kotlin and Jetpack Compose. This
guide explains the implementation details, architecture decisions, and how to use the app.

## Architecture

### MVVM Pattern

The app follows the **Model-View-ViewModel (MVVM)** architecture pattern:

- **Model**: Data layer (`CurrencyRepository`, `CurrencyApiService`)
- **View**: UI layer (Composables in `ui/` package)
- **ViewModel**: Business logic and state management (`MainViewModel`)

### State Management

All UI state is managed through a single data class:

```kotlin
data class MainUiState(
    val amount: String = "",
    val fromCurrency: CurrencyItem = CurrencyItem("USD", "United States Dollar"),
    val toCurrency: CurrencyItem = CurrencyItem("EUR", "Euro"),
    val convertedAmount: String = "",
    val inverseRateText: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val availableCurrencies: List<CurrencyItem> = emptyList(),
    val showCurrencyPicker: Boolean = false,
    val isPickingFromCurrency: Boolean = true
)
```

This state is exposed via `StateFlow` and collected in composables using
`collectAsStateWithLifecycle()`.

## Key Components

### 1. API Integration (Data Layer)

#### CurrencyApiService.kt

Retrofit interface defining API endpoints:

- `getCurrencies()`: Fetches all available currencies
- `convertCurrency()`: Performs currency conversion

#### RetrofitInstance.kt

Singleton object providing a configured Retrofit instance with:

- Base URL: `https://api.frankfurter.dev/`
- Gson converter for JSON parsing

#### CurrencyRepository.kt

Repository pattern implementation that:

- Wraps API calls in Kotlin `Result` type
- Handles exceptions gracefully
- Calculates inverse rates for better UX

### 2. ViewModel Layer

#### MainViewModel.kt

Manages all business logic:

**Debounced Conversion**: Uses `Job` and `delay(300)` to debounce API calls

```kotlin
conversionJob?.cancel()
if (newAmount.isNotEmpty() && newAmount != ".") {
    conversionJob = viewModelScope.launch {
        delay(300)  // Debounce
        performConversion()
    }
}
```

**State Updates**: All state changes go through the ViewModel

```kotlin
fun onAmountChanged(newAmount: String)
fun onFromCurrencySelected(currency: CurrencyItem)
fun onToCurrencySelected(currency: CurrencyItem)
fun onSwapCurrencies()
```

### 3. UI Components

#### MainScreen.kt

The main screen composable featuring:

- **Scaffold** with TopAppBar
- **OutlinedTextField** for amount input with auto-focus
- **CurrencyCard** components for From/To currency selection
- **FloatingActionButton** for swapping currencies
- **AnimatedContent** with shimmer effect for loading states
- **CurrencyPickerModal** for currency selection

#### CurrencyCard.kt

Custom card component displaying:

- Label (From/To)
- Currency code (USD, EUR, etc.)
- Full currency name
- Material 3 styling with primaryContainer colors

#### CurrencyPickerModal.kt

Full-screen modal with:

- **AnimatedVisibility** with slide-in animation
- **SearchBar** for filtering currencies
- **LazyColumn** for efficient list rendering
- Real-time search filtering

#### ShimmerEffect.kt

Custom loading animation:

- Uses `InfiniteTransition` for continuous animation
- Linear gradient brush that translates across the view
- Provides visual feedback during API calls

### 4. Theme

#### Theme.kt

Material 3 theme configuration:

- **Light and Dark** color schemes
- **Dynamic colors** support (Android 12+)
- Status bar color customization

#### Type.kt

Typography configuration using Material 3 text styles

## User Flow

1. **App Launch**
    - MainActivity sets up Compose UI
    - MainViewModel initializes and loads currencies from API
    - Amount input receives focus automatically

2. **Currency Conversion**
    - User enters amount in OutlinedTextField
    - Input is validated (numbers and decimal point only)
    - After 300ms debounce, API call is made
    - Shimmer effect displays during loading
    - Result animates in with converted amount and rate

3. **Currency Selection**
    - User taps From/To currency card
    - Modal slides up from bottom
    - User can search by code or name
    - Selected currency updates immediately
    - If amount exists, conversion runs automatically

4. **Swapping Currencies**
    - User taps swap FAB button
    - From and To currencies switch
    - Conversion runs automatically if amount exists

## API Details

### Frankfurter API

**Base URL**: `https://api.frankfurter.dev/`

**Endpoints Used**:

1. **Get Currencies**
   ```
   GET /currencies
   Response: {"USD": "United States Dollar", "EUR": "Euro", ...}
   ```

2. **Convert Currency**
   ```
   GET /latest?amount=100&from=USD&to=EUR
   Response: {
     "amount": 100.0,
     "base": "USD",
     "date": "2024-01-15",
     "rates": {"EUR": 91.85}
   }
   ```

## Error Handling

### Network Errors

- All API calls wrapped in try-catch
- Errors converted to `Result.failure()`
- UI shows "Couldn't update rates" message
- Result displays "N/A"

### Input Validation

- Regex pattern ensures only valid numbers: `^\\d*\\.?\\d*$`
- Empty or zero amounts don't trigger API calls
- Prevents multiple decimal points

### Loading States

- `isLoading` flag controls shimmer animation
- Prevents user confusion during API calls
- Smooth transitions between states

## Performance Optimizations

### 1. Debouncing

Prevents excessive API calls while user types by waiting 300ms after last keystroke.

### 2. LazyColumn

Currency list uses `LazyColumn` for efficient scrolling with thousands of items.

### 3. State Hoisting

State is hoisted to ViewModel, preventing unnecessary recompositions.

### 4. Remember

Search query and filtered lists use `remember` and `derivedStateOf` for efficiency.

### 5. Coroutines

All network calls run on background thread via `viewModelScope`.

## Material 3 Design Features

### Color Scheme

- **Primary**: Main brand color (blue tones)
- **Secondary**: Accent color for FAB (purple tones)
- **Tertiary**: Result card color (green tones)
- **Containers**: Lighter versions for cards and surfaces

### Typography

- **Display Small**: Large result text (36sp)
- **Headline Medium**: Currency codes (28sp)
- **Body Medium**: Descriptions and rates
- **Label Medium**: Field labels

### Components

- **OutlinedTextField**: Clean input with floating label
- **Cards**: Elevated surfaces with rounded corners
- **FAB**: Floating action button for primary actions
- **Modal**: Full-screen dialog with backdrop

## Testing Recommendations

### Unit Tests

- ViewModel state updates
- Repository error handling
- Input validation logic

### UI Tests

- Currency selection flow
- Amount input and validation
- Swap functionality
- Error message display

### Integration Tests

- API integration with mock server
- End-to-end conversion flow

## Future Enhancements

Potential features for future versions:

- **Offline Mode**: Cache last fetched rates
- **Favorites**: Quick access to frequently used currencies
- **History**: Save conversion history
- **Multiple Conversions**: Convert to multiple currencies at once
- **Charts**: Historical rate charts
- **Widget**: Home screen widget for quick conversion
- **Settings**: Custom decimal places, theme selection

## Dependencies

All dependencies are managed in `gradle/libs.versions.toml`:

- **Compose BOM**: 2024.10.01
- **Retrofit**: 2.9.0
- **Activity Compose**: 1.9.3
- **Lifecycle ViewModel**: 2.8.7
- **Coroutines**: 1.8.0

## Building the App

### Prerequisites

- Android Studio Hedgehog (2023.1.1) or later
- JDK 11 or later
- Android SDK with API 36

### Steps

1. Open project in Android Studio
2. Wait for Gradle sync to complete
3. Select device or emulator (API 25+)
4. Click Run button or press Shift+F10

### Troubleshooting

- **Gradle sync issues**: File > Invalidate Caches > Invalidate and Restart
- **Compose preview issues**: Build > Clean Project, then rebuild
- **Network errors**: Check internet connection and API availability

## Code Quality

### Best Practices Followed

- Single Responsibility Principle
- Repository pattern for data access
- State hoisting in Compose
- Immutable data classes
- Proper coroutine cancellation
- Error handling at all layers
- Meaningful variable names
- Comments for complex logic

### Kotlin Features Used

- Data classes
- Sealed classes (Result type)
- Extension functions
- Coroutines and Flow
- Nullable types
- Lambda expressions
- Default parameters

## Conclusion

SimpleConvert demonstrates modern Android development practices with Jetpack Compose, clean
architecture, and Material 3 design. The app provides a smooth, premium user experience with
real-time currency conversion powered by the free Frankfurter API.
