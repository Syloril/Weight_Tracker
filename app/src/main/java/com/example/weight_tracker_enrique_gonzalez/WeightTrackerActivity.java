package com.example.weight_tracker_enrique_gonzalez;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class WeightTrackerActivity extends AppCompatActivity {

    private SharedPreferences sharedPreferences;

    // Day 1
    private EditText weightEditTextDay1;
    private Button addDay1Button;
    private Button deleteDay1Button;

    // Day 2
    private EditText weightEditTextDay2;
    private Button addDay2Button;
    private Button deleteDay2Button;

    // Day 3
    private EditText weightEditTextDay3;
    private Button addDay3Button;
    private Button deleteDay3Button;

    // Day 4
    private EditText weightEditTextDay4;
    private Button addDay4Button;
    private Button deleteDay4Button;

    // Day 5
    private EditText weightEditTextDay5;
    private Button addDay5Button;
    private Button deleteDay5Button;

    // Day 6
    private EditText weightEditTextDay6;
    private Button addDay6Button;
    private Button deleteDay6Button;

    // Day 7
    private EditText weightEditTextDay7;
    private Button addDay7Button;
    private Button deleteDay7Button;

    // Declare Home and Add Goal buttons
    private Button homeButton;
    private Button addGoalButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.weight_database);

        sharedPreferences = getSharedPreferences("MyWeightTracker", MODE_PRIVATE);

        // Find Home and Add Goal buttons
        homeButton = findViewById(R.id.button_home);
        addGoalButton = findViewById(R.id.button_add_goal);

        // Find views for Day 1
        weightEditTextDay1 = findViewById(R.id.edit_text_weight_day1);
        addDay1Button = findViewById(R.id.button_add_day1);
        deleteDay1Button = findViewById(R.id.button_delete_day1);

        // Find views for Day 2
        weightEditTextDay2 = findViewById(R.id.edit_text_weight_day2);
        addDay2Button = findViewById(R.id.button_add_day2);
        deleteDay2Button = findViewById(R.id.button_delete_day2);

        // Find views for Day 3
        weightEditTextDay3 = findViewById(R.id.edit_text_weight_day3);
        addDay3Button = findViewById(R.id.button_add_day3);
        deleteDay3Button = findViewById(R.id.button_delete_day3);

        // Find views for Day 4
        weightEditTextDay4 = findViewById(R.id.edit_text_weight_day4);
        addDay4Button = findViewById(R.id.button_add_day4);
        deleteDay4Button = findViewById(R.id.button_delete_day4);

        // Find views for Day 5
        weightEditTextDay5 = findViewById(R.id.edit_text_weight_day5);
        addDay5Button = findViewById(R.id.button_add_day5);
        deleteDay5Button = findViewById(R.id.button_delete_day5);

        // Find views for Day 6
        weightEditTextDay6 = findViewById(R.id.edit_text_weight_day6);
        addDay6Button = findViewById(R.id.button_add_day6);
        deleteDay6Button = findViewById(R.id.button_delete_day6);

        // Find views for Day 7
        weightEditTextDay7 = findViewById(R.id.edit_text_weight_day7);
        addDay7Button = findViewById(R.id.button_add_day7);
        deleteDay7Button = findViewById(R.id.button_delete_day7);

        // Set the click listener for the Home button
        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(WeightTrackerActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        // Set the click listener for the Add Goal button
        addGoalButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(WeightTrackerActivity.this, GoalActivity.class);
                startActivity(intent);
            }
        });

        // Load data on start for all 7 days
        loadAllWeightData();

        // Set up click listeners for all daily buttons
        addDay1Button.setOnClickListener(v -> saveWeightData("day1", weightEditTextDay1));
        deleteDay1Button.setOnClickListener(v -> deleteWeightData("day1", weightEditTextDay1));

        addDay2Button.setOnClickListener(v -> saveWeightData("day2", weightEditTextDay2));
        deleteDay2Button.setOnClickListener(v -> deleteWeightData("day2", weightEditTextDay2));

        addDay3Button.setOnClickListener(v -> saveWeightData("day3", weightEditTextDay3));
        deleteDay3Button.setOnClickListener(v -> deleteWeightData("day3", weightEditTextDay3));

        addDay4Button.setOnClickListener(v -> saveWeightData("day4", weightEditTextDay4));
        deleteDay4Button.setOnClickListener(v -> deleteWeightData("day4", weightEditTextDay4));

        addDay5Button.setOnClickListener(v -> saveWeightData("day5", weightEditTextDay5));
        deleteDay5Button.setOnClickListener(v -> deleteWeightData("day5", weightEditTextDay5));

        addDay6Button.setOnClickListener(v -> saveWeightData("day6", weightEditTextDay6));
        deleteDay6Button.setOnClickListener(v -> deleteWeightData("day6", weightEditTextDay6));

        addDay7Button.setOnClickListener(v -> saveWeightData("day7", weightEditTextDay7));
        deleteDay7Button.setOnClickListener(v -> deleteWeightData("day7", weightEditTextDay7));
    }

    private void saveWeightData(String dayKey, EditText editText) {
        String weight = editText.getText().toString();
        if (weight.isEmpty()) {
            Toast.makeText(this, "Please enter a weight.", Toast.LENGTH_SHORT).show();
            return;
        }
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(dayKey + "_weight", weight);
        editor.apply();
        Toast.makeText(this, "Weight for " + dayKey + " saved!", Toast.LENGTH_SHORT).show();
    }

    private void deleteWeightData(String dayKey, EditText editText) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(dayKey + "_weight");
        editor.apply();
        editText.setText("");
        Toast.makeText(this, "Weight for " + dayKey + " deleted.", Toast.LENGTH_SHORT).show();
    }

    private void loadAllWeightData() {
        loadSingleWeightData("day1", weightEditTextDay1);
        loadSingleWeightData("day2", weightEditTextDay2);
        loadSingleWeightData("day3", weightEditTextDay3);
        loadSingleWeightData("day4", weightEditTextDay4);
        loadSingleWeightData("day5", weightEditTextDay5);
        loadSingleWeightData("day6", weightEditTextDay6);
        loadSingleWeightData("day7", weightEditTextDay7);
    }

    private void loadSingleWeightData(String dayKey, EditText editText) {
        String savedWeight = sharedPreferences.getString(dayKey + "_weight", "");
        editText.setText(savedWeight);
    }
}