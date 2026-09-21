# Rural Job Hub

An Android app (Java) for managing rural employment schemes. It connects village-level officials, supervisors, and labourers on one platform for registration, job card issuing with QR codes, job allotment, attendance, payments, and work progress inspection.

## User Roles & Features

### Pradhan (Admin)
- Secure login and password change
- Add and manage jobs
- Add Rozgar Sewaks
- Add, view, and delete Field Inspectors
- View labourer list and approve labourer registrations
- Issue job cards to approved labourers
- View labourer details
- View work progress reports
- View issues and feedback raised by field inspectors

### Rozgar Sewak
- Secure login and password change
- Add labourers
- View available jobs
- Allot labourers to jobs
- Scan a labourer's job card (QR code) and view its details
- Mark and view attendance
- Make payments and view payment details
- View before/after proof photos of work

### Field Inspector
- Secure login and password change
- Inspect work progress
- Mark attendance
- Raise issues and submit feedback

### Labourer
- Sign up and log in
- Register for the scheme
- Demand for a job
- View job card
- Generate a QR code for the job card
- Upload before/after proof photos of completed work
- Change password

## Tech Stack
- **Language:** Java
- **Platform:** Android (Gradle build)
- **Backend:** Firebase (via `google-services.json`)
- **Other:** QR code generation and scanning, image upload

## Project Structure

```
app/src/main/java/com/android/project/
├── activitycontrollers/
│   ├── labourer/
│   ├── pradhan/
│   ├── rozgarsewak/
│   └── fieldinspector/
├── adapters/      # RecyclerView/ListView adapters
├── model/         # Data models (Job, Labourer, Attendance, ...)
└── utility/       # Helpers (QR code, image path, constants)
```

## Getting Started

### Prerequisites
- Android Studio
- JDK 8 or higher
- An Android device or emulator
- A Firebase project

### Setup
1. Clone the repository
   ```bash
   git clone https://github.com/ChinthanaBhat/Rural-Job-Hub.git
   ```
2. Open the project in **Android Studio**.
3. Add your own `google-services.json` to the `app/` folder (download it from your Firebase console).
4. Let Gradle sync, then click **Run** on a device or emulator.

## Contributing
Pull requests are welcome. For major changes, please open an issue first to discuss what you would like to change.


