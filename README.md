# SmartFit Mobile Application

BIT219 Mobile Application Development Project (Assignment)

## Group Members

- Aaditya Chaudhary - E2300548
- Aakroshan Chaudhary - E2300551

## Project Description

The SmartFit Mobile Application is an Android-based fitness tracking system developed using Kotlin and Jetpack Compose. The application allows users to record and monitor their daily physical activities such as steps, workouts, and calories burned.

Users can add, view, edit, and delete their activity logs while also viewing daily and weekly summaries of their fitness progress. The application connects to an online REST API to fetch workout suggestions and exercise tips to help users maintain a healthy lifestyle.

The system also stores activity logs locally using a Room Database and saves user preferences such as theme mode and step goals using Jetpack DataStore. The application demonstrates modern Android development practices including Material Design UI, navigation between multiple screens, API integration, debugging, and automated testing.

## Technologies Used

- Kotlin
- Jetpack Compose
- Material Design 3
- Android Navigation Compose
- Retrofit (REST API Integration)
- Kotlin Coroutines
- Room Database
- Jetpack DataStore
- JUnit (Unit Testing)
- Compose UI Testing

## Application Features

### Activity Management

- Add new activity logs
- View activity history
- Edit existing activities
- Delete activities

### Fitness Dashboard

- View daily steps progress
- Track calories burned
- Animated progress bar for step goal

### Workout Suggestions

- Fetch workout tips from an online API
- Display exercise images and descriptions

### User Preferences

- Dark mode / Light mode theme
- Set daily step goals
- Save user name

### Navigation

The application contains multiple screens including:

- Welcome Screen
- Home Dashboard
- Activity Log Screen
- Add Activity Screen
- Profile / Settings Screen

## Installation / Setup

1. Install Android Studio on your computer.
2. Clone or download this repository.
3. Open Android Studio and select **Open Existing Project**.
4. Navigate to the project folder and open it.
5. Allow Gradle Sync to complete.
6. Connect an Android device or start an Android emulator.
7. Click **Run** to launch the SmartFit application.

### Minimum Requirements

- Android Studio Hedgehog or newer
- Android SDK version 24 or above
- Internet connection for API features

## Debugging and Testing

### Debugging

Logging statements were added to monitor important system operations including:

- API requests and responses
- Database insert and update operations
- Navigation between screens

### Testing

The project includes both Unit Testing and UI Testing.

#### Unit Testing

- Step progress calculation
- Calorie calculation logic

#### UI Testing

- Navigation between screens
- Display of activity logs
- Loading of workout suggestions

Testing was implemented using JUnit and Jetpack Compose UI Testing tools.

## GitHub Repository

- Repository Name: https://github.com/iconicaditya/SmartFit.git
