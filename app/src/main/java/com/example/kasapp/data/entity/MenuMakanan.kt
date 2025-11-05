package com.example.kasapp.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "menu_makanan")
data class MenuMakanan(
    @PrimaryKey(autoGenerate = true)
    val idMenu: Int = 0,
    val namaMenu: String,
    val hargaMenu: Double,
    val jenisMenu: String
)
