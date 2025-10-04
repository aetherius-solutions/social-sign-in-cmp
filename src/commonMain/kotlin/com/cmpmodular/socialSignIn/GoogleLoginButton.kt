package com.cmpmodular.socialSignIn

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

import org.jetbrains.compose.resources.DrawableResource

@Composable
expect fun GoogleLoginButton(
    webClientId: String,
    backgroundColor: Color? = null,
    drawableResource: DrawableResource? = null,
    onResponse: (AuthResponse) -> Unit,
    modifier: Modifier = Modifier
)

@Composable
expect fun AppleLoginButton(
    backgroundColor: Color? = null,
    drawableResource: DrawableResource? = null,
    modifier: Modifier = Modifier,
    onResponse: (AuthResponse) -> Unit
)
