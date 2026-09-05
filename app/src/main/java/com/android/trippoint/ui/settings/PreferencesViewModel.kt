package com.android.trippoint.ui.settings

import androidx.lifecycle.viewModelScope
import com.android.trippoint.authentication.domain.repository.AuthRepository
import com.android.trippoint.core.common.BaseViewModel
import com.android.trippoint.core.network.UpdatePreferencesInput
import kotlinx.coroutines.launch

class PreferencesViewModel(
    private val authRepository: AuthRepository
) : BaseViewModel<PreferencesContract.State, PreferencesContract.Intent, PreferencesContract.Effect>(
    initialState = PreferencesContract.State()
) {
    init {
        loadPreferences()
    }

    private fun loadPreferences() {
        viewModelScope.launch {
            setState { copy(isLoading = true) }
            val result = authRepository.getMyPreferences()
            if (result.isSuccess) {
                val prefs = result.getOrNull()
                if (prefs != null) {
                    setState {
                        copy(
                            isLoading = false,
                            currency = prefs.currency ?: "INR",
                            language = prefs.language ?: "en",
                            dateFormat = prefs.dateFormat ?: "DD/MM/YYYY",
                            units = prefs.units ?: "METRIC",
                            isDarkTheme = prefs.theme == "DARK",
                            timezone = prefs.timezone ?: "Asia/Kolkata"
                        )
                    }
                } else {
                    setState { copy(isLoading = false) }
                }
            } else {
                setState { copy(isLoading = false, error = "Failed to load preferences") }
            }
        }
    }

    override fun onIntent(intent: PreferencesContract.Intent) {
        when (intent) {
            is PreferencesContract.Intent.CurrencyChanged -> {
                setState { copy(currency = intent.currency) }
                savePreferences()
            }
            is PreferencesContract.Intent.LanguageChanged -> {
                setState { copy(language = intent.language) }
                savePreferences()
            }
            is PreferencesContract.Intent.DateFormatChanged -> {
                setState { copy(dateFormat = intent.format) }
                savePreferences()
            }
            is PreferencesContract.Intent.UnitsChanged -> {
                setState { copy(units = intent.units) }
                savePreferences()
            }
            is PreferencesContract.Intent.ThemeChanged -> {
                setState { copy(isDarkTheme = intent.isDark) }
                savePreferences()
            }
            PreferencesContract.Intent.SaveClicked -> savePreferences()
        }
    }

    private fun savePreferences() {
        viewModelScope.launch {
            // No loading for auto-save to avoid flickering
            val currentState = uiState.value
            val input = UpdatePreferencesInput(
                currency = currentState.currency,
                language = currentState.language,
                dateFormat = currentState.dateFormat,
                units = currentState.units,
                theme = if (currentState.isDarkTheme) "DARK" else "LIGHT",
                timezone = currentState.timezone
            )
            
            authRepository.updatePreferences(input)
            // We don't navigate back on auto-save
        }
    }
}
