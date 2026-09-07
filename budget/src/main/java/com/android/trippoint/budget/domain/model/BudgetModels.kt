package com.android.trippoint.budget.domain.model

import com.android.trippoint.core.designsystem.components.StatusVariant

data class Budget(
    val id: String,
    val tripId: String,
    val totalAmount: Double,
    val spentAmount: Double,
    val currency: String = "USD",
    val title: String,
    val categories: List<BudgetCategory> = emptyList(),
    val status: BudgetStatus = BudgetStatus.ACTIVE,
    val createdAt: String,
    val updatedAt: String
) {
    val remainingAmount: Double get() = totalAmount - spentAmount
    val progress: Float get() = if (totalAmount > 0) (spentAmount / totalAmount).toFloat().coerceIn(0f, 1f) else 0f
    
    val statusVariant: StatusVariant get() = when(status) {
        BudgetStatus.ACTIVE -> StatusVariant.Confirmed
        BudgetStatus.EXCEEDED -> StatusVariant.Cancelled
        BudgetStatus.COMPLETED -> StatusVariant.Confirmed
        BudgetStatus.DRAFT -> StatusVariant.Draft
    }
}

data class BudgetCategory(
    val id: String,
    val name: String,
    val allocatedAmount: Double,
    val spentAmount: Double,
    val icon: String? = null
) {
    val progress: Float 
        get() = if (allocatedAmount > 0) {
            (spentAmount / allocatedAmount).toFloat().coerceIn(0f, 1f)
        } else 0f
}

data class Expense(
    val id: String,
    val budgetId: String,
    val amount: Double,
    val currency: String,
    val category: String,
    val date: String,
    val description: String,
    val location: String? = null,
    val attachments: List<String> = emptyList(),
    val paymentMethod: String? = null,
    val createdAt: String
)

enum class BudgetStatus {
    ACTIVE,
    EXCEEDED,
    COMPLETED,
    DRAFT
}
