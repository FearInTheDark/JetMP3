package com.vincent.jetmp3.ui.viewmodels

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(

) : ViewModel() {
    private val _isLoggingIn: MutableState<Boolean> = mutableStateOf(false)
    val isLoggingIn: MutableState<Boolean> = _isLoggingIn

    private val _email : MutableState<String> = mutableStateOf("")
    val email : MutableState<String> = _email

    private val _password : MutableState<String> = mutableStateOf("")
    val password : MutableState<String> = _password

    private val _username : MutableState<String> = mutableStateOf("")
    val username : MutableState<String> = _username

    fun setIsLoggingIn(value: Boolean) {
        _isLoggingIn.value = value
    }

    fun setEmail(value: String) {
        _email.value = value
    }

    fun setPassword(value: String) {
        _password.value = value
    }

    fun setUsername(value: String) {
        _username.value = value
    }

    fun clear() {
        _email.value = ""
        _password.value = ""
        _username.value = ""
    }

    fun login() {
        println("Logging in with email: ${_email.value} and password: ${_password.value}")
    }
}