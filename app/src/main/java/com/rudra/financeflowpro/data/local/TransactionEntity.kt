package com.rudra.financeflowpro.data.local

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "transactions",
    foreignKeys = [ForeignKey(
        entity = AccountEntity::class,
        parentColumns = ["id"],
        childColumns = ["accountId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index("accountId"), Index("date"), Index("category")]
)
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val accountId: Long,
    val type: String,
    val amount: Double,
    val category: String,
    val subcategory: String = "",
    val description: String = "",
    val date: Long,
    val time: Long = 0,
    val toAccountId: Long? = null,
    val isRecurring: Boolean = false,
    val recurringInterval: String? = null,
    val tags: String = "",
    val note: String = "",
    val createdAt: Long
)
