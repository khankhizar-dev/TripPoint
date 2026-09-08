package com.android.trippoint.documents.categories

import androidx.lifecycle.viewModelScope
import com.android.trippoint.core.common.BaseViewModel
import com.android.trippoint.core.designsystem.theme.TripPointIcons
import com.android.trippoint.documents.domain.model.CategoryInfo
import com.android.trippoint.documents.domain.model.DocumentType
import com.android.trippoint.documents.domain.repository.DocumentRepository
import kotlinx.coroutines.launch

class DocumentCategoriesViewModel(
    private val repository: DocumentRepository
) : BaseViewModel<
    DocumentCategoriesContract.State,
    DocumentCategoriesContract.Intent,
    DocumentCategoriesContract.Effect
>(
    DocumentCategoriesContract.State()
) {
    override fun onIntent(intent: DocumentCategoriesContract.Intent) {
        when (intent) {
            is DocumentCategoriesContract.Intent.LoadCategories -> loadCategories(intent.tripId)
            is DocumentCategoriesContract.Intent.CategoryClicked -> {
                sendEffect(DocumentCategoriesContract.Effect.NavigateToDocumentsByType(intent.type))
            }
            DocumentCategoriesContract.Intent.BackClicked -> sendEffect(DocumentCategoriesContract.Effect.NavigateBack)
        }
    }

    private fun loadCategories(tripId: String) {
        viewModelScope.launch {
            setState { copy(isLoading = true, tripId = tripId) }
            val result = repository.getDocuments(tripId)
            if (result.isSuccess) {
                val docs = result.getOrDefault(emptyList())
                val categories = listOf(
                    CategoryInfo(
                        DocumentType.PASSPORT_VISA,
                        "Passport & Visa",
                        docs.count { it.type == DocumentType.PASSPORT_VISA },
                        TripPointIcons.Docs
                    ),
                    CategoryInfo(
                        DocumentType.TICKETS_BOARDING,
                        "Tickets & Boarding",
                        docs.count { it.type == DocumentType.TICKETS_BOARDING },
                        TripPointIcons.Flight
                    ),
                    CategoryInfo(
                        DocumentType.ID_PROOF,
                        "ID Proofs",
                        docs.count { it.type == DocumentType.ID_PROOF },
                        TripPointIcons.Profile
                    ),
                    CategoryInfo(
                        DocumentType.HOTEL_VOUCHER,
                        "Hotel Vouchers",
                        docs.count { it.type == DocumentType.HOTEL_VOUCHER },
                        TripPointIcons.Hotel
                    ),
                    CategoryInfo(
                        DocumentType.INSURANCE,
                        "Insurance",
                        docs.count { it.type == DocumentType.INSURANCE },
                        TripPointIcons.Lock
                    ),
                    CategoryInfo(
                        DocumentType.OTHER,
                        "Other Documents",
                        docs.count { it.type == DocumentType.OTHER },
                        TripPointIcons.More
                    )
                )
                setState { copy(isLoading = false, categories = categories, error = null) }
            } else {
                setState { copy(isLoading = false, error = result.exceptionOrNull()?.message) }
            }
        }
    }
}
