package com.cmpmodular.socialSignIn

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.interop.LocalUIViewController
import social_sign_in_cmp.generated.resources.Res
import social_sign_in_cmp.generated.resources.ic_google
import cocoapods.GoogleSignIn.GIDSignIn
import cocoapods.GoogleSignIn.GIDSignInResult
import kotlinx.cinterop.ExperimentalForeignApi
import org.jetbrains.compose.resources.DrawableResource
import platform.Foundation.NSError
import platform.UIKit.UIViewController

@Composable
actual fun GoogleLoginButton(
    webClientId: String,
    backgroundColor: Color?,
    drawableResource: DrawableResource?,
    onResponse: (AuthResponse) -> Unit,
    modifier: Modifier
) {
    val uiViewController = LocalUIViewController.current

    GoogleButtonUI(
        backgroundColor = backgroundColor ?: Color.White,
        drawableResource = drawableResource?: Res.drawable.ic_google,
        modifier = modifier,
        onClick = { googleLogin(uiViewController, onResponse) }
    )
}

@OptIn(ExperimentalForeignApi::class)
private fun googleLogin(
    uiViewController: UIViewController,
    onLoggedIn: (AuthResponse) -> Unit
) {
    GIDSignIn.sharedInstance.signInWithPresentingViewController(uiViewController) { result, error ->
        when {
            result != null -> {
                result.user.refreshTokensIfNeededWithCompletion { user, nsError ->
                    user?.idToken?.let { token->
                        onLoggedIn(AuthResponse.Success(token.tokenString, user.accessToken.tokenString))
                    }
                }
            }
            error != null -> onLoggedIn(AuthResponse.Error(error.fullErrorMessage))
            else -> onLoggedIn(AuthResponse.Cancelled)
        }
    }
}

@OptIn(ExperimentalForeignApi::class)
private val GIDSignInResult.toGoogleAccount: GoogleAccount
    get() = GoogleAccount(
        idToken = user.idToken?.tokenString.orEmpty(),
        accessToken = user.accessToken.tokenString,
        profile = Profile(
            name = user.profile?.name.orEmpty(),
            familyName = user.profile?.familyName.orEmpty(),
            givenName = user.profile?.givenName.orEmpty(),
            email = user.profile?.email.orEmpty(),
            picture = user.profile?.imageURLWithDimension(100u)?.absoluteString
        ),
    )

private val NSError.fullErrorMessage: String
    get() {
        val underlyingErrors = underlyingErrors.joinToString(", ") { it.toString() }
        val recoveryOptions = localizedRecoveryOptions?.joinToString(", ") { it.toString() }

        return listOfNotNull(
            "code: $code",
            domain?.let { "domain: $domain" },
            "description: $localizedDescription",
            localizedFailureReason?.let { "reason: $localizedFailureReason" },
            localizedRecoverySuggestion?.let { "suggestion: $localizedRecoverySuggestion" },
            "underlyingErrors: $underlyingErrors",
            "recoveryOptions: $recoveryOptions".takeIf { recoveryOptions != null },
        ).joinToString("\n")
    }