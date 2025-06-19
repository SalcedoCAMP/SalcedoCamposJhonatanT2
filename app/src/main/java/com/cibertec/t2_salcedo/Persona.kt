package com.cibertec.t2_salcedo

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "persons")
data class Persona(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val fullName: String,
    val documentId: String,
    val celular: String,
    val direccion: String,
    val districto: String,
    val civilStatus: String?
)