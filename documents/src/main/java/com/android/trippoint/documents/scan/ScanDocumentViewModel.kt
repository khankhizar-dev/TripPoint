package com.android.trippoint.documents.scan

import androidx.lifecycle.viewModelScope
import com.android.trippoint.core.common.BaseViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ScanDocumentViewModel : BaseViewModel<
    ScanDocumentContract.State,
    ScanDocumentContract.Intent,
    ScanDocumentContract.Effect
>(
    ScanDocumentContract.State()
) {
    override fun onIntent(intent: ScanDocumentContract.Intent) {
        when (intent) {
            ScanDocumentContract.Intent.CaptureClicked -> captureDocument()
            is ScanDocumentContract.Intent.AutoCaptureToggled -> {
                setState { copy(isAutoCaptureEnabled = intent.enabled) }
            }
            ScanDocumentContract.Intent.BackClicked -> {
                sendEffect(ScanDocumentContract.Effect.NavigateBack)
            }
            ScanDocumentContract.Intent.FlashToggled -> {
                setState { copy(isFlashEnabled = !isFlashEnabled) }
            }
        }
    }

    private fun captureDocument() {
        viewModelScope.launch {
            setState { copy(isProcessing = true) }
            delay(1500) // Simulating capture/processing
            setState { copy(isProcessing = false) }
            sendEffect(ScanDocumentContract.Effect.DocumentCaptured("mock://document_uri"))
        }
    }
}
