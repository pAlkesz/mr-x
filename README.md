![mrx_feature_graphic](https://github.com/user-attachments/assets/fc6405d5-b83b-4f75-839e-8f9f0950267c)

Mr. X Mobile App
==================

**Mr. X** is a Compose Multiplatform app built entirely with Kotlin and Jetpack Compose. It
follows Material3 design and Android development best practices and is intended to be a
hobby and showcase project.

The app is currently in development and only available to a closed circle of testers.  

# Features

**Mr. X** is a question and answer based game in which a host has to think of a person and
players have to figure out who the host thought of based on a system of rules. For more information
please visit the tutorial within the app. Users can create and join games to play Mr. X with other players. 

## Screenshots

Home screen                |  Game Screen               |  Tutorial screen
:-------------------------:|:-------------------------: | :-------------------------:
![home_screenshot](https://github.com/user-attachments/assets/1f62d381-dec1-46e6-a7ce-cd4880561ce4)  |  ![games_screenshot](https://github.com/user-attachments/assets/2d7f1691-b656-4349-a926-7fd18be66308) | ![tutorial_screenshot](https://github.com/user-attachments/assets/8ea679e7-d6d2-4819-859e-94d37373d49d)

# Development Environment

**Mr. X** uses the Gradle build system and can be imported directly into Android Studio and Xcode but also JetBrains Fleet.
More information about Compose Multiplatform developer environment setup is available [here](https://www.jetbrains.com/help/kotlin-multiplatform-dev/compose-multiplatform-create-first-app.html)   

The default run configuration is the composeApp which builds the Android app but with the iOSApp run configuration the iOS app also can be build directly from Android Studio.

# Architecture

The **Mr. X** app follows the
[official architecture guidance](https://developer.android.com/topic/architecture) 
on Android and uses Clean Architecture, MVVM and Koin for dependency injection.

# Modularization

The **Mr. X** app has only one module because it is a small app but the folder structure resembles the modularization strategy used in
[modularization learning journey](docs/ModularizationLearningJourney.md). This is intentional so the app is prepared for modularization in the future should it be needed.

# Testing

The app contains unit tests in the common source set and uses the kotlin.test library to run them. These tests are executed on every push to the main branch. Lint is also executed on every push.



