package com.example.weight_tracker

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "weight_table")
data class WeightEntry(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val userEmail: String,
    val weightValue: Double,
    val unit: String,
    val date: Long
)