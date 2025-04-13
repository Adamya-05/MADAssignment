package com.adamya.googleauthentication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "GoogleSignIn";
    private GoogleSignInClient mGoogleSignInClient;
    private ActivityResultLauncher<Intent> signInLauncher;
    private ProgressBar progressBar;
    private SignInButton signInButton;
    private boolean isUsingFallback = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize UI elements
        progressBar = findViewById(R.id.progressBar);
        signInButton = findViewById(R.id.signInButton);

        // Set up sign-in client with standard configuration
        setupGoogleSignIn(false);

        // Set up activity result launcher for sign-in
        signInLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    // Hide the loading indicator
                    progressBar.setVisibility(View.GONE);
                    signInButton.setEnabled(true);
                    
                    try {
                        // Get signed in account from the intent data
                        Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(result.getData());
                        GoogleSignInAccount account = task.getResult(ApiException.class);
                        
                        // Sign in successful
                        Log.d(TAG, "Sign in successful: " + account.getEmail());
                        Toast.makeText(this, "Sign-in successful!", Toast.LENGTH_SHORT).show();
                        
                        // Navigate to home activity
                        goToHomeScreen(account);
                    } catch (ApiException e) {
                        // Sign in failed
                        String errorMessage;
                        Log.e(TAG, "Sign in failed: code=" + e.getStatusCode() + ", message=" + e.getMessage());
                        
                        // More detailed error message
                        switch (e.getStatusCode()) {
                            case 12501:
                                // User canceled sign-in - this is normal behavior, don't show error
                                Log.d(TAG, "User canceled the sign-in process - this is normal behavior");
                                return;
                                
                            case 10:
                                if (!isUsingFallback) {
                                    Log.e(TAG, "Developer Error 10: Trying fallback method...");
                                    // Try fallback method but only once
                                    tryFallbackSignIn();
                                    return; // Exit early - don't show the error toast since we're trying again
                                } else {
                                    showFirebaseConfigurationHelp();
                                    errorMessage = "Sign-in configuration issue. See logs for details.";
                                }
                                break;
                                
                            case 12500:
                                errorMessage = "Google Play Services out of date. Please update.";
                                break;
                                
                            case 7:
                                errorMessage = "Network error. Check your internet connection.";
                                break;
                                
                            case 5:
                                errorMessage = "Invalid account. Please try another Google account.";
                                break;
                                
                            case 8:
                                errorMessage = "Internal error. Please try again.";
                                break;
                                
                            case 4:
                                errorMessage = "Sign-in was canceled.";
                                break;
                                
                            default:
                                errorMessage = "Sign-in failed: code=" + e.getStatusCode();
                        }
                        
                        // Show error message
                        Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();
                        Log.e(TAG, errorMessage);
                    }
                });

        // Set up sign in button
        signInButton.setOnClickListener(v -> signIn());
    }

    private void showFirebaseConfigurationHelp() {
        Log.e(TAG, "==== FIREBASE CONFIGURATION HELP ====");
        Log.e(TAG, "Error 10 means your app is not properly configured for Google Sign-In.");
        Log.e(TAG, "To fix this, follow these steps:");
        Log.e(TAG, "1. Go to Firebase console -> Authentication -> Sign-in method");
        Log.e(TAG, "2. Enable Google as a sign-in provider");
        Log.e(TAG, "3. Go to Project Settings -> Your Android app");
        Log.e(TAG, "4. Add your SHA-1 certificate fingerprint");
        Log.e(TAG, "5. Download the new google-services.json file and replace the one in your app folder");
        Log.e(TAG, "6. Make sure the oauth_client array in google-services.json is not empty");
        Log.e(TAG, "7. Check that the package name in the Android app matches your app's package name");
        Log.e(TAG, "==== END HELP ====");
    }

    @Override
    protected void onStart() {
        super.onStart();
        
        // Check if user is already signed in
        GoogleSignInAccount lastSignedInAccount = GoogleSignIn.getLastSignedInAccount(this);
        if (lastSignedInAccount != null) {
            Log.d(TAG, "User already signed in: " + lastSignedInAccount.getEmail());
            goToHomeScreen(lastSignedInAccount);
        }
    }

    private void setupGoogleSignIn(boolean useFallback) {
        // Configure Google Sign In options
        GoogleSignInOptions.Builder builder;
        
        if (useFallback) {
            Log.d(TAG, "Using GAMES_SIGN_IN as fallback");
            builder = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_GAMES_SIGN_IN);
        } else {
            Log.d(TAG, "Using standard DEFAULT_SIGN_IN");
            builder = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN);
        }
        
        // Add email request and skip token retrieval since we don't have a server client ID
        GoogleSignInOptions gso = builder
                .requestEmail()
                .requestProfile() // Also request profile info
                .build();
        
        // Create client with options
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        
        // Print debug info about the current configuration
        Log.d(TAG, "Google Sign-In configured with " + (useFallback ? "GAMES_SIGN_IN" : "DEFAULT_SIGN_IN") + 
              " and requesting email and profile");
    }

    private void signIn() {
        // Reset fallback flag when starting a new sign-in process
        isUsingFallback = false;
        
        // Set up standard sign-in method
        setupGoogleSignIn(false);
        
        // Show loading indicator
        progressBar.setVisibility(View.VISIBLE);
        signInButton.setEnabled(false);
        
        // Launch sign in intent
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        signInLauncher.launch(signInIntent);
    }

    // Try fallback authentication method if first attempt fails
    private void tryFallbackSignIn() {
        Log.d(TAG, "Attempting fallback sign-in method");
        Toast.makeText(this, "Trying alternative authentication method...", Toast.LENGTH_SHORT).show();
        
        // Set flag to prevent repeated fallback attempts
        isUsingFallback = true;
        
        // Configure with fallback option
        setupGoogleSignIn(true);
        
        // Show loading indicator
        progressBar.setVisibility(View.VISIBLE);
        signInButton.setEnabled(false);
        
        // Launch sign in intent with fallback options
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        signInLauncher.launch(signInIntent);
    }

    private void goToHomeScreen(GoogleSignInAccount account) {
        // Create intent for home activity
        Intent intent = new Intent(this, HomeActivity.class);
        
        // Pass user information
        intent.putExtra("user_email", account.getEmail());
        intent.putExtra("user_name", account.getDisplayName());
        
        // Start home activity
        startActivity(intent);
        finish();
    }
}