package com.android.trippoint.core.network

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

class BudgetRemoteDataSource(
    private val api: TripPointApi
) {
    private val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()

    // 1. Get Budget
    suspend fun getBudget(tripId: String): BudgetDto? {
        val query = """
            query GetBudget(${'$'}tripId: ID!) {
              budget(tripId: ${'$'}tripId) {
                id tripId totalAmount currency locked createdBy createdAt updatedAt
              }
            }
        """.trimIndent()
        val variables = mapOf("tripId" to tripId)
        val request = GraphQlRequest(query = query, variables = variables)
        val response = api.postGraphQl(request)
        val data = response.body()?.data?.get("budget") ?: return null
        return moshi.adapter(BudgetDto::class.java).fromJsonValue(data)
    }

    // 2. Create Budget
    suspend fun createBudget(tripId: String, input: CreateBudgetInput): BudgetDto? {
        val query = """
            mutation CreateBudget(${'$'}tripId: ID!, ${'$'}input: CreateBudgetInput!) {
              createBudget(tripId: ${'$'}tripId, input: ${'$'}input) {
                id tripId totalAmount currency locked createdBy createdAt updatedAt
              }
            }
        """.trimIndent()
        val variables = mapOf("tripId" to tripId, "input" to input)
        val request = GraphQlRequest(query = query, variables = variables)
        val response = api.postGraphQl(request)
        val data = response.body()?.data?.get("createBudget") ?: return null
        return moshi.adapter(BudgetDto::class.java).fromJsonValue(data)
    }

    // 3. Update Budget
    suspend fun updateBudget(tripId: String, input: UpdateBudgetInput): BudgetDto? {
        val query = """
            mutation UpdateBudget(${'$'}tripId: ID!, ${'$'}input: UpdateBudgetInput!) {
              updateBudget(tripId: ${'$'}tripId, input: ${'$'}input) {
                id tripId totalAmount currency locked createdBy createdAt updatedAt
              }
            }
        """.trimIndent()
        val variables = mapOf("tripId" to tripId, "input" to input)
        val request = GraphQlRequest(query = query, variables = variables)
        val response = api.postGraphQl(request)
        val data = response.body()?.data?.get("updateBudget") ?: return null
        return moshi.adapter(BudgetDto::class.java).fromJsonValue(data)
    }

    // 4. Get Budget Summary
    suspend fun getBudgetSummary(tripId: String): BudgetSummaryDto? {
        val query = """
            query GetBudgetSummary(${'$'}tripId: ID!) {
              budgetSummary(tripId: ${'$'}tripId) {
                budget {
                  id tripId totalAmount currency locked createdBy createdAt updatedAt
                }
                spentAmount remainingAmount expenseCount
                categoryBreakdown { category amount }
              }
            }
        """.trimIndent()
        val variables = mapOf("tripId" to tripId)
        val request = GraphQlRequest(query = query, variables = variables)
        val response = api.postGraphQl(request)
        val data = response.body()?.data?.get("budgetSummary") ?: return null
        return moshi.adapter(BudgetSummaryDto::class.java).fromJsonValue(data)
    }

    // 5. Get Budget Overview
    suspend fun getBudgetOverview(tripId: String): BudgetOverviewDto? {
        val query = """
            query GetBudgetOverview(${'$'}tripId: ID!) {
              budgetOverview(tripId: ${'$'}tripId) {
                budget spent remaining percentageUsed expenseCount
                categoryBreakdown { category amount percentage }
              }
            }
        """.trimIndent()
        val variables = mapOf("tripId" to tripId)
        val request = GraphQlRequest(query = query, variables = variables)
        val response = api.postGraphQl(request)
        val data = response.body()?.data?.get("budgetOverview") ?: return null
        return moshi.adapter(BudgetOverviewDto::class.java).fromJsonValue(data)
    }

    // 6. Get Expenses
    suspend fun getExpenses(tripId: String, filter: ExpenseFilterInput? = null): List<ExpenseDto> {
        val query = """
            query GetExpenses(${'$'}tripId: ID!, ${'$'}filter: ExpenseFilterInput) {
              expenses(tripId: ${'$'}tripId, filter: ${'$'}filter) {
                id tripId budgetId bookingId category title description amount
                currency exchangeRate convertedAmount expenseDate paymentMethod
                paidBy createdBy recurring recurrenceRule archived createdAt updatedAt
              }
            }
        """.trimIndent()
        val variables = mutableMapOf<String, Any>("tripId" to tripId)
        filter?.let { variables["filter"] = it }
        val request = GraphQlRequest(query = query, variables = variables)
        val response = api.postGraphQl(request)
        val data = response.body()?.data?.get("expenses") as? List<*> ?: return emptyList()
        val adapter = moshi.adapter(ExpenseDto::class.java)
        return data.mapNotNull { adapter.fromJsonValue(it) }
    }

    // 7. Get Single Expense
    suspend fun getExpense(tripId: String, expenseId: String): ExpenseDto? {
        val query = """
            query GetExpense(${'$'}tripId: ID!, ${'$'}expenseId: ID!) {
              expense(tripId: ${'$'}tripId, expenseId: ${'$'}expenseId) {
                id tripId budgetId bookingId category title description amount
                currency exchangeRate convertedAmount expenseDate paymentMethod
                paidBy createdBy recurring recurrenceRule archived createdAt updatedAt
              }
            }
        """.trimIndent()
        val variables = mapOf("tripId" to tripId, "expenseId" to expenseId)
        val request = GraphQlRequest(query = query, variables = variables)
        val response = api.postGraphQl(request)
        val data = response.body()?.data?.get("expense") ?: return null
        return moshi.adapter(ExpenseDto::class.java).fromJsonValue(data)
    }

    // 8. Create Expense
    suspend fun createExpense(tripId: String, input: CreateExpenseInput): ExpenseDto? {
        val query = """
            mutation CreateExpense(${'$'}tripId: ID!, ${'$'}input: CreateExpenseInput!) {
              createExpense(tripId: ${'$'}tripId, input: ${'$'}input) {
                id tripId budgetId bookingId category title description amount
                currency exchangeRate convertedAmount expenseDate paymentMethod
                paidBy createdBy recurring recurrenceRule archived createdAt updatedAt
              }
            }
        """.trimIndent()
        val variables = mapOf("tripId" to tripId, "input" to input)
        val request = GraphQlRequest(query = query, variables = variables)
        val response = api.postGraphQl(request)
        val data = response.body()?.data?.get("createExpense") ?: return null
        return moshi.adapter(ExpenseDto::class.java).fromJsonValue(data)
    }

    // 9. Update Expense
    suspend fun updateExpense(tripId: String, expenseId: String, input: UpdateExpenseInput): ExpenseDto? {
        val query = """
            mutation UpdateExpense(${'$'}tripId: ID!, ${'$'}expenseId: ID!, ${'$'}input: UpdateExpenseInput!) {
              updateExpense(tripId: ${'$'}tripId, expenseId: ${'$'}expenseId, input: ${'$'}input) {
                id tripId budgetId bookingId category title description amount
                currency exchangeRate convertedAmount expenseDate paymentMethod
                paidBy createdBy recurring recurrenceRule archived createdAt updatedAt
              }
            }
        """.trimIndent()
        val variables = mapOf("tripId" to tripId, "expenseId" to expenseId, "input" to input)
        val request = GraphQlRequest(query = query, variables = variables)
        val response = api.postGraphQl(request)
        val data = response.body()?.data?.get("updateExpense") ?: return null
        return moshi.adapter(ExpenseDto::class.java).fromJsonValue(data)
    }

    // 10. Archive Expense
    suspend fun archiveExpense(tripId: String, expenseId: String): ExpenseDto? {
        val query = """
            mutation ArchiveExpense(${'$'}tripId: ID!, ${'$'}expenseId: ID!) {
              archiveExpense(tripId: ${'$'}tripId, expenseId: ${'$'}expenseId) {
                id title archived updatedAt
              }
            }
        """.trimIndent()
        val variables = mapOf("tripId" to tripId, "expenseId" to expenseId)
        val request = GraphQlRequest(query = query, variables = variables)
        val response = api.postGraphQl(request)
        val data = response.body()?.data?.get("archiveExpense") ?: return null
        return moshi.adapter(ExpenseDto::class.java).fromJsonValue(data)
    }

    // 11. Daily Expense Report
    suspend fun getDailyExpenseReport(tripId: String, fromDate: String, toDate: String): List<DailyExpenseReportDto> {
        val query = """
            query GetDailyExpenseReport(${'$'}tripId: ID!, ${'$'}fromDate: String!, ${'$'}toDate: String!) {
              dailyExpenseReport(tripId: ${'$'}tripId, fromDate: ${'$'}fromDate, toDate: ${'$'}toDate) {
                date amount expenseCount
              }
            }
        """.trimIndent()
        val variables = mapOf("tripId" to tripId, "fromDate" to fromDate, "toDate" to toDate)
        val request = GraphQlRequest(query = query, variables = variables)
        val response = api.postGraphQl(request)
        val data = response.body()?.data?.get("dailyExpenseReport") as? List<*> ?: return emptyList()
        val adapter = moshi.adapter(DailyExpenseReportDto::class.java)
        return data.mapNotNull { adapter.fromJsonValue(it) }
    }

    // 12. Category Expense Report
    suspend fun getCategoryExpenseReport(
        tripId: String, 
        fromDate: String, 
        toDate: String
    ): List<CategoryExpenseReportDto> {
        val query = """
            query GetCategoryExpenseReport(${'$'}tripId: ID!, ${'$'}fromDate: String!, ${'$'}toDate: String!) {
              categoryExpenseReport(tripId: ${'$'}tripId, fromDate: ${'$'}fromDate, toDate: ${'$'}toDate) {
                category amount percentage expenseCount
              }
            }
        """.trimIndent()
        val variables = mapOf("tripId" to tripId, "fromDate" to fromDate, "toDate" to toDate)
        val request = GraphQlRequest(query = query, variables = variables)
        val response = api.postGraphQl(request)
        val data = response.body()?.data?.get("categoryExpenseReport") as? List<*> ?: return emptyList()
        val adapter = moshi.adapter(CategoryExpenseReportDto::class.java)
        return data.mapNotNull { adapter.fromJsonValue(it) }
    }

    // 13. Settlement Summary
    suspend fun getSettlementSummary(tripId: String): SettlementSummaryDto? {
        val query = """
            query GetSettlementSummary(${'$'}tripId: ID!) {
              settlementSummary(tripId: ${'$'}tripId) {
                totalExpense memberCount equalShare
                members { userId paidAmount shareAmount balance }
                settlements { fromUserId toUserId amount }
              }
            }
        """.trimIndent()
        val variables = mapOf("tripId" to tripId)
        val request = GraphQlRequest(query = query, variables = variables)
        val response = api.postGraphQl(request)
        val data = response.body()?.data?.get("settlementSummary") ?: return null
        return moshi.adapter(SettlementSummaryDto::class.java).fromJsonValue(data)
    }

    // Legacy/Dummy to keep compatibility until fully migrated
    suspend fun getBudgets(): List<BudgetDto> = emptyList()
}

