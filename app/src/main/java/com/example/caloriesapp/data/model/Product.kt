package com.example.caloriesapp.data.model

data class Product (
    val id: String, //Id продукта
    val name: String, // Имя продукта
    val bgu: String, // БЖУ, записано в виде "X,Y,Z" на 100 грамм
    val kcal: String // Килокалории на 100 грамм
)