package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "novelties")
data class Novelty(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val employeeCid: String,
    val message: String,
    val timestamp: Long = System.currentTimeMillis()
)
