package com.android.trippoint.authentication.register

import androidx.compose.ui.graphics.Color

enum class PasswordStrength(val labelResId: Int, val color: Color, val progress: Float) {
    EMPTY(com.android.trippoint.authentication.R.string.auth_register_password_empty, Color.Transparent, 0f),
    WEAK(com.android.trippoint.authentication.R.string.auth_register_password_weak, Color(0xFFEF4444), 0.33f),
    MEDIUM(com.android.trippoint.authentication.R.string.auth_register_password_medium, Color(0xFFF59E0B), 0.66f),
    STRONG(com.android.trippoint.authentication.R.string.auth_register_password_strong, Color(0xFF10B981), 1f);

    companion object {
        fun calculate(password: String): PasswordStrength {
            if (password.isEmpty()) return EMPTY
            
            var score = 0
            if (password.length >= 8) score++
            if (password.any { it.isDigit() }) score++
            if (password.any { !it.isLetterOrDigit() }) score++
            if (password.any { it.isUpperCase() } && password.any { it.isLowerCase() }) score++

            return when {
                score <= 1 -> WEAK
                score <= 3 -> MEDIUM
                else -> STRONG
            }
        }
    }
}
