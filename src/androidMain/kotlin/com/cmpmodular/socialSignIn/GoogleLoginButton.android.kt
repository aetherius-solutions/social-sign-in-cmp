package com.cmpmodular.socialSignIn

import android.app.Activity
import android.content.IntentSender
import androidx.activity.compose.LocalActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import cmpmodular.socialsignin.generated.resources.Res
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.common.api.ApiException
import org.jetbrains.compose.resources.DrawableResource
import cmpmodular.socialsignin.generated.resources.ic_google

@Composable
actual fun GoogleLoginButton(
    webClientId: String,
    backgroundColor: Color?,
    drawableResource: DrawableResource?,
    onResponse: (AuthResponse) -> Unit,
    modifier: Modifier
) {
    val activity = LocalActivity.current ?: return
    val oneTapClient = Identity.getSignInClient(activity)

    val oneTapLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartIntentSenderForResult()
    ) { result ->
        try {
            val credential = oneTapClient.getSignInCredentialFromIntent(result.data)
            val idToken = credential.googleIdToken
            when {
                !idToken.isNullOrEmpty() -> {
                    onResponse.invoke(AuthResponse.Success(idToken))
                }
                else -> {
                    onResponse.invoke(AuthResponse.Error("Token empty"))
                }
            }
        } catch (e: ApiException) {
            if (result.resultCode == Activity.RESULT_CANCELED) {
                onResponse.invoke(AuthResponse.Cancelled)
            } else {
                onResponse.invoke(AuthResponse.Error(e.fullErrorMessage))
            }
        }
    }

    GoogleButtonUI(
        backgroundColor = backgroundColor ?: Color.White,
        drawableResource = drawableResource ?: Res.drawable.ic_google,
        modifier = modifier,
        onClick = {
            val signInRequest = BeginSignInRequest
                .builder()
                .setGoogleIdTokenRequestOptions(
                    BeginSignInRequest.GoogleIdTokenRequestOptions
                        .builder()
                        .setSupported(true)
                        .setServerClientId(webClientId)
                        .setFilterByAuthorizedAccounts(false)
                        .build()
                )
                .build()

            oneTapClient
                .beginSignIn(signInRequest)
                .addOnSuccessListener(activity) { result ->
                    try {
                        oneTapLauncher.launch(
                            IntentSenderRequest.Builder(result.pendingIntent.intentSender).build()
                        )
                    } catch (e: IntentSender.SendIntentException) {
                        onResponse.invoke(AuthResponse.Error("Sign-in failed"))
                    }
                }
                .addOnFailureListener(activity) { e ->
                    onResponse.invoke(AuthResponse.Error("Sign-in failed"))
                }
        }
    )
}

private val ApiException.fullErrorMessage: String
    get() {
        return listOfNotNull(
            "code: $statusCode",
            message?.let { "message: $message" },
            "localizedMessage: $localizedMessage",
            "status: $status",
        ).joinToString("\n")
    }