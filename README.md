# SimpleConvert - Modern Forex Calculator App

A premium, smooth, and incredibly easy-to-use Android Foreign Exchange (Forex) Calculator app built
with Kotlin and Jetpack Compose.

## Features

### Core Functionality

- **Single Screen UI**: All functionality on one clean, uncluttered screen
- **Real-time Currency Conversion**: Automatic conversion as you type with 300ms debounce
- **Large Amount Input**: Clear, focused OutlinedTextField with numeric-decimal keyboard
- **Searchable Currency Selection**: Full-screen modal with search functionality
- **Currency Swap**: Quick FAB button to instantly reverse currency selection
- **Exchange Rate Display**: Shows converted amount and inverse rate (e.g., "1 USD = 1.18 EUR")

### Premium UI/UX

- **Material 3 Design**: Modern, clean interface with light/dark theme support
- **Smooth Animations**:
    - Shimmer loading effect while fetching rates
    - AnimatedContent transitions for result updates
    - Slide-up modal transitions for currency picker
- **Auto-focus**: Amount input automatically receives focus on app launch
- **Error Handling**: Graceful error messages without crashes
- **State-Driven Architecture**: MVVM pattern with StateFlow for reactive UI

## API Integration

Uses the free, key-less **Frankfurter API** (api.frankfurter.dev):

- **Currency List**: `https://api.frankfurter.dev/currencies`
- **Conversion**: `https://api.frankfurter.dev/latest?amount={amount}&from={from}&to={to}`

## Technology Stack

### Architecture

- **MVVM (Model-View-ViewModel)** pattern
- **StateFlow** for state management
- **Kotlin Coroutines** for asynchronous operations
- **Repository Pattern** for data layer abstraction

### Libraries & Dependencies

- **Jetpack Compose** - Modern declarative UI framework
- **Material 3** - Latest Material Design components
- **Retrofit 2** - Type-safe HTTP client
- **Gson** - JSON serialization/deserialization
- **Lifecycle ViewModel Compose** - ViewModel integration with Compose
- **Coroutines** - Asynchronous programming

## Project Structure

```
app/src/main/java/com/example/forexcalculatorapp/
├── data/
│   ├── CurrencyApiService.kt      # Retrofit API interface
│   ├── CurrencyRepository.kt      # Data repository
│   ├── ConversionResponse.kt      # API response models
│   └── RetrofitInstance.kt        # Retrofit singleton
├── ui/
│   ├── components/
│   │   ├── CurrencyCard.kt        # Currency selector card
│   │   ├── CurrencyPickerModal.kt # Searchable currency list
│   │   └── ShimmerEffect.kt       # Loading animation
│   ├── screens/
│   │   └── MainScreen.kt          # Main app screen
│   └── theme/
│       ├── Theme.kt               # Material 3 theme
│       └── Type.kt                # Typography configuration
├── viewmodel/
│   └── MainViewModel.kt           # App state management
└── MainActivity.kt                # App entry point
```

## Building & Running

1. **Clone the repository**
2. **Open in Android Studio** (Hedgehog or later recommended)
3. **Sync Gradle files**
4. **Run on device or emulator** (requires API 25+)

### Requirements

- **Minimum SDK**: 25 (Android 7.1)
- **Target SDK**: 36
- **Kotlin**: 2.0.21
- **Gradle**: 8.13.0

## Key Implementation Details

### Debounced Conversion

API calls are debounced by 300ms to avoid spamming the server while the user types.

### State Management

All UI state is managed through a single `MainUiState` data class, making the UI a pure function of
state.

### Error Handling

Network errors are caught and displayed as non-intrusive messages. The result shows "N/A" when rates
can't be fetched.

### Shimmer Loading

Custom shimmer effect provides visual feedback during API calls, creating a smooth, premium feel.

### Focus Management

The amount input automatically receives focus on app launch for immediate user interaction.

## License

This project is created as a demonstration of modern Android development practices.

## Credits

- **API**: [Frankfurter API](https://www.frankfurter.app/) - Free, open-source currency exchange
  rates
- **Design**: Material 3 Design System
- **Framework**: Jetpack Compose
