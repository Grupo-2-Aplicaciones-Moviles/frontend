# Phase 13: Scheduled Booking Implementation Summary

## Overview
Phase 13 implements the scheduled booking functionality for the WeRide Android app, allowing users to schedule vehicle reservations for future times with automatic notifications.

## Completed Features

### 1. ScheduledBookingViewModel ✅
**File:** `ui/screens/scheduledBooking/ScheduledBookingViewModel.kt`

**Features:**
- Manages vehicle data loading and state
- Date/time selection management
- Duration selection (15, 30, 45, 60, 90, 120 minutes)
- Vehicle availability checking for selected time slots
- Booking creation with validation
- Estimated cost calculation
- Form validation (must be at least 15 minutes in future, max 7 days ahead)
- Notification scheduling integration

**State Management:**
- Vehicle loading states (loading, error, success)
- Date/time selection state
- Duration selection
- Availability check status and results
- Booking creation status and results
- Form validation status

### 2. ScheduledBookingScreen ✅
**File:** `ui/screens/scheduledBooking/ScheduledBookingScreen.kt`

**UI Components:**
- Vehicle information card with type icon and details
- Date selector with Material 3 DatePicker
- Time selector with Material 3 TimePicker
- Duration selector with chip-based selection
- Estimated cost display card
- Availability check button with status indicator
- Confirm booking button with loading state

**User Flow:**
1. View vehicle details
2. Select booking date (via date picker)
3. Select booking time (via time picker)
4. Choose duration from preset options
5. Verify vehicle availability
6. Confirm booking
7. Receive navigation to confirmation

### 3. NotificationScheduler Utility ✅
**File:** `utils/NotificationScheduler.kt`

**Features:**
- Notification channel creation for Android O+
- Exact alarm scheduling for booking reminders
- Multiple reminder intervals (60, 30, 15 minutes before)
- Cancellation of scheduled reminders
- Immediate notification display for testing/alerts
- Proper PendingIntent management with immutable flags

**Methods:**
- `scheduleBookingReminder()` - Schedule single reminder
- `scheduleMultipleReminders()` - Schedule multiple reminders
- `cancelBookingReminder()` - Cancel specific reminder
- `cancelAllReminders()` - Cancel all reminders for booking
- `showImmediateNotification()` - Show notification instantly

### 4. Navigation Integration ✅
**Files Updated:**
- `ui/navigation/Screen.kt` - Route already defined
- `ui/navigation/MainNavigation.kt` - Route added to navigation graph
- `di/AppModule.kt` - ViewModel registered with Koin DI

**Navigation Flow:**
```
VehicleDetailScreen → [Programar button] → ScheduledBookingScreen → [Success] → HomeScreen
```

### 5. VehicleDetailScreen Enhancement ✅
**File:** `ui/screens/vehicle/VehicleDetailScreen.kt`

**Changes:**
- Added `onScheduleClick` parameter
- Split "Reservar ahora" button into two buttons:
  - "Reservar ahora" - Primary action (immediate booking)
  - "Programar" - Secondary action (scheduled booking)
- Used `WeRideOutlinedButton` for secondary action
- Proper navigation to `ScheduledUnlock` route

## Integration Points

### Backend API Integration
The implementation uses existing repository methods:
- `VehicleRepository.getVehicleById()` - Load vehicle details
- `BookingRepository.createBooking()` - Create scheduled booking
- `BookingRepository.searchBookings()` - Check vehicle availability

### Date/Time Handling
- Uses `java.time` API for date/time manipulation
- ISO 8601 format for API communication
- Spanish locale formatting for display
- Local time zone handling

### Notification System
- Uses `AlarmManager` for exact timing
- Fallback to inexact alarms if permission denied
- `NotificationManager` for display
- Proper channel configuration for Android O+

## Technical Implementation Details

### Form Validation
- Minimum lead time: 15 minutes from now
- Maximum advance booking: 7 days
- Duration options: 15, 30, 45, 60, 90, 120 minutes
- Real-time validation updates

### Availability Check
- Queries backend for conflicting bookings
- Checks for "confirmed" and "active" status bookings
- Provides clear availability feedback
- Shows conflicting status if unavailable

