# 📱 ScrollGuard

> **Advanced Digital Wellbeing, Reel & Shorts Counter, and App Blocker for Android**

ScrollGuard monitors and regulates short-form content consumption (Instagram Reels & YouTube Shorts), tracks overall screen time and gaming sessions, visualizes distraction metrics, and enforces mindful app usage limits.

---

## 🎯 Key Features

### 1. 🎬 Reel & Shorts Tracking
- **Native Detection**: Uses Android's `AccessibilityService` (`ScrollDetectorService`) to automatically detect vertical reel/short scroll events in Instagram and YouTube with debouncing.
- **Web Browser Support**: Monitors browser address bars (Chrome, Firefox, Brave, Opera, Edge) for `instagram.com/reels` and `youtube.com/shorts` content.
- **Floating Overlay Bubble**: Draggable, color-coded live widget displaying current scroll count vs daily target (`🎬 23 / 50`). Changes green (<50%), orange (50-80%), to red (80%+).
- **Manual Logging Fallback**: "+1 Scroll" button on the dashboard for quick manual logging.

### 2. ⏱️ Screen Time & App Blocker
- **Usage Stats Integration**: Live background synchronization using `UsageStatsManager` grouped by categories.
- **Customizable Limits**: Set daily limits per app in minutes.
- **Strict Mode vs Flexible Mode**: Choose between an impassable block overlay (`Intent.CATEGORY_HOME`) or a 5-minute extension option.
- **Mindful Focus Mode**: One-tap toggle to immediately silence social media feeds and notifications.

### 3. 🎮 Gaming & Phone Habit Tracking
- **Game Session Tracker**: Tracks duration, launches, and historical play time for gaming apps.
- **Phone Pickup Counter**: Listens for `USER_PRESENT` broadcasts to count daily device unlocks.
- **Notification Tracker**: Identifies the noisiest apps via `NotificationListenerService`.

### 4. 📊 Analytics & Health
- **Thumb Scroll Distance Odometer**: Calculates kilometers scrolled using screen DPI and reel heights.
- **Opportunity Cost Calculator**: Computes financial value of time spent based on user-configured hourly rate.
- **20-20-20 Eye Strain Protection**: Background reminder service prompting a 20-second break every 20 minutes.
- **Bedtime Wind Down**: Scheduled evening alerts with automatic focus mode activation.

---

## 🏗️ Architecture & Tech Stack

- **Language**: Kotlin 2.1.0
- **UI Toolkit**: Jetpack Compose + Material 3 (100% declarative UI)
- **Architecture**: MVVM + Clean Architecture + Repository Pattern
- **Dependency Injection**: Dagger Hilt
- **Persistence**: Room Database (SQLite) + Jetpack DataStore Preferences
- **Asynchronous**: Kotlin Coroutines + StateFlow / Flow
- **Background Processing**: Foreground Services, BroadcastReceivers, WorkManager

---

## 📁 Project Structure

```
com.scrollguard/
├── app/
│   └── ScrollGuardApp.kt              # Hilt Application, notification channels
├── data/
│   ├── db/
│   │   ├── ScrollGuardDatabase.kt     # Room Database definition (10 entities)
│   │   ├── dao/                       # Reel, ScreenTime, Limits, Games, Pickups, etc.
│   │   └── entity/                    # Entity schemas
│   ├── preferences/
│   │   └── UserPreferences.kt         # DataStore preference store
│   └── repository/                    # Clean repositories wrapping DB & System APIs
├── di/                                # Hilt modules (AppModule, DatabaseModule)
├── receiver/                          # BootReceiver, ScreenUnlockReceiver, BedtimeReceiver
├── service/
│   ├── ScrollDetectorService.kt       # Accessibility Service for Reels/Shorts
│   ├── FloatingCounterService.kt      # Floating window overlay bubble
│   ├── ScreenTimeService.kt           # Foreground monitoring service
│   ├── AppBlockerService.kt           # Impassable full-screen overlay
│   ├── EyeStrainReminderService.kt    # 20-20-20 rule timer
│   └── NotificationTrackerService.kt  # NotificationListenerService
├── ui/
│   ├── MainActivity.kt                # Single activity with Material 3 NavigationBar
│   ├── navigation/                    # Compose NavHost & Routes
│   ├── screen/
│   │   ├── dashboard/                 # Overview, live counters, top apps
│   │   ├── analytics/                 # Scroll distance, cost calculator, platform splits
│   │   ├── applimits/                 # App limit manager & dialogs
│   │   ├── focus/                     # Deep work & focus mode
│   │   ├── challenges/                # Digital detox streaks
│   │   ├── settings/                  # Limits, hourly wage, bedtime, eye strain
│   │   └── onboarding/                # Permission setup wizard
│   └── theme/                         # Material 3 typography, colors, theme
└── worker/                            # Daily and weekly summary workers, data cleanup
```

---

## 🚀 Getting Started

### Prerequisites
- Android Studio Ladybug (2024.2+) or newer
- JDK 17 or JDK 21
- Android SDK 35 (compileSdk: 35, minSdk: 26)

### How to Run
1. Open Android Studio.
2. Select **Open** and choose the `ScrollGuard` directory:
   `c:\Users\Ansh Jain\Desktop\Code_learning\AntiGravity\ScrollGuard`
3. Wait for Gradle sync to complete.
4. Run on an Android emulator or physical device running Android 8.0+ (API 26+).
5. Follow the in-app **Onboarding Wizard** to grant:
   - **Usage Access** (for screen time tracking)
   - **Accessibility Service** (for reel & shorts scroll detection)
   - **Draw Over Other Apps** (for the floating counter & app blocker)
