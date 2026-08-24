package com.android.trippoint.ui.settings

import com.android.trippoint.authentication.domain.model.UserPreferences
import com.android.trippoint.core.common.UiEffect
import com.android.trippoint.core.common.UiIntent
import com.android.trippoint.core.common.UiState

class PreferencesContract {
    sealed class Intent : UiIntent {
        data class CurrencyChanged(val currency: String) : Intent()
        data class LanguageChanged(val language: String) : Intent()
        data class DateFormatChanged(val format: String) : Intent()
        data class UnitsChanged(val units: String) : Intent()
        data class ThemeChanged(val isDark: Boolean) : Intent()
        object SaveClicked : Intent()
    }

    data class State(
        val currency: String = "INR",
        val language: String = "en",
        val dateFormat: String = "DD/MM/YYYY",
        val units: String = "METRIC",
        val isDarkTheme: Boolean = false,
        val timezone: String = "Asia/Kolkata",
        val isLoading: Boolean = false,
        val isSuccess: Boolean = false,
        val error: String? = null
    ) : UiState

    sealed class Effect : UiEffect {
        object NavigateBack : Effect()
        data class ShowError(val message: String) : Effect()
    }
}
