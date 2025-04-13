# Google Authentication App

A simple Android application that demonstrates Google Sign-In authentication using Firebase.

## Setup Instructions

### 1. Create a Firebase Project

1. Go to the [Firebase Console](https://console.firebase.google.com/)
2. Click "Add project" and follow the setup steps
3. Register your app with Firebase:
   - Click "Android" icon on the project overview page
   - Enter your app's package name: `com.adamya.googleauthentication`
   - Enter your SHA-1 certificate fingerprint (for development)
   - Download the `google-services.json` file and place it in the app directory

### 2. Enable Google Sign-In

1. In the Firebase Console, go to Authentication
2. Click "Get started" or "Sign-in method" tab
3. Enable Google as a sign-in provider
4. Configure the OAuth consent screen in Google Cloud Console if prompted

### 3. Update the Web Client ID

1. Open `app/src/main/res/values/strings.xml`
2. Replace `YOUR_WEB_CLIENT_ID` in the `default_web_client_id` string with the actual Web client ID from your Firebase project

## Features

- Google Sign-In authentication
- Home screen with welcome message
- Logout functionality

## How It Works

1. Main screen shows a Google Sign-In button
2. On successful authentication, user is directed to the home screen
3. Home screen displays a welcome message with the user's email
4. The logout button signs the user out and returns to the login screen 