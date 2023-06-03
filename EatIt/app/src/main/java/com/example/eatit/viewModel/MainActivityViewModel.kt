package com.example.eatit.viewModel

import androidx.lifecycle.ViewModel
import com.example.eatit.utilities.Filters

/**
 * ViewModel for [com.example.eatit.MainActivity].
 */

class MainActivityViewModel : ViewModel() {
    var isSigningIn: Boolean = false
    var filters: Filters = Filters.default
}
