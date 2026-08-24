package com.android.trippoint.ui.settings

import com.android.trippoint.core.common.BaseViewModel

class SupportViewModel : BaseViewModel<SupportContract.State, SupportContract.Intent, SupportContract.Effect>(
    initialState = SupportContract.State()
) {
    override fun onIntent(intent: SupportContract.Intent) {
        // Handle support actions
    }
}