### Cost Calculation
- Real-time estimated cost based on:
  - Vehicle's price per minute
  - Selected duration
- Displayed with proper formatting (S/ currency)

### User Experience
- Loading states for all async operations
- Error handling with user-friendly messages
- Success confirmation after booking
- Back navigation support
- Disabled states for invalid selections

## Dependencies Used

### Existing
- Koin (Dependency Injection)
- Retrofit (API calls)
- Kotlin Coroutines (Async operations)
- Jetpack Compose (UI)
- Material 3 (Design components)

### Android APIs
- AlarmManager (Notification scheduling)
- NotificationManager (Notification display)
- PendingIntent (Notification intents)

## File Structure

```
app/src/main/java/weTech/weRide/
├── ui/
│   ├── screens/
│   │   └── scheduledBooking/
│   │       ├── ScheduledBookingViewModel.kt (NEW)
│   │       └── ScheduledBookingScreen.kt (NEW)
│   ├── screens/
│   │   └── vehicle/
│   │       └── VehicleDetailScreen.kt (MODIFIED)
│   └── navigation/
│       ├── Screen.kt (MODIFIED - route already defined)
│       └── MainNavigation.kt (MODIFIED - route added)
├── di/
│   └── AppModule.kt (MODIFIED - ViewModel registered)
└── utils/
    └── NotificationScheduler.kt (NEW)
```

## Testing Recommendations

### Unit Tests Needed
1. `ScheduledBookingViewModel`
   - Date/time selection logic
   - Duration selection
   - Form validation
   - Cost calculation
   - Availability check logic

2. `NotificationScheduler`
   - Reminder scheduling
   - Cancellation logic
   - Notification creation

### Integration Tests Needed
1. End-to-end booking flow
2. Navigation between screens
3. API integration with backend
4. Notification delivery

### UI Tests Needed
1. Date picker interaction
2. Time picker interaction
3. Duration selection
4. Form validation feedback
5. Loading states
6. Error states

## Known Limitations

1. **User ID**: Currently hardcoded as `1L` - should be retrieved from auth state
2. **Notification Receiver**: The `BookingReminderReceiver` class is documented but not yet registered in AndroidManifest.xml
3. **Exact Alarm Permission**: May need `SCHEDULE_EXACT_ALARM` permission for Android 12+
4. **Notification Icons**: Uses placeholder icon resource - should be replaced with actual app icon

## Next Steps (Phase 14)

1. **User Authentication Integration**
   - Replace hardcoded user ID with actual user from auth state
   - Implement proper auth token management

2. **Notification Receiver Registration**
   - Create `BookingReminderReceiver` as separate file
   - Register in AndroidManifest.xml
   - Test notification delivery

3. **Permission Handling**
   - Request exact alarm permission for Android 12+
   - Handle notification permissions for Android 13+
   - Graceful fallback if permissions denied

4. **QR Scanner Integration**
   - Integrate QR scanner for vehicle unlock
   - Connect with scheduled booking flow

5. **Production Polish**
   - Add skeleton screens
   - Improve error handling
   - Add analytics tracking
   - Performance optimization

## Build Instructions

```bash
# Navigate to project directory
cd Frontend-WeRide

# Build debug APK
./gradlew assembleDebug

# Run on emulator
./gradlew installDebug

# Run tests
./gradlew test
```

## API Requirements

The following backend API endpoints are used:

1. **GET /api/v1/vehicles/{id}**
   - Purpose: Load vehicle details
   - Response: VehicleResource

2. **POST /api/v1/bookings**
   - Purpose: Create scheduled booking
   - Request: CreateBookingRequest
   - Response: BookingResource

3. **GET /api/v1/bookings**
   - Purpose: Check vehicle availability
   - Query params: vehicleId, startAtFrom, startAtTo, status
   - Response: List<BookingResource>

## Notes

- All date/times are stored and transmitted as ISO 8601 strings
- Time zone handling uses device's local time zone
- Notifications are scheduled using device's AlarmManager
- Form validation ensures bookings are at least 15 minutes in the future
- Maximum booking lead time is 7 days
- Duration presets are based on common usage patterns
