//package com.cmpmodular.socialSignIn
//
//import android.app.Activity
//import android.app.Activity.RESULT_CANCELED
//import android.content.IntentSender
//import androidx.activity.compose.rememberLauncherForActivityResult
//import androidx.activity.result.IntentSenderRequest
//import androidx.activity.result.contract.ActivityResultContracts
//import androidx.compose.runtime.Composable
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.platform.LocalContext
//import com.google.android.gms.auth.api.identity.BeginSignInRequest
//import com.google.android.gms.auth.api.identity.Identity
//import com.google.android.gms.common.api.ApiException
//import kmp_app_template.composeapp.generated.resources.Res
//import kmp_app_template.composeapp.generated.resources.google_web_client_id
//import org.jetbrains.compose.resources.stringResource
//
//@Composable
//internal actual fun GoogleLoginButton(
//    onResponse: (AuthResponse) -> Unit,
//    modifier: Modifier
//) {
//    val activity = LocalContext.current as Activity
//    val oneTapClient = Identity.getSignInClient(LocalContext.current)
//    val webClientId = stringResource(Res.string.google_web_client_id)
//    val oneTapLauncher = rememberLauncherForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result ->
//        try {
//                val credential = oneTapClient.getSignInCredentialFromIntent(result.data)
//                val idToken = credential.googleIdToken
//                when {
//                    !idToken.isNullOrEmpty() -> {
//                        onResponse.invoke(AuthResponse.Success(idToken))
//                    }
//
//                    else -> {
//                        onResponse.invoke(AuthResponse.Error("Token empty"))
//                    }
//                }
//            } catch (e: ApiException) {
//                if (result.resultCode == RESULT_CANCELED) {
//                    onResponse.invoke(AuthResponse.Cancelled)
//                } else {
//                    onResponse.invoke(AuthResponse.Error(e.fullErrorMessage))
//                }
//            }
//        }
//
//    GoogleButtonUI(
//        modifier = modifier,
//        onClick = {
//            val signInRequest = BeginSignInRequest
//                .builder()
//                .setGoogleIdTokenRequestOptions(
//                    BeginSignInRequest.GoogleIdTokenRequestOptions
//                        .builder()
//                        .setSupported(true)
//                        .setServerClientId(webClientId)
//                        .setFilterByAuthorizedAccounts(false)
//                        .build()
//                )
//                .build()
//            oneTapClient
//                .beginSignIn(signInRequest)
//                .addOnSuccessListener(activity) { result ->
//                    try {
//                        oneTapLauncher.launch(
//                            IntentSenderRequest
//                                .Builder(result.pendingIntent.intentSender)
//                                .build()
//                        )
//                    } catch (e: IntentSender.SendIntentException) {
//                        println("aleksa exception: ${e.message}")
//                    }
//                }
//                .addOnFailureListener(activity) { e ->
//                }
//        },
//    )
//}
//
//private val ApiException.fullErrorMessage: String
//    get() {
//        return listOfNotNull(
//            "code: $statusCode",
//            message?.let { "message: $message" },
//            "localizedMessage: $localizedMessage",
//            "status: $status",
//        ).joinToString("\n")
//    }
//
//@Composable
//internal actual fun AppleLoginButton(
//    modifier: Modifier,
//    onResponse: (AuthResponse) -> Unit
//) {
//}