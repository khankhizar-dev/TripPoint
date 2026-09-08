package com.android.trippoint.budget.data.repository

import com.android.trippoint.budget.domain.model.Budget
import com.android.trippoint.budget.domain.model.Expense
import com.android.trippoint.budget.domain.repository.BudgetRepository
import com.android.trippoint.core.common.model.InvitationStatus
import com.android.trippoint.core.common.model.TravelerRole
import com.android.trippoint.core.common.model.TripMember
import com.android.trippoint.core.database.preferences.PreferencesManager
import com.android.trippoint.core.network.BudgetDto
import com.android.trippoint.core.network.BudgetOverviewDto
import com.android.trippoint.core.network.BudgetRemoteDataSource
import com.android.trippoint.core.network.BudgetSummaryDto
import com.android.trippoint.core.network.CreateBudgetInput
import com.android.trippoint.core.network.CreateExpenseInput
import com.android.trippoint.core.network.DailyExpenseReportDto
import com.android.trippoint.core.network.CategoryExpenseReportDto
import com.android.trippoint.core.network.ExpenseDto
import com.android.trippoint.core.network.ExpenseFilterInput
import com.android.trippoint.core.network.SettlementSummaryDto
import com.android.trippoint.core.network.TripMemberDto
import com.android.trippoint.core.network.TripRemoteDataSource
import com.android.trippoint.core.network.UpdateBudgetInput
import com.android.trippoint.core.network.UpdateExpenseInput

class BudgetRepositoryImpl(
    private val remoteDataSource: BudgetRemoteDataSource,
    private val tripRemoteDataSource: TripRemoteDataSource,
    private val preferencesManager: PreferencesManager
) : BudgetRepository {

    override suspend fun getBudget(tripId: String): Result<Budget> {
        return try {
            val dto = remoteDataSource.getBudget(tripId)
            if (dto != null) {
                Result.success(dto.toDomain())
            } else {
                Result.failure(Exception("Budget not found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun createBudget(
        tripId: String,
        totalAmount: Double,
        currency: String
    ): Result<Budget> {
        return try {
            val input = CreateBudgetInput(totalAmount = totalAmount, currency = currency)
            val dto = remoteDataSource.createBudget(tripId, input)
            if (dto != null) {
                Result.success(dto.toDomain())
            } else {
                Result.failure(Exception("Failed to create budget"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateBudget(
        tripId: String,
        totalAmount: Double?,
        currency: String?
    ): Result<Budget> {
        return try {
            val input = UpdateBudgetInput(totalAmount = totalAmount, currency = currency)
            val dto = remoteDataSource.updateBudget(tripId, input)
            if (dto != null) {
                Result.success(dto.toDomain())
            } else {
                Result.failure(Exception("Failed to update budget"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getBudgetSummary(tripId: String): Result<BudgetSummaryDto> {
        return try {
            val dto = remoteDataSource.getBudgetSummary(tripId)
            if (dto != null) Result.success(dto)
            else Result.failure(Exception("Failed to get budget summary"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getBudgetOverview(tripId: String): Result<BudgetOverviewDto> {
        return try {
            val dto = remoteDataSource.getBudgetOverview(tripId)
            if (dto != null) Result.success(dto)
            else Result.failure(Exception("Failed to get budget overview"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getExpenses(tripId: String, filter: ExpenseFilterInput?): Result<List<Expense>> {
        return try {
            val dtos = remoteDataSource.getExpenses(tripId, filter)
            Result.success(dtos.map { it.toDomain() })
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getExpense(tripId: String, expenseId: String): Result<Expense> {
        return try {
            val dto = remoteDataSource.getExpense(tripId, expenseId)
            if (dto != null) Result.success(dto.toDomain())
            else Result.failure(Exception("Expense not found"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun createExpense(tripId: String, input: CreateExpenseInput): Result<Expense> {
        return try {
            val dto = remoteDataSource.createExpense(tripId, input)
            if (dto != null) Result.success(dto.toDomain())
            else Result.failure(Exception("Failed to create expense"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateExpense(
        tripId: String,
        expenseId: String,
        input: UpdateExpenseInput
    ): Result<Expense> {
        return try {
            val dto = remoteDataSource.updateExpense(tripId, expenseId, input)
            if (dto != null) Result.success(dto.toDomain())
            else Result.failure(Exception("Failed to update expense"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun archiveExpense(tripId: String, expenseId: String): Result<Boolean> {
        return try {
            val dto = remoteDataSource.archiveExpense(tripId, expenseId)
            Result.success(dto != null && dto.archived)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getDailyExpenseReport(
        tripId: String,
        fromDate: String,
        toDate: String
    ): Result<List<DailyExpenseReportDto>> {
        return try {
            val dtos = remoteDataSource.getDailyExpenseReport(tripId, fromDate, toDate)
            Result.success(dtos)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getCategoryExpenseReport(
        tripId: String,
        fromDate: String,
        toDate: String
    ): Result<List<CategoryExpenseReportDto>> {
        return try {
            val dtos = remoteDataSource.getCategoryExpenseReport(tripId, fromDate, toDate)
            Result.success(dtos)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getSettlementSummary(tripId: String): Result<SettlementSummaryDto> {
        return try {
            val dto = remoteDataSource.getSettlementSummary(tripId)
            if (dto != null) Result.success(dto)
            else Result.failure(Exception("Failed to get settlement summary"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getTripMembers(tripId: String): Result<List<TripMember>> {
        return try {
            val dtos = tripRemoteDataSource.getTripMembers(tripId)
            Result.success(dtos.map { it.toDomain() })
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun getCurrentUserId(): String? = preferencesManager.getUserId()

    override suspend fun getBudgets(): Result<List<Budget>> {
        return try {
            val dtos = remoteDataSource.getBudgets()
            Result.success(dtos.map { it.toDomain() })
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun BudgetDto.toDomain(): Budget {
        return Budget(
            id = id,
            tripId = tripId,
            totalAmount = totalAmount,
            spentAmount = 0.0,
            currency = currency,
            title = "Budget", // Generic fallback instead of specific mock
            createdAt = createdAt,
            updatedAt = updatedAt
        )
    }

    private fun ExpenseDto.toDomain(): Expense {
        return Expense(
            id = id,
            budgetId = budgetId,
            amount = amount,
            currency = currency,
            category = category,
            date = expenseDate,
            description = description,
            paidBy = paidBy,
            createdBy = createdBy,
            createdAt = createdAt
        )
    }

    private fun TripMemberDto.toDomain(): TripMember {
        return TripMember(
            id = id,
            tripId = tripId,
            userId = userId,
            userName = null, // Backend field removed
            role = try { TravelerRole.valueOf(role) } catch (_: Exception) { TravelerRole.MEMBER },
            status = try { InvitationStatus.valueOf(status) } catch (_: Exception) { InvitationStatus.PENDING },
            invitedAt = invitedAt,
            joinedAt = joinedAt
        )
    }
}
