package com.example.weight_tracker

import android.os.Bundle
import android.telephony.SmsManager
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.material.datepicker.MaterialDatePicker
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AddWeightActivity : AppCompatActivity() {

    private var selectedDate: Long = System.currentTimeMillis()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_weight)

        val weightInput = findViewById<EditText>(R.id.weightInput)
        val radioLbs = findViewById<RadioButton>(R.id.radioLbs)
        val btnPickDate = findViewById<Button>(R.id.pickDateBtn)
        val btnSave = findViewById<Button>(R.id.saveBtn)
        val btnCancel = findViewById<Button>(R.id.btnCancel)

        val db = AppDatabase.getDatabase(this)
        val prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE)

        btnPickDate.setOnClickListener {
            val datePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Select date")
                .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                .build()

            datePicker.addOnPositiveButtonClickListener { selection ->
                // Get the time zone offset for your current location
                val timeZone = java.util.TimeZone.getDefault()
                val offset = timeZone.getOffset(selection)

                // Adjust the UTC selection to match your Local Time
                selectedDate = selection - offset

                btnPickDate.text = "Date Selected"
            }
            datePicker.show(supportFragmentManager, "DATE_PICKER")
        }

        btnCancel.setOnClickListener {
            finish()
        }

        btnSave.setOnClickListener {
            val weightStr = weightInput.text.toString()
            if (weightStr.isEmpty()) {
                Toast.makeText(this, "Please enter a weight", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val weightValue = weightStr.toDouble()
            val unit = if (radioLbs.isChecked) "lbs" else "kg"

            // Get the current user email for tagging the data
            val currentUser = prefs.getString("lastLoggedInUser", "") ?: ""

            lifecycleScope.launch(Dispatchers.IO) {
                // Save to Room - tagged with userEmail
                val entry = WeightEntry(
                    userEmail = currentUser,
                    weightValue = weightValue,
                    unit = unit,
                    date = selectedDate
                )
                db.weightDao().insert(entry)

                // Goal Check logic using user-specific goal key
                val goalKey = "goal_weight_$currentUser"
                val goal = prefs.getFloat(goalKey, 0f).toDouble()
                val isSmsOn = prefs.getBoolean("sms_enabled", false)
                val comparisonWeight = if (unit == "kg") weightValue * 2.20462 else weightValue

                if (comparisonWeight <= goal && goal > 0) {
                    if (isSmsOn) {
                        sendGoalSms(comparisonWeight, goal)
                    }
                    withContext(Dispatchers.Main) {
                        showGoalReachedModal(comparisonWeight, goal)
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@AddWeightActivity, "Entry Saved!", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                }
            }
        }
    }

    private fun showGoalReachedModal(current: Double, goal: Double) {
        AlertDialog.Builder(this)
            .setTitle("Goal Reached! 🎉")
            .setMessage("Congratulations! You weighed in at %.1f lbs, hitting your goal of %.1f lbs!".format(current, goal))
            .setCancelable(false)
            .setPositiveButton("Celebrate!") { _, _ ->
                finish()
            }
            .create()
            .show()
    }

    private fun sendGoalSms(current: Double, goal: Double) {
        try {
            val smsManager: SmsManager = getSystemService(SmsManager::class.java)
            val message = "Goal Reached! Current weight: %.1f lbs. Your goal was %.1f lbs.".format(current, goal)
            smsManager.sendTextMessage("5551234567", null, message, null, null)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}