# RoadAlertness

## Overview

RoadAlertness is a mobile application designed to address the critical issue of sleep-deprived driving, a significant contributor to motor-vehicle accidents. The application employs a context-aware approach, utilizing biometric data, sleep schedules, prescription priorities, and user-reported health symptoms to assess a user's fitness for driving. The goal is to provide personalized recommendations, guiding users to choose between driving and public transportation based on their current state.

## Features

- **User Input for Health Symptoms:**
    - Users can input and rate 10 different health symptoms on a scale of 1 to 5.
    - The application calculates a weighted sum based on the user's ratings.

- **Fuzzy Logic Assessment:**
    - The computed value from user input is fed into a fuzzy logic controller.
    - Fuzzy logic, combined with other inputs, determines the user's fitness for driving.

- **Data Storage and Retrieval:**
    - User symptom input records are stored securely in a SQLite database.
    - Users can view their historical records in the "View Records" section.

- **Transportation Recommendations:**
    - Based on the fuzzy logic assessment and other contextual factors, the application provides personalized recommendations for the best transportation method.

## Tech Stack

    - **Android Studio:** Primary IDE for Android application development.
    - **Java:** Core programming languages for backend logic.
    - **SQLite Database:** Used for secure storage and retrieval of user records.

## Getting Started

1. **Clone the Repository:**
    ```bash
    git clone https://github.com/tusanand/RoadAlertness.git
    ```

2. **Open in Android Studio:**
    - Launch Android Studio and open the project.

3. **Update local.properties to point to the Android studio sdk**

4. **Build and Run:**
    - Build and run the application on an Android emulator or a physical device.

5. **Explore Features:** 
    - Click the `Add Symptoms` button.
    - Input your health symptoms and rate them.
    - Click the `Save Symptoms` button.
    - Input other parameters like hear rate, respiratory rate, sleep hours, compute reaction time.
    - On the `Dashboard` page, click the `Save Record` button to save the data into the database.
    - To view your historical records, click on the panel at the top.
    - A list appears on the screen, click on the list item to view the symptoms data.