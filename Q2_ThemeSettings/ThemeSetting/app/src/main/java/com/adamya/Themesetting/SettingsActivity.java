package com.adamya.Themesetting;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

public class SettingsActivity extends AppCompatActivity {

    private Switch themeSwitch;
    private Button backButton;
    private TextView currentThemeText;
    private SharedPreferences sharedPreferences;
    private View settingsLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        themeSwitch = findViewById(R.id.themeSwitch);
        backButton = findViewById(R.id.backButton);
        currentThemeText = findViewById(R.id.currentThemeText);
        settingsLayout = findViewById(R.id.settingsLayout);
        
        sharedPreferences = getSharedPreferences("ThemePrefs", Context.MODE_PRIVATE);
        boolean isDarkTheme = sharedPreferences.getBoolean("dark_theme", false);
        
        // Set the initial switch state based on current theme
        themeSwitch.setChecked(isDarkTheme);
        updateThemeInfo(isDarkTheme);
        
        themeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("dark_theme", isChecked);
                editor.apply();
                
                updateThemeInfo(isChecked);
                applyTheme(isChecked);
            }
        });
        
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
    
    private void updateThemeInfo(boolean isDarkTheme) {
        if (isDarkTheme) {
            currentThemeText.setText(R.string.current_theme_dark);
            settingsLayout.setBackgroundColor(getResources().getColor(android.R.color.black, null));
            currentThemeText.setTextColor(getResources().getColor(android.R.color.white, null));
        } else {
            currentThemeText.setText(R.string.current_theme_light);
            settingsLayout.setBackgroundColor(getResources().getColor(android.R.color.white, null));
            currentThemeText.setTextColor(getResources().getColor(android.R.color.darker_gray, null));
        }
    }
    
    private void applyTheme(boolean isDarkTheme) {
        if (isDarkTheme) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
    }
} 