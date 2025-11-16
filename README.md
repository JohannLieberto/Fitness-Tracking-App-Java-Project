# Fitness-Tracking-App-Java-Project
Java Project Assignment

Overview

This Fitness Tracker Application is a command-line Java system that allows users to track workouts, nutrition plans, and goals.

## Features

- User profile creation with BMI calculation
- Record and analyze workouts (cardio, strength, HIIT, yoga, flexibility)
- Sealed exercise hierarchy
- Immutable workout sessions and nutrition plans (using records and defensive copying)
- Goal setting and progress tracking
- Filtering and recommendation features (using lambdas and pattern matching)
- Full error/exception handling

## How to Compile and Run

### Prerequisites

- Java JDK 21 or higher (download from [Oracle](https://www.oracle.com/java/technologies/downloads/) or [Adoptium](https://adoptium.net/))
- Command line (Terminal or Command Prompt)
- No external libraries required

### Step 1: Compile

For Windows:
javac -d bin src\model\.java src\exception\.java src\service\*.java src\FitnessTrackerMain.java

For macOS/Linux:
javac -d bin src/model/.java src/exception/.java src/service/*.java src/FitnessTrackerMain.java


### Step 2: Run

java -cp bin FitnessTrackerMain
