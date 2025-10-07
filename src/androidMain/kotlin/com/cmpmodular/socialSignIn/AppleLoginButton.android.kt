package com.cmpmodular.socialSignIn

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import org.jetbrains.compose.resources.DrawableResource

@Composable
actual fun AppleLoginButton(
    backgroundColor: Color?,
    drawableResource: DrawableResource?,
    modifier: Modifier,
    onResponse: (AuthResponse) -> Unit
) {
    // Apple Sign-In is not supported on Android devices.
}