package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "system_configs")
data class SystemConfig(
    @PrimaryKey val key: String,
    val value: String
)
