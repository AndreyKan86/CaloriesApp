package com.example.caloriesapp.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "saved_products")
data class SavedProduct(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String, // Название продукта
    val bgu: String, // БЖУ
    val kcal: String, // Ккал
    val weight: String // Вес
)
