package com.adamya.Themesetting;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private EditText inputValue;
    private TextView resultText;
    private Spinner fromUnitSpinner;
    private Spinner toUnitSpinner;
    private Button swapButton;
    private Button settingsButton;
    private RecyclerView commonConversionsRecyclerView;
    
    private String[] lengthUnits = {"Meter", "Kilometer", "Centimeter", "Millimeter", "Inch", "Foot", "Yard", "Mile"};
    private DecimalFormat decimalFormat = new DecimalFormat("#.#####");
    private CommonConversionsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Apply theme before setting content view
        applyTheme();
        
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        // Initialize UI components
        inputValue = findViewById(R.id.inputValue);
        resultText = findViewById(R.id.resultText);
        fromUnitSpinner = findViewById(R.id.fromUnitSpinner);
        toUnitSpinner = findViewById(R.id.toUnitSpinner);
        swapButton = findViewById(R.id.swapButton);
        settingsButton = findViewById(R.id.settingsButton);
        commonConversionsRecyclerView = findViewById(R.id.commonConversionsRecyclerView);
        
        // Setup spinners
        ArrayAdapter<String> unitAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_dropdown_item, lengthUnits);
        fromUnitSpinner.setAdapter(unitAdapter);
        toUnitSpinner.setAdapter(unitAdapter);
        
        // Set default different units
        fromUnitSpinner.setSelection(0); // Meter
        toUnitSpinner.setSelection(3); // Millimeter
        
        // Setup common conversions recycler view
        commonConversionsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new CommonConversionsAdapter(generateCommonConversions());
        commonConversionsRecyclerView.setAdapter(adapter);
        
        // Set listeners
        inputValue.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                convert();
            }
        });
        
        fromUnitSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                convert();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
        
        toUnitSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                convert();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
        
        swapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int fromPosition = fromUnitSpinner.getSelectedItemPosition();
                int toPosition = toUnitSpinner.getSelectedItemPosition();
                
                fromUnitSpinner.setSelection(toPosition);
                toUnitSpinner.setSelection(fromPosition);
                convert();
            }
        });
        
        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(intent);
            }
        });
    }
    
    private void convert() {
        String input = inputValue.getText().toString().trim();
        if (input.isEmpty()) {
            resultText.setText("0");
            return;
        }
        
        try {
            double value = Double.parseDouble(input);
            String fromUnit = fromUnitSpinner.getSelectedItem().toString();
            String toUnit = toUnitSpinner.getSelectedItem().toString();
            
            // Convert to base unit (meters)
            double valueInMeters = convertToMeters(value, fromUnit);
            
            // Convert from meters to target unit
            double result = convertFromMeters(valueInMeters, toUnit);
            
            resultText.setText(decimalFormat.format(result));
        } catch (NumberFormatException e) {
            resultText.setText("0");
        }
    }
    
    private double convertToMeters(double value, String unit) {
        switch (unit) {
            case "Kilometer":
                return value * 1000;
            case "Centimeter":
                return value * 0.01;
            case "Millimeter":
                return value * 0.001;
            case "Inch":
                return value * 0.0254;
            case "Foot":
                return value * 0.3048;
            case "Yard":
                return value * 0.9144;
            case "Mile":
                return value * 1609.34;
            case "Meter":
            default:
                return value;
        }
    }
    
    private double convertFromMeters(double meters, String unit) {
        switch (unit) {
            case "Kilometer":
                return meters / 1000;
            case "Centimeter":
                return meters * 100;
            case "Millimeter":
                return meters * 1000;
            case "Inch":
                return meters / 0.0254;
            case "Foot":
                return meters / 0.3048;
            case "Yard":
                return meters / 0.9144;
            case "Mile":
                return meters / 1609.34;
            case "Meter":
            default:
                return meters;
        }
    }
    
    private List<CommonConversion> generateCommonConversions() {
        List<CommonConversion> conversions = new ArrayList<>();
        conversions.add(new CommonConversion("1 Meter", "100 Centimeters"));
        conversions.add(new CommonConversion("1 Meter", "1000 Millimeters"));
        conversions.add(new CommonConversion("1 Foot", "12 Inches"));
        conversions.add(new CommonConversion("1 Yard", "3 Feet"));
        conversions.add(new CommonConversion("1 Mile", "1.609 Kilometers"));
        conversions.add(new CommonConversion("1 Inch", "2.54 Centimeters"));
        return conversions;
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        // Reapply theme when returning to activity
        applyTheme();
    }
    
    private void applyTheme() {
        SharedPreferences sharedPreferences = getSharedPreferences("ThemePrefs", Context.MODE_PRIVATE);
        boolean isDarkTheme = sharedPreferences.getBoolean("dark_theme", false);
        
        if (isDarkTheme) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
    }
    
    public static class CommonConversion {
        private final String from;
        private final String to;
        
        public CommonConversion(String from, String to) {
            this.from = from;
            this.to = to;
        }
        
        public String getFrom() {
            return from;
        }
        
        public String getTo() {
            return to;
        }
    }
    
    public class CommonConversionsAdapter extends RecyclerView.Adapter<CommonConversionsAdapter.ViewHolder> {
        private final List<CommonConversion> conversions;
        
        public CommonConversionsAdapter(List<CommonConversion> conversions) {
            this.conversions = conversions;
        }
        
        @Override
        public ViewHolder onCreateViewHolder(android.view.ViewGroup parent, int viewType) {
            View view = getLayoutInflater().inflate(R.layout.item_common_conversion, parent, false);
            return new ViewHolder(view);
        }
        
        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            CommonConversion conversion = conversions.get(position);
            holder.fromText.setText(conversion.getFrom());
            holder.toText.setText(conversion.getTo());
        }
        
        @Override
        public int getItemCount() {
            return conversions.size();
        }
        
        public class ViewHolder extends RecyclerView.ViewHolder {
            private final TextView fromText;
            private final TextView toText;
            
            public ViewHolder(View itemView) {
                super(itemView);
                fromText = itemView.findViewById(R.id.fromText);
                toText = itemView.findViewById(R.id.toText);
            }
        }
    }
}