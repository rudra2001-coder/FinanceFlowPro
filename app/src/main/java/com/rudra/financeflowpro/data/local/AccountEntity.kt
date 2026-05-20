package com.rudra.financeflowpro.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "accounts")
data class AccountEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val type: String,
    val balance: Double,
    val currency: String,
    val color: String,
    val icon: String,
    val isDefault: Boolean,
    val isArchived: Boolean = false,
    val createdAt: Long,
    val updatedAt: Long
)
