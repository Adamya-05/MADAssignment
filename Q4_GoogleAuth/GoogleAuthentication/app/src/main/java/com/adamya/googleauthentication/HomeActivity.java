package com.adamya.googleauthentication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.material.button.MaterialButton;

public class HomeActivity extends AppCompatActivity {

    private static final String TAG = "HomeActivity";
    private GoogleSignInClient mGoogleSignInClient;
    private TextView userEmailTextView;
    private MaterialButton logoutButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.home_layout), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize UI components
        userEmailTextView = findViewById(R.id.userEmailTextView);
        logoutButton = findViewById(R.id.logoutButton);

        // Configure Google Sign In client for sign out
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_GAMES_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        // Get user email from intent
        Intent intent = getIntent();
        String userEmail = intent.getStringExtra("user_email");
        String userName = intent.getStringExtra("user_name");

        // Display user email
        if (userEmail != null) {
            userEmailTextView.setText(userEmail);
            Log.d(TAG, "Displaying email: " + userEmail);
        } else {
            // Fall back to checking for signed in account
            if (GoogleSignIn.getLastSignedInAccount(this) != null) {
                userEmail = GoogleSignIn.getLastSignedInAccount(this).getEmail();
                userEmailTextView.setText(userEmail);
                Log.d(TAG, "Displaying email from GoogleSignIn: " + userEmail);
            } else {
                Log.e(TAG, "No user email found, returning to sign in");
                returnToSignIn();
            }
        }

        // Set up logout button
        logoutButton.setOnClickListener(v -> signOut());
    }

    private void signOut() {
        // Disable button to prevent multiple clicks
        logoutButton.setEnabled(false);
        logoutButton.setText("Signing out...");

        // Sign out from Google
        mGoogleSignInClient.signOut()
                .addOnCompleteListener(this, task -> {
                    // Re-enable button
                    logoutButton.setEnabled(true);
                    logoutButton.setText(getString(R.string.logout));

                    if (task.isSuccessful()) {
                        // Sign out successful
                        Toast.makeText(this, "Signed out successfully", Toast.LENGTH_SHORT).show();
                        returnToSignIn();
                    } else {
                        // Sign out failed
                        Toast.makeText(this, "Sign out failed", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void returnToSignIn() {
        // Return to MainActivity for sign in
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        // Prevent navigating back to sign in screen
        super.onBackPressed();
        moveTaskToBack(true);
    }
} 