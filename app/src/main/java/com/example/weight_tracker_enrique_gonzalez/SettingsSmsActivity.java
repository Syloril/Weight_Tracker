package com.example.weight_tracker_enrique_gonzalez;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class SettingsSmsActivity extends AppCompatActivity {

    private static final int SMS_PERMISSION_CODE = 100;

    private TextView statusTextView;
    private Button requestPermissionButton;
    private Button disablePermissionButton;
    private Button homeButton; // Declared the home button

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Link this Java class to your XML layout file (e.g., activity_settings_sms.xml)
        setContentView(R.layout.settings); // Make sure this is the correct XML file name

        statusTextView = findViewById(R.id.statusTextView);
        requestPermissionButton = findViewById(R.id.requestPermissionButton);
        disablePermissionButton = findViewById(R.id.disablePermissionButton);
        homeButton = findViewById(R.id.button_home_from_sms_settings); // Found the home button by its ID

        // Set up click listeners for SMS permission buttons
        requestPermissionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAndRequestSmsPermission();
            }
        });

        disablePermissionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(SettingsSmsActivity.this, "Please disable SMS permission from device settings if needed.", Toast.LENGTH_LONG).show();
                updatePermissionStatus();
            }
        });

        // Set the click listener for the Return to Home button
        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create an Intent to navigate back to MainActivity (your home screen)
                Intent intent = new Intent(SettingsSmsActivity.this, MainActivity.class);
                // These flags ensure that the MainActivity becomes the top-most activity
                // and clears any other activities from the stack.
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish(); // Close the current SettingsSmsActivity
            }
        });

        // Initial check and update of permission status when the activity starts
        updatePermissionStatus();
    }

    // This method checks if SMS permission is granted and requests it if not
    private void checkAndRequestSmsPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED) {
            // Permission is already granted
            Toast.makeText(this, "SMS permission already granted.", Toast.LENGTH_SHORT).show();
            updatePermissionStatus();
            sendTestSms(); // Send a test SMS if permission is already there
        } else {
            // Permission is not granted, request it
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, SMS_PERMISSION_CODE);
        }
    }

    // This callback is triggered after the user responds to the permission request dialog
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == SMS_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted by the user
                Toast.makeText(this, "SMS permission granted!", Toast.LENGTH_SHORT).show();
                sendTestSms(); // Send a test SMS
            } else {
                // Permission denied by the user
                Toast.makeText(this, "SMS permission denied.", Toast.LENGTH_SHORT).show();
            }
            updatePermissionStatus(); // Always update UI after permission result
        }
    }

    // Updates the TextView and button visibility based on current SMS permission status
    private void updatePermissionStatus() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED) {
            statusTextView.setText("Status: SMS permission GRANTED.");
            requestPermissionButton.setVisibility(View.GONE); // Hide request button
            disablePermissionButton.setVisibility(View.VISIBLE); // Show disable button
        } else {
            statusTextView.setText("Status: SMS permission DENIED or not requested.");
            requestPermissionButton.setVisibility(View.VISIBLE); // Show request button
            disablePermissionButton.setVisibility(View.GONE); // Hide disable button
        }
    }

    // Method to send a test SMS message
    private void sendTestSms() {
        String phoneNumber = "5551234567"; // Replace with a real phone number for testing
        String message = "Hello from your Weight Tracker app! This is a test SMS.";

        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNumber, null, message, null, null);
            Toast.makeText(this, "Test SMS sent to " + phoneNumber, Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Toast.makeText(this, "Failed to send SMS: " + e.getMessage(), Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }
}