data class BudgetDto(
    val id: String,
    val tripId: String,
    val totalAmount: Double,
    val currency: String,
    val locked: Boolean,
    val createdBy: String,
    val createdAt: String,
    val updatedAt: String
)

data class BudgetSummaryDto(
    val budget: BudgetDto,
    val spentAmount: Double,
    val remainingAmount: Double,
    val expenseCount: Int,
    val categoryBreakdown: List<CategoryBreakdownDto>
)

data class BudgetOverviewDto(
    val budget: Double,
    val spent: Double,
    val remaining: Double,
    val percentageUsed: Double,
    val expenseCount: Int,
    val categoryBreakdown: List<CategoryBreakdownOverviewDto>
)

data class CategoryBreakdownDto(
    val category: String,
    val amount: Double
)

data class CategoryBreakdownOverviewDto(
    val category: String,
    val amount: Double,
    val percentage: Double
)

data class ExpenseDto(
    val id: String,
    val tripId: String,
    val budgetId: String,
    val bookingId: String?,
    val category: String,
    val title: String,
    val description: String,
    val amount: Double,
    val currency: String,
    val exchangeRate: Double?,
    val convertedAmount: Double?,
    val expenseDate: String,
    val paymentMethod: String?,
    val paidBy: String?,
    val createdBy: String,
    val recurring: Boolean,
    val recurrenceRule: String?,
    val archived: Boolean,
    val createdAt: String,
    val updatedAt: String
)

