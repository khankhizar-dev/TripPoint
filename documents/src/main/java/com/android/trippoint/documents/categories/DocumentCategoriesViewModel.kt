package com.android.trippoint.documents.categories

import androidx.lifecycle.viewModelScope
import com.android.trippoint.core.common.BaseViewModel
import com.android.trippoint.core.designsystem.theme.TripPointIcons
import com.android.trippoint.documents.domain.model.CategoryInfo
import com.android.trippoint.documents.domain.model.DocumentType
import com.android.trippoint.documents.domain.repository.DocumentRepository
import com.android.trippoint.core.designsystem.R as designR
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
                        designR.string.documents_type_passport_visa,
                        docs.count { it.type == DocumentType.PASSPORT_VISA },
                        TripPointIcons.Docs
                    ),
                    CategoryInfo(
                        DocumentType.TICKETS_BOARDING,
                        designR.string.documents_type_tickets_boarding,
                        docs.count { it.type == DocumentType.TICKETS_BOARDING },
                        TripPointIcons.Flight
                    ),
                    CategoryInfo(
                        DocumentType.ID_PROOF,
                        designR.string.documents_type_id_proofs,
                        docs.count { it.type == DocumentType.ID_PROOF },
                        TripPointIcons.Profile
                    ),
                    CategoryInfo(
                        DocumentType.HOTEL_VOUCHER,
                        designR.string.documents_type_hotel_vouchers,
                        docs.count { it.type == DocumentType.HOTEL_VOUCHER },
                        TripPointIcons.Hotel
                    ),
                    CategoryInfo(
                        DocumentType.INSURANCE,
                        designR.string.documents_type_insurance,
                        docs.count { it.type == DocumentType.INSURANCE },
                        TripPointIcons.Lock
                    ),
                    CategoryInfo(
                        DocumentType.OTHER,
                        designR.string.documents_type_other,
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
