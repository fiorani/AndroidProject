package com.example.eatit.viewModel

import android.location.Location
import android.net.Uri
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.eatit.data.UsersRepository
import com.example.eatit.model.Filter
import com.example.eatit.model.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UsersViewModel @Inject constructor(private val repository: UsersRepository) : ViewModel() {
    private var _user: User = User()
    private var _location = mutableStateOf(Location("MyLocationProvider"))
    private var _position = mutableStateOf("")
    private var _filter = Filter()
    val user
        get() = _user
    val filter
        get() = _filter
    val location
        get() = _location
    val position
        get() = _position

    fun addNewUser(user: User) = viewModelScope.launch {
        repository.insertNewUser(user)
    }

    suspend fun getUser(): User {
        val user = repository.getUser()
        _position = mutableStateOf(user.position)
        return user
    }

    fun setPosition(position: String) {
        _position.value = position
        _user.position = position
        setUser(_user)
    }

    fun setUser(user: User) {
        repository.setUser(user)
    }

    fun changePsw() {
        viewModelScope.launch {
            repository.setPassword()
        }
    }

    fun setLocation(location: Location) {
        _location.value = location
    }

    suspend fun uploadPhoto(uri: Uri): Uri {
        return repository.uploadPhoto(uri)
    }

    fun deleteUser() {
        repository.deleteUser()
    }
}