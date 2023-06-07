package com.example.eatit.viewModel

import android.location.Location
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
    private var _location = mutableStateOf(Location("MyLocationProvider"))
    val user
        get() = _user
    val userPosition
        get() = _userPosition

    val location
        get() = _location

    fun addNewUser(user: User) = viewModelScope.launch {
        repository.insertNewUser(user)
    }

    suspend fun getUser(): User {
        return repository.getUser()
    }
    suspend fun getUserById(userId: String): User {
        return repository.getUserById(userId)
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

    fun setLocation(location: Location) {
        _location.value = location
    }
}