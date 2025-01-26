package com.example.caloriesapp.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.caloriesapp.data.model.SavedProduct

@Dao
interface UserDao {

    @Insert
    fun insert(savedProduct: SavedProduct): Long

    @Query("SELECT * FROM saved_products")
    fun getAll(): List<SavedProduct> // Получение всех записей

    @Query("DELETE FROM saved_products WHERE id = :id")
    fun deleteById(id: Int) // Удаление по ID
}

