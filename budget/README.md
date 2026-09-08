# Budget & Expense Module

The `:budget` module handles all financial aspects of a trip, including budget planning, expense tracking, and spending analytics.

## 🛠 Features

### Budget Planning
- [x] **Global Budget List**: Aggregated view of all trip budgets with high-fidelity status badges.
- [x] **Budget Overview**: Real-time progress tracking of spent vs. total amounts with category-wise breakdowns.
- [x] **Status Lifecycle**: Track budgets through states like `ACTIVE`, `EXCEEDED`, and `COMPLETED`.

### Expense Management
- [x] **Expense Tracking**: Chronological list of all spending for a specific budget with detailed categorization.
- [x] **High-Fidelity Entry**: Manual entry form with localized category dropdowns and date pickers.
- [x] **AI Receipt Scanner**: animated camera interface for automatic data extraction from receipts (amount, date, merchant).

### Analytics & Reports
- [x] **Spending Trends**: Dynamic bar charts for analyzing daily and weekly spending habits.
- [x] **AI Insights**: Automated financial feedback and spending habit analysis.
- [x] **Financial Reports**: Export spending data to PDF and CSV formats for external sharing.

## 🏗 Architecture
- **MVI (Model-View-Intent)**: State management for all financial screens.
- **GraphQL Integration**: 100% migrated to real API calls for all budget, expense, and settlement data.
- **Repository Pattern**: Centralized data management via `BudgetRepository` with secure user UUID attribution.
- **Clean Code**: 100% compliant with **Detekt** and **Ktlint**.

## 🧪 Testing
- **100% Logic Coverage**: Comprehensive unit tests for all ViewModels and Repositories using MockK and Turbine.
- Run tests: `./gradlew :budget:testDebugUnitTest`
