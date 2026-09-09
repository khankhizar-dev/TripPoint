package com.android.trippoint.documents.scan

import com.android.trippoint.core.common.UiEffect
import com.android.trippoint.core.common.UiIntent
import com.android.trippoint.core.common.UiState

class ScanDocumentContract {
    sealed class Intent : UiIntent {
        data class LoadTripId(val tripId: String) : Intent()
        object CaptureClicked : Intent()
        data class AutoCaptureToggled(val enabled: Boolean) : Intent()
        object BackClicked : Intent()
        object FlashToggled : Intent()
    }

    data class State(
        val tripId: String = "",
        val isAutoCaptureEnabled: Boolean = true,
        val isFlashEnabled: Boolean = false,
        val isProcessing: Boolean = false,
        val error: String? = null
    ) : UiState

    sealed class Effect : UiEffect {
        object NavigateBack : Effect()
        data class DocumentCaptured(val uri: String) : Effect()
    }
}
