package com.example.caloriesapp.data.repository

import android.content.Context
import com.example.caloriesapp.data.database.AppDatabase
import com.example.caloriesapp.data.dao.UserDao
import com.example.caloriesapp.data.model.SavedProduct

class SavedProductRepository(context: Context) {

    private val savedProductDao: UserDao

    init {
        // Получаем базу данных и DAO
        val db = AppDatabase.getDatabase(context)
        savedProductDao = db.userDao()
    }

    suspend fun insert(savedProduct: SavedProduct) {
        savedProductDao.insert(savedProduct)
    }

    suspend fun getAll(): List<SavedProduct> {
        return savedProductDao.getAll()
    }

    suspend fun deleteById(id: Int) {
        savedProductDao.deleteById(id)
    }
}