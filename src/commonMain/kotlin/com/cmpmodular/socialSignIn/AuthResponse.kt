package com.cmpmodular.socialSignIn

sealed interface AuthResponse {
    data class Success(val token: String) : AuthResponse
    data class Error(val message: String) : AuthResponse
    data object Cancelled : AuthResponse
}