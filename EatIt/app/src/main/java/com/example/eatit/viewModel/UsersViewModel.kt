package com.example.eatit.viewModel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.eatit.data.UsersRepository
import com.example.eatit.model.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UsersViewModel @Inject constructor(private val repository: UsersRepository) : ViewModel() {
    private var _userPosition = mutableStateOf("")
    private var _user: User? = null
    val user
        get() = _user
    val userPosition
        get() = _userPosition

    fun addNewUser(user: User) = viewModelScope.launch {
        repository.insertNewUser(user)
    }

    suspend fun getUser(): User {
        return repository.getUser()
    }

    suspend fun getPosition(): String {
        return repository.getPosition()
    }

    fun setPosition(position: String) {
        _userPosition.value = position
        repository.setPosition(position)
    }

    fun setUser(user: User) {
        _user = user
    }
}