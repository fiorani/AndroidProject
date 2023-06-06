package com.example.eatit.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.eatit.data.UsersRepository
import com.example.eatit.model.User
import com.google.firebase.firestore.DocumentSnapshot
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UsersViewModel @Inject constructor(private val repository: UsersRepository) : ViewModel() {
    private var _userPosition: String? = ""
    val userPosition
        get() = _userPosition

    fun addNewUser(user: User) = viewModelScope.launch {
        repository.insertNewUser(user)
    }

    fun getUser(): List<DocumentSnapshot> {
        return repository.getUser()
    }

    suspend fun getPosition(): String {
        return repository.getPosition()
    }

    fun setPosition(position: String) {
        repository.setPosition(position)
        _userPosition = position
    }
}