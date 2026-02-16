package com.example.weight_tracker_enrique_gonzalez;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class GoalActivity extends AppCompatActivity {

    private SharedPreferences sharedPreferences;
    private EditText weightInput;
    private CalendarView calendarView;
    private Button submitButton;
    private Button cancelButton;

    private long selectedDateInMillis;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_weight); // Make sure this matches your XML file name

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences("MyWeightTracker", MODE_PRIVATE);

        // Find views by their IDs
        weightInput = findViewById(R.id.editTextNumberDecimal);
        calendarView = findViewById(R.id.calendarView);
        submitButton = findViewById(R.id.button4);
        cancelButton = findViewById(R.id.button5);

        // Set the default selected date to the current date
        selectedDateInMillis = calendarView.getDate();

        // Listen for date changes on the CalendarView
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
                // Get the new date in milliseconds
                Date selectedDate = new Date(year - 1900, month, dayOfMonth);
                selectedDateInMillis = selectedDate.getTime();
            }
        });

        // Set the OnClickListener for the Submit button
        submitButton.setOnClickListener(v -> {
            saveWeightAndDate();
        });

        // Set the OnClickListener for the Cancel button
        cancelButton.setOnClickListener(v -> {
            finish(); // Closes the current activity and returns to the previous one
        });
    }

    private void saveWeightAndDate() {
        String weight = weightInput.getText().toString();

        if (weight.isEmpty()) {
            Toast.makeText(this, "Please enter a weight.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Format the selected date to be used as a key
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String dateString = sdf.format(new Date(selectedDateInMillis));

        // Use a unique key that includes the date
        String dataKey = "weight_" + dateString;

        // Save the weight to SharedPreferences
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(dataKey, weight);
        editor.apply();

        Toast.makeText(this, "Weight for " + dateString + " saved: " + weight, Toast.LENGTH_LONG).show();
        finish(); // Go back to the previous screen after saving
    }
}