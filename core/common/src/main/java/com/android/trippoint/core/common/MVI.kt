package com.android.trippoint.core.common

interface UiState
interface UiIntent
interface UiEffect

interface MviViewModel<S : UiState, I : UiIntent, E : UiEffect> {
    val uiState: S
    fun onIntent(intent: I)
}
