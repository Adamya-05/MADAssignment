package com.adamya.unitconverter;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    String[] units = {"Feet", "Inches", "Centimeters", "Meters", "Yards"};
    Spinner fromSpinner, toSpinner;
    EditText inputValue;
    TextView resultText;
    Button swapButton;
    RecyclerView commonConversionsRecyclerView;
    ConversionAdapter conversionAdapter;
    List<ConversionAdapter.Conversion> commonConversions = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fromSpinner = findViewById(R.id.fromUnitSpinner);
        toSpinner = findViewById(R.id.toUnitSpinner);
        inputValue = findViewById(R.id.inputValue);
        resultText = findViewById(R.id.resultText);
        swapButton = findViewById(R.id.swapButton);
        commonConversionsRecyclerView = findViewById(R.id.commonConversionsRecyclerView);

        // Setup spinners
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, units);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        fromSpinner.setAdapter(adapter);
        toSpinner.setAdapter(adapter);

        // Set default selections
        fromSpinner.setSelection(3); // Meters
        toSpinner.setSelection(2);   // Centimeters

        // Setup RecyclerView
        commonConversionsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        conversionAdapter = new ConversionAdapter(commonConversions);
        commonConversionsRecyclerView.setAdapter(conversionAdapter);

        // Setup listeners
        inputValue.addTextChangedListener(textWatcher);
        fromSpinner.setOnItemSelectedListener(spinnerListener);
        toSpinner.setOnItemSelectedListener(spinnerListener);
        
        swapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int fromPosition = fromSpinner.getSelectedItemPosition();
                int toPosition = toSpinner.getSelectedItemPosition();
                
                fromSpinner.setSelection(toPosition);
                toSpinner.setSelection(fromPosition);
            }
        });

        // Initialize with a default value
        inputValue.setText("1");
    }

    private final TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void afterTextChanged(Editable s) { convert(); }
        @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
        @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
    };

    private final AdapterView.OnItemSelectedListener spinnerListener = new AdapterView.OnItemSelectedListener() {
        public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) { convert(); }
        public void onNothingSelected(AdapterView<?> parent) {}
    };

    private void convert() {
        String input = inputValue.getText().toString();
        if (input.isEmpty()) {
            resultText.setText("0");
            updateCommonConversions(0);
            return;
        }

        double value;
        try {
            value = Double.parseDouble(input);
        } catch (NumberFormatException e) {
            resultText.setText("Invalid input");
            return;
        }

        String fromUnit = fromSpinner.getSelectedItem().toString();
        String toUnit = toSpinner.getSelectedItem().toString();

        double valueInMeters = convertToMeters(value, fromUnit);
        double finalResult = convertFromMeters(valueInMeters, toUnit);

        resultText.setText(String.format("%.4f", finalResult));
        
        // Update common conversions based on current input value and from unit
        updateCommonConversions(value);
    }

    private void updateCommonConversions(double value) {
        if (value == 0) {
            commonConversions.clear();
            conversionAdapter.notifyDataSetChanged();
            return;
        }

        String fromUnit = fromSpinner.getSelectedItem().toString();
        double valueInMeters = convertToMeters(value, fromUnit);
        
        List<ConversionAdapter.Conversion> newConversions = new ArrayList<>();
        
        // Create common conversions for all other units
        for (String unit : units) {
            if (!unit.equals(fromUnit)) {
                double convertedValue = convertFromMeters(valueInMeters, unit);
                newConversions.add(new ConversionAdapter.Conversion(
                        value, fromUnit, convertedValue, unit));
            }
        }
        
        commonConversions.clear();
        commonConversions.addAll(newConversions);
        conversionAdapter.notifyDataSetChanged();
    }

    private double convertToMeters(double value, String unit) {
        switch (unit) {
            case "Feet": return value * 0.3048;
            case "Inches": return value * 0.0254;
            case "Centimeters": return value * 0.01;
            case "Yards": return value * 0.9144;
            default: return value; // Meters
        }
    }

    private double convertFromMeters(double meters, String unit) {
        switch (unit) {
            case "Feet": return meters / 0.3048;
            case "Inches": return meters / 0.0254;
            case "Centimeters": return meters / 0.01;
            case "Yards": return meters / 0.9144;
            default: return meters; // Meters
        }
    }
}