data class DailyExpenseReportDto(
    val date: String,
    val amount: Double,
    val expenseCount: Int
)

data class CategoryExpenseReportDto(
    val category: String,
    val amount: Double,
    val percentage: Double,
    val expenseCount: Int
)

data class SettlementSummaryDto(
    val totalExpense: Double,
    val memberCount: Int,
    val equalShare: Double,
    val members: List<SettlementMemberDto>,
    val settlements: List<SettlementActionDto>
)

data class SettlementMemberDto(
    val userId: String,
    val paidAmount: Double,
    val shareAmount: Double,
    val balance: Double
)

data class SettlementActionDto(
    val fromUserId: String,
    val toUserId: String,
    val amount: Double
)

data class CreateBudgetInput(
    val totalAmount: Double,
    val currency: String,
    val title: String? = null
)

data class UpdateBudgetInput(
    val totalAmount: Double? = null,
    val currency: String? = null
)

data class CreateExpenseInput(
    val category: String,
    val title: String,
    val description: String,
    val amount: Double,
    val currency: String,
    val expenseDate: String,
    val paymentMethod: String?,
    val paidBy: String,
    val recurring: Boolean,
    val recurrenceRule: String?,
    val bookingId: String? = null
)

data class UpdateExpenseInput(
    val category: String? = null,
    val title: String? = null,
    val description: String? = null,
    val amount: Double? = null,
    val currency: String? = null,
    val expenseDate: String? = null,
    val paymentMethod: String? = null,
    val paidBy: String? = null,
    val recurring: Boolean? = null,
    val recurrenceRule: String? = null
)

data class ExpenseFilterInput(
    val search: String? = null,
    val category: String? = null,
    val minAmount: Double? = null,
    val maxAmount: Double? = null,
    val fromDate: String? = null,
    val toDate: String? = null,
    val includeArchived: Boolean? = null
)
