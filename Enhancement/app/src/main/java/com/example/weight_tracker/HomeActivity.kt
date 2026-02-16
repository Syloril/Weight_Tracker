package com.example.weight_tracker

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val listView = findViewById<ListView>(R.id.weightListView)
        val goalDisplay = findViewById<TextView>(R.id.goalDisplay)
        val btnAdd = findViewById<Button>(R.id.btnAddWeight)
        val btnSetGoal = findViewById<Button>(R.id.btnSetGoal)
        val smsCheckBox = findViewById<CheckBox>(R.id.smsCheckBox)
        val btnLogOut = findViewById<Button>(R.id.btnLogOut)

        val db = AppDatabase.getDatabase(this)
        val prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE)

        // Identify the current user
        val currentUser = prefs.getString("lastLoggedInUser", "default_user") ?: "default_user"
        val goalKey = "goal_weight_$currentUser" // Unique key for this user's goal

        // Load User-Specific Goal and SMS Preference
        goalDisplay.text = "Goal: ${prefs.getFloat(goalKey, 0f)} lbs"
        smsCheckBox.isChecked = prefs.getBoolean("sms_enabled", false)

        // Observe Database with Filter
        // We call a filtered query
        db.weightDao().getWeightsForUser(currentUser).observe(this) { weightList ->
            val displayStrings = weightList.map {
                "${it.weightValue} ${it.unit} - ${formatDate(it.date)}"
            }
            val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, displayStrings)
            listView.adapter = adapter

            listView.setOnItemLongClickListener { _, _, position, _ ->
                val entryToDelete = weightList[position]
                lifecycleScope.launch(Dispatchers.IO) {
                    db.weightDao().delete(entryToDelete)
                }
                Toast.makeText(this, "Deleted entry", Toast.LENGTH_SHORT).show()
                true
            }
        }

        // Set Goal Button
        btnSetGoal.setOnClickListener {
            val input = EditText(this)
            input.inputType = android.text.InputType.TYPE_CLASS_NUMBER or android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL
            AlertDialog.Builder(this)
                .setTitle("Enter Goal Weight (lbs)")
                .setView(input)
                .setPositiveButton("Save") { _, _ ->
                    val goal = input.text.toString().toFloatOrNull() ?: 0f
                    // Save to the user-specific key
                    prefs.edit().putFloat(goalKey, goal).apply()
                    goalDisplay.text = "Goal: $goal lbs"
                }
                .show()
        }

        smsCheckBox.setOnCheckedChangeListener { _, isChecked ->
            prefs.edit().putBoolean("sms_enabled", isChecked).apply()
        }

        btnLogOut.setOnClickListener {
            prefs.edit().putBoolean("isLoggedIn", false).apply()
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }

        btnAdd.setOnClickListener {
            startActivity(Intent(this, AddWeightActivity::class.java))
        }
    }

    private fun formatDate(time: Long): String {
        val date = java.util.Date(time)
        val sdf = java.text.SimpleDateFormat("MMM dd, yyyy", java.util.Locale.US)
        // Force the formatter to use Local time so it matches the adjusted timestamp
        sdf.timeZone = java.util.TimeZone.getDefault()
        return sdf.format(date)
    }
}