package com.aethersentinel.showcase.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

/**
 * Small shared event bus used for one-shot UI events (snackbars) that
 * originate from any tab but need to surface on the root Scaffold's
 * SnackbarHost.
 */
class AppEventsViewModel : ViewModel() {

    private val _snackbarEvents = MutableSharedFlow<String>(extraBufferCapacity = 4)
    val snackbarEvents: SharedFlow<String> = _snackbarEvents.asSharedFlow()

    fun showSnackbar(message: String) {
        _snackbarEvents.tryEmit(message)
    }
}
