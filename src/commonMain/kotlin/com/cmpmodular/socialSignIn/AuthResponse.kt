package com.cmpmodular.socialSignIn

sealed interface AuthResponse {
    data class Success(val idToken: String, val accessToken: String?) : AuthResponse
    data class Error(val message: String) : AuthResponse
    data object Cancelled : AuthResponse
}