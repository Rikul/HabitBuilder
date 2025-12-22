# HabitTracker Context

## Project Overview
HabitTracker is a minimalist, open-source Android application for tracking habits. It features a modern tech stack built entirely with Jetpack Compose and adheres to clean architecture principles. The project is designed to be a playground for exploring new Android technologies while delivering a functional and polished product.

### Key Technologies
*   **Language:** Kotlin
*   **UI Framework:** Jetpack Compose (Material3)
*   **Architecture:** Modular, MVVM, Single Activity
*   **Dependency Injection:** Hilt
*   **Asynchronous Processing:** Kotlin Coroutines & Flow
*   **Local Storage:** Room Database
*   **Build System:** Gradle (Kotlin DSL) with Version Catalogs (`libs.versions.toml`) and Convention Plugins

## Module Structure
The project follows a modular architecture to separate concerns and improve build times:

*   **`app`**: The main application module wiring everything together. Contains `MainActivity` and Application class.
*   **`core-*`**: Fundamental modules used across features.
    *   `core-common`: Shared utility classes and extensions.
    *   `core-database`: Room database definition, DAOs, and persistence logic.
    *   `core-model`: Domain entities and data classes.
    *   `core-testing`: Shared testing utilities and rules.
    *   `core-ui`: Common UI components, theme, and design system.
*   **`feature-*`**: Independent feature modules.
    *   `feature-dashboard`: The main screen showing habit lists and status.
    *   `feature-insights`: Statistics and visualization of habit data.
    *   `feature-misc`: Settings, license info, and other miscellaneous screens.
    *   `feature-widgets`: Home screen widget implementation (Glance).
*   **`build-logic`**: Custom Gradle convention plugins for consistent module configuration.

## Building and Running

### Prerequisites
*   JDK 17 or higher
*   Android Studio Iguana or newer (recommended)

### Key Commands
*   **Build Debug APK:**
    ```bash
    ./gradlew assembleDebug
    ```
*   **Run Unit Tests:**
    ```bash
    ./gradlew test
    ```
*   **Run Instrumented Tests:**
    ```bash
    ./gradlew connectedAndroidTest
    ```
*   **Run Lint Checks:**
    ```bash
    ./gradlew lint
    ```
*   **Dependency Analysis (Build Health):**
    ```bash
    ./gradlew buildHealth
    ```
*   **App Size Analysis (Ruler):**
    ```bash
    ./gradlew analyzeReleaseBundle
    ```
*   **License Check:**
    ```bash
    ./gradlew licenseeRelease
    ```

## Development Conventions

### Code Style
*   Follows official Kotlin coding conventions.
*   Uses a strict modularization strategy; avoid circular dependencies between modules.

### Testing Strategy
*   **Unit Tests:** Focus on ViewModels, UseCases, and Domain logic.
*   **Instrumented Tests:** Used for Database operations (Room).
*   **UI Tests:** Compose UI tests for screens and components.
*   **Flow Testing:** Uses [Turbine](https://github.com/cashapp/turbine) for testing Coroutine Flows.

### Dependency Management
*   Dependencies are managed centrally in `gradle/libs.versions.toml`.
*   Modules apply shared configurations via convention plugins defined in `build-logic`.

### UI/UX
*   **Showkase:** A browser for UI components is available in debug builds.
*   **Material3:** The app uses Material Design 3 components and theming.
