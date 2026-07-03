package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "employees")
data class Employee(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val lastname: String,
    val cid: String,
    val phone: String,
    val pass: String,
    val dept: String,
    val role: String,
    val salaryUSD: Double,
    val shift: String,
    val vacationStatus: String, // "Activo", "Vacaciones", "Próximas"
    val q1: String, // "Pendiente", "Pagado"
    val q2: String  // "Procesando", "Pagado"
)
