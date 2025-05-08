package com.vincent.jetmp3.ui.viewmodels

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vincent.jetmp3.data.repository.AuthRepository
import com.vincent.jetmp3.domain.models.request.LoginRequest
import com.vincent.jetmp3.domain.models.request.SignupRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
	private val authRepository: AuthRepository
) : ViewModel() {

	private val _uiState: MutableStateFlow<AuthState> = MutableStateFlow(AuthState.Ready)
	val uiState = _uiState.asStateFlow()

	private val _isLoggingIn: MutableState<Boolean> = mutableStateOf(true)
	val isLoggingIn: MutableState<Boolean> = _isLoggingIn

	private val _email: MutableState<String> = mutableStateOf("")
	val email: MutableState<String> = _email

	private val _password: MutableState<String> = mutableStateOf("")
	val password: MutableState<String> = _password

	private val _username: MutableState<String> = mutableStateOf("")
	val username: MutableState<String> = _username

	val errorMessage = authRepository.errorMessage

	val authValid = authRepository.authValid

	private fun login() {
		if (_email.value.isEmpty() || _password.value.isEmpty()) return
		viewModelScope.launch {
			_uiState.value = AuthState.Fetching
			try {
				authRepository.login(LoginRequest(_email.value, _password.value))
			} catch (e: Exception) {
				Log.d("AuthViewModel", "Auth Error")
			} finally {
				_uiState.value = AuthState.Ready
			}
		}
	}

	private fun register() {
		if (_email.value.isEmpty() || _password.value.isEmpty() || _username.value.isEmpty()) return
		viewModelScope.launch {
			_uiState.value = AuthState.Fetching
			try {
				authRepository.register(SignupRequest(_username.value, _email.value, _password.value))
			} catch (e: Exception) {
				Log.d("AuthViewModel", "Auth Error")
			} finally {
				_uiState.value = AuthState.Ready
			}
		}
	}

	fun handle() {
		if (_isLoggingIn.value) {
			login()
		} else {
			register()
		}
	}

	fun clearErrors() {
		authRepository.clearErrors()
	}

	sealed class AuthState {
		data object Fetching : AuthState()
		data object Ready : AuthState()
	}
}

